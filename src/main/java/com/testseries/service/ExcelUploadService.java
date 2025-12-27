package com.testseries.service;

import com.testseries.model.Question;
import com.testseries.model.TestSeries;
import com.testseries.repository.QuestionRepository;
import com.testseries.repository.TestSeriesRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelUploadService {

    private final QuestionRepository questionRepository;
    private final TestSeriesRepository testSeriesRepository;
    private final com.testseries.repository.TopicRepository topicRepository;

    public int uploadQuestions(Long testSeriesId, MultipartFile file) throws IOException {
        TestSeries testSeries = testSeriesRepository.findById(testSeriesId)
                .orElseThrow(() -> new RuntimeException("Test Series not found"));

        List<Question> questions = parseExcelFile(file.getInputStream());

        for (Question q : questions) {
            q.setTestSeries(testSeries);
        }

        questionRepository.saveAll(questions);
        return questions.size();
    }

    public int uploadQuestionsForTopic(Long topicId, MultipartFile file) throws IOException {
        com.testseries.model.Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        List<Question> questions = parseExcelFile(file.getInputStream());

        for (Question q : questions) {
            q.setTopic(topic);
            q.setSubject(topic.getSubject());
        }

        questionRepository.saveAll(questions);
        return questions.size();
    }

    private List<Question> parseExcelFile(InputStream is) throws IOException {
        List<Question> questions = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        int rowNumber = 0;
        while (rows.hasNext()) {
            Row currentRow = rows.next();

            // Skip header
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }

            // Stop if row is empty
            if (isRowEmpty(currentRow))
                break;

            try {
                Question question = new Question();
                question.setQuestionText(getCellValue(currentRow, 0));
                question.setOptionA(getCellValue(currentRow, 1));
                question.setOptionB(getCellValue(currentRow, 2));
                question.setOptionC(getCellValue(currentRow, 3));
                question.setOptionD(getCellValue(currentRow, 4));
                question.setCorrectAnswer(getCellValue(currentRow, 5));
                question.setExplanation(getCellValue(currentRow, 6));

                // Marks
                String marksStr = getCellValue(currentRow, 7);
                question.setMarks(marksStr.isEmpty() ? 1 : Integer.parseInt(marksStr.split("\\.")[0]));

                String negMarksStr = getCellValue(currentRow, 8);
                question.setNegativeMarks(negMarksStr.isEmpty() ? 0.0 : Double.parseDouble(negMarksStr));

                // Difficulty (Optional - Defaults to Medium)
                String difficulty = getCellValue(currentRow, 9);
                if (difficulty == null || difficulty.trim().isEmpty()) {
                    question.setDifficulty("Medium");
                } else {
                    // Normalize input (e.g., "easy " -> "Easy")
                    String normalized = difficulty.trim().toLowerCase();
                    if (normalized.equals("easy"))
                        question.setDifficulty("Easy");
                    else if (normalized.equals("hard"))
                        question.setDifficulty("Hard");
                    else
                        question.setDifficulty("Medium");
                }

                questions.add(question);
            } catch (Exception e) {
                System.err.println("Error parsing row " + rowNumber + ": " + e.getMessage());
                // Continue to next row possibly, or throw? For now let's just log
            }
            rowNumber++;
        }

        workbook.close();
        return questions;
    }

    // Helper to get String value safely
    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null)
            return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null)
            return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }
}
