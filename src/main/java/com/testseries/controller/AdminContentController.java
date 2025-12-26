package com.testseries.controller;

import com.testseries.model.Course;
import com.testseries.model.Exam;
import com.testseries.model.Question;
import com.testseries.repository.CourseRepository;
import com.testseries.repository.ExamRepository;
import com.testseries.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/content")
@CrossOrigin(origins = "*")
public class AdminContentController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // --- Exams ---
    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examRepository.findAll());
    }

    @PostMapping("/exams")
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        return ResponseEntity.ok(examRepository.save(exam));
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) {
        examRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Exam deleted"));
    }

    // --- Courses ---
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseRepository.save(course));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Course deleted"));
    }

    // --- Question Bank ---
    @GetMapping("/questions/bank")
    public ResponseEntity<List<Question>> getBankQuestions() {
        // Return all questions for admin to see, or maybe just APPROVED if we want separate view
        // For now, let's return all non-TestSeries questions
        return ResponseEntity.ok(questionRepository.findByTestSeriesIsNull());
    }

    @PostMapping("/questions/bank")
    public ResponseEntity<Question> addQuestionToBank(@RequestBody Question question) {
        question.setTestSeries(null); 
        // Default to APPROVED for now when added by Admin directly
        if(question.getStatus() == null) {
            question.setStatus(com.testseries.model.QuestionStatus.APPROVED);
        }
        return ResponseEntity.ok(questionRepository.save(question));
    }

    @GetMapping("/questions/review")
    public ResponseEntity<List<Question>> getReviewQueue() {
        return ResponseEntity.ok(questionRepository.findByStatus(com.testseries.model.QuestionStatus.REVIEW_PENDING));
    }

    @PutMapping("/questions/{id}/status")
    public ResponseEntity<Question> updateQuestionStatus(@PathVariable Long id, @RequestParam com.testseries.model.QuestionStatus status) {
        return questionRepository.findById(id).map(q -> {
            q.setStatus(status);
            return ResponseEntity.ok(questionRepository.save(q));
        }).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Question deleted"));
    }
}
