package com.testseries.controller;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.testseries.model.Answer;
import com.testseries.model.ExamAttempt;
import com.testseries.model.Question;
import com.testseries.repository.AnswerRepository;
import com.testseries.repository.ExamAttemptRepository;
import com.testseries.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/exam/{examAttemptId}/pdf")
    public ResponseEntity<byte[]> generatePdfReport(@PathVariable Long examAttemptId) {
        try {
            // Fetch exam attempt
            ExamAttempt examAttempt = examAttemptRepository.findById(examAttemptId)
                    .orElseThrow(() -> new RuntimeException("Exam attempt not found"));

            // Fetch answers and questions
            List<Answer> answers = answerRepository.findByExamAttemptId(examAttemptId);
            List<Question> questions = questionRepository.findByTestSeriesId(examAttempt.getTestSeries().getId());

            // Create answer map for quick lookup
            Map<Long, Answer> answerMap = new HashMap<>();
            for (Answer answer : answers) {
                answerMap.put(answer.getQuestion().getId(), answer);
            }

            // Create PDF in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Colors
            DeviceRgb headerColor = new DeviceRgb(102, 126, 234);
            DeviceRgb correctColor = new DeviceRgb(76, 175, 80);
            DeviceRgb incorrectColor = new DeviceRgb(244, 67, 54);
            DeviceRgb unansweredColor = new DeviceRgb(158, 158, 158);

            // ========== TITLE ==========
            Paragraph title = new Paragraph("EXAM PERFORMANCE REPORT")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(headerColor)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // ========== STUDENT DETAILS ==========
            document.add(new Paragraph("Student Information")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(headerColor));

            Table studentTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}));
            studentTable.setWidth(UnitValue.createPercentValue(100));
            
            addInfoRow(studentTable, "Test Series:", examAttempt.getTestSeries().getTitle());
            addInfoRow(studentTable, "Student Name:", examAttempt.getUser().getName());
            addInfoRow(studentTable, "Email:", examAttempt.getUser().getEmail());
            
            if (examAttempt.getSubmittedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                addInfoRow(studentTable, "Submitted On:", examAttempt.getSubmittedAt().format(formatter));
            }
            
            document.add(studentTable);
            document.add(new Paragraph("\n"));

            // ========== OVERALL RESULTS ==========
            document.add(new Paragraph("Overall Results")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(headerColor));

            Table resultsTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}));
            resultsTable.setWidth(UnitValue.createPercentValue(100));

            resultsTable.addHeaderCell(createHeaderCell("Metric"));
            resultsTable.addHeaderCell(createHeaderCell("Value"));

            resultsTable.addCell("Score Obtained");
            resultsTable.addCell(examAttempt.getScore() + " / " + examAttempt.getTotalMarks());

            double percentage = (examAttempt.getScore() * 100.0) / examAttempt.getTotalMarks();
            resultsTable.addCell("Percentage");
            resultsTable.addCell(String.format("%.2f%%", percentage));

            resultsTable.addCell("Status");
            Cell statusCell = new Cell().add(new Paragraph(examAttempt.getIsPassed() ? "PASSED ✓" : "NOT PASSED ✗"));
            statusCell.setFontColor(examAttempt.getIsPassed() ? correctColor : incorrectColor);
            statusCell.setBold();
            resultsTable.addCell(statusCell);

            if (examAttempt.getTimeSpentSeconds() != null && examAttempt.getTimeSpentSeconds() > 0) {
                int minutes = examAttempt.getTimeSpentSeconds() / 60;
                int seconds = examAttempt.getTimeSpentSeconds() % 60;
                resultsTable.addCell("Time Spent");
                resultsTable.addCell(minutes + "m " + seconds + "s");
            }

            // Calculate statistics
            int correctCount = 0;
            int incorrectCount = 0;
            int unansweredCount = 0;

            for (Question question : questions) {
                Answer answer = answerMap.get(question.getId());
                if (answer == null || answer.getSelectedAnswer() == null || answer.getSelectedAnswer().isEmpty()) {
                    unansweredCount++;
                } else if (answer.getSelectedAnswer().equals(question.getCorrectAnswer())) {
                    correctCount++;
                } else {
                    incorrectCount++;
                }
            }

            resultsTable.addCell("Correct Answers");
            Cell correctCell = new Cell().add(new Paragraph(String.valueOf(correctCount)));
            correctCell.setFontColor(correctColor).setBold();
            resultsTable.addCell(correctCell);

            resultsTable.addCell("Incorrect Answers");
            Cell incorrectCell = new Cell().add(new Paragraph(String.valueOf(incorrectCount)));
            incorrectCell.setFontColor(incorrectColor).setBold();
            resultsTable.addCell(incorrectCell);

            resultsTable.addCell("Unanswered");
            Cell unansweredCell = new Cell().add(new Paragraph(String.valueOf(unansweredCount)));
            unansweredCell.setFontColor(unansweredColor);
            resultsTable.addCell(unansweredCell);

            document.add(resultsTable);
            document.add(new Paragraph("\n"));

            // ========== QUESTION-WISE ANALYSIS ==========
            document.add(new Paragraph("Question-Wise Analysis")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(headerColor));
            document.add(new Paragraph("\n"));

            int questionNumber = 1;
            for (Question question : questions) {
                Answer answer = answerMap.get(question.getId());
                
                // Question header
                boolean isCorrect = answer != null && answer.getSelectedAnswer() != null 
                        && answer.getSelectedAnswer().equals(question.getCorrectAnswer());
                boolean isUnanswered = answer == null || answer.getSelectedAnswer() == null 
                        || answer.getSelectedAnswer().isEmpty();

                DeviceRgb questionColor = isUnanswered ? unansweredColor : 
                                         (isCorrect ? correctColor : incorrectColor);
                String status = isUnanswered ? "UNANSWERED" : (isCorrect ? "CORRECT ✓" : "INCORRECT ✗");

                Paragraph questionHeader = new Paragraph("Question " + questionNumber)
                        .setFontSize(12)
                        .setBold()
                        .setFontColor(questionColor);
                document.add(questionHeader);

                // Question text
                document.add(new Paragraph(question.getQuestionText())
                        .setFontSize(11)
                        .setMarginLeft(10));

                // Options table
                Table optionsTable = new Table(UnitValue.createPercentArray(new float[]{1, 4, 1}));
                optionsTable.setWidth(UnitValue.createPercentValue(100));
                optionsTable.setMarginLeft(10);

                addOptionRow(optionsTable, "A", question.getOptionA(), 
                            question.getCorrectAnswer().equals("A"), 
                            answer != null && "A".equals(answer.getSelectedAnswer()));
                addOptionRow(optionsTable, "B", question.getOptionB(), 
                            question.getCorrectAnswer().equals("B"), 
                            answer != null && "B".equals(answer.getSelectedAnswer()));
                addOptionRow(optionsTable, "C", question.getOptionC(), 
                            question.getCorrectAnswer().equals("C"), 
                            answer != null && "C".equals(answer.getSelectedAnswer()));
                addOptionRow(optionsTable, "D", question.getOptionD(), 
                            question.getCorrectAnswer().equals("D"), 
                            answer != null && "D".equals(answer.getSelectedAnswer()));

                document.add(optionsTable);

                // Answer details
                Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}));
                detailsTable.setWidth(UnitValue.createPercentValue(100));
                detailsTable.setMarginLeft(10);

                detailsTable.addCell("Your Answer:");
                String yourAnswer = (answer != null && answer.getSelectedAnswer() != null) 
                        ? answer.getSelectedAnswer() : "Not Answered";
                Cell yourAnswerCell = new Cell().add(new Paragraph(yourAnswer));
                yourAnswerCell.setFontColor(isUnanswered ? unansweredColor : 
                                           (isCorrect ? correctColor : incorrectColor));
                detailsTable.addCell(yourAnswerCell);

                detailsTable.addCell("Correct Answer:");
                Cell correctAnswerCell = new Cell().add(new Paragraph(question.getCorrectAnswer()));
                correctAnswerCell.setFontColor(correctColor).setBold();
                detailsTable.addCell(correctAnswerCell);

                detailsTable.addCell("Status:");
                Cell statusDetailCell = new Cell().add(new Paragraph(status));
                statusDetailCell.setFontColor(questionColor).setBold();
                detailsTable.addCell(statusDetailCell);

                detailsTable.addCell("Marks:");
                int marksObtained = isCorrect ? question.getMarks() : 
                                   (isUnanswered ? 0 : -1); // Default negative marking is -1
                detailsTable.addCell(marksObtained + " / " + question.getMarks());

                document.add(detailsTable);
                document.add(new Paragraph("\n"));

                questionNumber++;
            }

            // ========== FOOTER ==========
            document.add(new Paragraph("This is an auto-generated report. For any queries, please contact support.")
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

            // Close document
            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "exam-report-" + examAttemptId + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void addInfoRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(value);
    }

    private Cell createHeaderCell(String text) {
        Cell cell = new Cell().add(new Paragraph(text).setBold());
        cell.setBackgroundColor(new DeviceRgb(102, 126, 234));
        cell.setFontColor(ColorConstants.WHITE);
        return cell;
    }

    private void addOptionRow(Table table, String option, String text, boolean isCorrect, boolean isSelected) {
        Cell optionCell = new Cell().add(new Paragraph(option).setBold());
        if (isCorrect) {
            optionCell.setBackgroundColor(new DeviceRgb(232, 245, 233));
            optionCell.setFontColor(new DeviceRgb(76, 175, 80));
        } else if (isSelected) {
            optionCell.setBackgroundColor(new DeviceRgb(255, 235, 238));
            optionCell.setFontColor(new DeviceRgb(244, 67, 54));
        }
        table.addCell(optionCell);

        Cell textCell = new Cell().add(new Paragraph(text));
        if (isCorrect) {
            textCell.setBackgroundColor(new DeviceRgb(232, 245, 233));
        } else if (isSelected) {
            textCell.setBackgroundColor(new DeviceRgb(255, 235, 238));
        }
        table.addCell(textCell);

        Cell statusCell = new Cell();
        if (isCorrect) {
            statusCell.add(new Paragraph("✓").setBold().setFontColor(new DeviceRgb(76, 175, 80)));
            statusCell.setBackgroundColor(new DeviceRgb(232, 245, 233));
        } else if (isSelected) {
            statusCell.add(new Paragraph("✗").setBold().setFontColor(new DeviceRgb(244, 67, 54)));
            statusCell.setBackgroundColor(new DeviceRgb(255, 235, 238));
        }
        table.addCell(statusCell);
    }
}
