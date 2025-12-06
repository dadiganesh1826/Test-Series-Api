package com.testseries.controller;

import com.testseries.dto.ExamResultResponse;
import com.testseries.dto.SubmitExamRequest;
import com.testseries.model.ExamAttempt;
import com.testseries.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Exam Management", description = "APIs for taking exams, submitting answers, and viewing results")
public class ExamController {
        private static final Logger log = LoggerFactory.getLogger(ExamController.class);
        private final ExamService examService;

        @Operation(summary = "Start a new exam", description = "Initiates a new exam attempt for a user on a specific test series. Creates an exam attempt record and returns the attempt details.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Exam started successfully", content = @Content(schema = @Schema(implementation = ExamAttempt.class))),
                        @ApiResponse(responseCode = "404", description = "User or test series not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
        })
        @PostMapping("/start")
        public ResponseEntity<ExamAttempt> startExam(
                        @Parameter(description = "ID of the user taking the exam", required = true) @RequestParam Long userId,
                        @Parameter(description = "ID of the test series to attempt", required = true) @RequestParam Long testSeriesId) {
                try {
                        log.info("Received request to start exam - userId: {}, testSeriesId: {}", userId, testSeriesId);
                        ExamAttempt examAttempt = examService.startExam(userId, testSeriesId);
                        log.info("Successfully started exam - examAttemptId: {}", examAttempt.getId());
                        return ResponseEntity.ok(examAttempt);
                } catch (Exception e) {
                        log.error("Error starting exam for userId: {}, testSeriesId: {}", userId, testSeriesId, e);
                        throw e;
                }
        }

        @Operation(summary = "Submit exam answers", description = "Submits all answers for an exam attempt and calculates the final score. Returns detailed results including score, pass/fail status, and answer breakdown.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Exam submitted successfully", content = @Content(schema = @Schema(implementation = ExamResultResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Exam attempt not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid submission data or exam already submitted")
        })
        @PostMapping("/submit")
        public ResponseEntity<ExamResultResponse> submitExam(
                        @Parameter(description = "Exam submission details including attempt ID and answers", required = true) @RequestBody SubmitExamRequest request) {
                ExamResultResponse result = examService.submitExam(request);
                return ResponseEntity.ok(result);
        }

        @Operation(summary = "Get user's exam history", description = "Retrieves all exam attempts made by a specific user, including completed and incomplete attempts")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved exam history", content = @Content(schema = @Schema(implementation = ExamAttempt.class))),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @GetMapping("/history")
        public ResponseEntity<List<ExamAttempt>> getExamHistory(
                        @Parameter(description = "ID of the user", required = true) @RequestParam Long userId) {
                List<ExamAttempt> history = examService.getUserExamHistory(userId);
                return ResponseEntity.ok(history);
        }

        @Operation(summary = "Get exam result details", description = "Retrieves detailed results for a specific exam attempt including score, answers, and explanations")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved exam result", content = @Content(schema = @Schema(implementation = ExamResultResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Exam attempt not found"),
                        @ApiResponse(responseCode = "403", description = "User not authorized to view this result")
        })
        @GetMapping("/result/{examAttemptId}")
        public ResponseEntity<ExamResultResponse> getExamResult(
                        @PathVariable Long examAttemptId,
                        @RequestParam Long userId) {
                ExamResultResponse result = examService.getExamResult(examAttemptId, userId);
                return ResponseEntity.ok(result);
        }

        @Operation(summary = "Auto-save exam progress", description = "Saves current exam progress without submitting")
        @PostMapping("/auto-save")
        public ResponseEntity<java.util.Map<String, String>> autoSaveExam(
                        @RequestBody com.testseries.dto.AutoSaveRequest request) {
                examService.autoSaveExam(request);
                return ResponseEntity.ok(java.util.Map.of(
                                "status", "saved",
                                "savedAt", java.time.LocalDateTime.now().toString()));
        }

        @Operation(summary = "Get time remaining", description = "Gets remaining time for an exam attempt")
        @GetMapping("/{examAttemptId}/time-remaining")
        public ResponseEntity<java.util.Map<String, Object>> getTimeRemaining(
                        @PathVariable Long examAttemptId) {
                java.util.Map<String, Object> timeInfo = examService.getTimeRemaining(examAttemptId);
                return ResponseEntity.ok(timeInfo);
        }
}
