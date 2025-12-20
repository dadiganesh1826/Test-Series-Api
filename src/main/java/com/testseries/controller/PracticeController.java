package com.testseries.controller;

import com.testseries.model.PracticeAnswer;
import com.testseries.model.PracticeAttempt;
import com.testseries.service.PracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/practice")
@CrossOrigin(origins = "*")
public class PracticeController {

    @Autowired
    private PracticeService practiceService;

    @PostMapping("/start/{testSeriesId}")
    public ResponseEntity<PracticeAttempt> startPractice(
            @PathVariable Long testSeriesId,
            @RequestParam Long userId) {
        PracticeAttempt attempt = practiceService.startPractice(userId, testSeriesId);
        return ResponseEntity.ok(attempt);
    }

    @PostMapping("/custom")
    public ResponseEntity<Map<String, Object>> startCustomPractice(@RequestBody com.testseries.dto.TopicPracticeDTO request) {
        return ResponseEntity.ok(practiceService.startCustomPractice(request));
    }

    @PostMapping("/{practiceAttemptId}/answer")
    public ResponseEntity<Map<String, Object>> submitAnswer(
            @PathVariable Long practiceAttemptId,
            @RequestBody Map<String, Object> answerData) {
        
        Long questionId = Long.valueOf(answerData.get("questionId").toString());
        String selectedAnswer = answerData.get("selectedAnswer").toString();
        Integer timeSpent = answerData.get("timeSpent") != null 
                ? Integer.valueOf(answerData.get("timeSpent").toString()) 
                : 0;

        Map<String, Object> feedback = practiceService.submitAnswer(
                practiceAttemptId, questionId, selectedAnswer, timeSpent);
        
        return ResponseEntity.ok(feedback);
    }

    @PostMapping("/{practiceAttemptId}/complete")
    public ResponseEntity<PracticeAttempt> completePractice(@PathVariable Long practiceAttemptId) {
        PracticeAttempt attempt = practiceService.completePractice(practiceAttemptId);
        return ResponseEntity.ok(attempt);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<PracticeAttempt>> getPracticeHistory(@PathVariable Long userId) {
        List<PracticeAttempt> history = practiceService.getUserPracticeHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{practiceAttemptId}")
    public ResponseEntity<PracticeAttempt> getPracticeAttempt(
            @PathVariable Long practiceAttemptId,
            @RequestParam Long userId) {
        PracticeAttempt attempt = practiceService.getPracticeAttempt(practiceAttemptId, userId);
        return ResponseEntity.ok(attempt);
    }

    @GetMapping("/{practiceAttemptId}/answers")
    public ResponseEntity<List<PracticeAnswer>> getPracticeAnswers(@PathVariable Long practiceAttemptId) {
        List<PracticeAnswer> answers = practiceService.getPracticeAnswers(practiceAttemptId);
        return ResponseEntity.ok(answers);
    }
}
