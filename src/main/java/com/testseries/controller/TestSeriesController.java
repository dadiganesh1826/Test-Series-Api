package com.testseries.controller;

import com.testseries.model.Question;
import com.testseries.model.TestSeries;
import com.testseries.service.TestSeriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-series")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Test Series Management", description = "APIs for managing test series and questions")
public class TestSeriesController {
        private final TestSeriesService testSeriesService;

        @Operation(summary = "Get all active test series", description = "Retrieves a list of all active test series available for users to take")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of test series", content = @Content(schema = @Schema(implementation = TestSeries.class)))
        })
        @GetMapping
        public ResponseEntity<List<TestSeries>> getAllTestSeries() {
                List<TestSeries> testSeries = testSeriesService.getAllActiveTestSeries();
                return ResponseEntity.ok(testSeries);
        }

        @Operation(summary = "Get test series by ID", description = "Retrieves detailed information about a specific test series including metadata")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved test series", content = @Content(schema = @Schema(implementation = TestSeries.class))),
                        @ApiResponse(responseCode = "404", description = "Test series not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<TestSeries> getTestSeriesById(
                        @Parameter(description = "ID of the test series to retrieve", required = true) @PathVariable Long id) {
                TestSeries testSeries = testSeriesService.getTestSeriesById(id);
                return ResponseEntity.ok(testSeries);
        }

        @Operation(summary = "Get questions for a test series", description = "Retrieves all questions associated with a specific test series")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved questions", content = @Content(schema = @Schema(implementation = Question.class))),
                        @ApiResponse(responseCode = "404", description = "Test series not found")
        })
        @GetMapping("/{id}/questions")
        public ResponseEntity<List<Question>> getQuestions(
                        @Parameter(description = "ID of the test series", required = true) @PathVariable Long id) {
                List<Question> questions = testSeriesService.getQuestionsByTestSeriesId(id);
                return ResponseEntity.ok(questions);
        }

        @Operation(summary = "Create a new test series", description = "Creates a new test series with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Test series created successfully", content = @Content(schema = @Schema(implementation = TestSeries.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @PostMapping
        public ResponseEntity<TestSeries> createTestSeries(
                        @Parameter(description = "Test series details", required = true) @RequestBody TestSeries testSeries) {
                TestSeries created = testSeriesService.createTestSeries(testSeries);
                return ResponseEntity.ok(created);
        }

        @Operation(summary = "Add a question to test series", description = "Adds a new question to an existing test series")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Question added successfully", content = @Content(schema = @Schema(implementation = Question.class))),
                        @ApiResponse(responseCode = "404", description = "Test series not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid question data")
        })
        @PostMapping("/{id}/questions")
        public ResponseEntity<Question> addQuestion(
                        @Parameter(description = "ID of the test series", required = true) @PathVariable Long id,
                        @Parameter(description = "Question details", required = true) @RequestBody Question question) {
                Question created = testSeriesService.addQuestion(id, question);
                return ResponseEntity.ok(created);
        }

        @Operation(summary = "Search test series", description = "Search test series by title or description")
        @GetMapping("/search")
        public ResponseEntity<List<TestSeries>> searchTestSeries(@RequestParam String query) {
                List<TestSeries> results = testSeriesService.searchTestSeries(query);
                return ResponseEntity.ok(results);
        }

        @Operation(summary = "Filter by category", description = "Get test series by category")
        @GetMapping("/category/{categoryId}")
        public ResponseEntity<List<TestSeries>> getTestSeriesByCategory(@PathVariable Long categoryId) {
                List<TestSeries> results = testSeriesService.getTestSeriesByCategory(categoryId);
                return ResponseEntity.ok(results);
        }

        @Operation(summary = "Get featured test series", description = "Get featured test series for homepage")
        @GetMapping("/featured")
        public ResponseEntity<List<TestSeries>> getFeaturedTestSeries() {
                List<TestSeries> results = testSeriesService.getFeaturedTestSeries();
                return ResponseEntity.ok(results);
        }
        
        @Operation(summary = "Delete test series", description = "Delete a test series by ID")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTestSeries(@PathVariable Long id) {
                testSeriesService.deleteTestSeries(id);
                return ResponseEntity.ok().build();
        }
        
        @Operation(summary = "Toggle test series active status", description = "Activate or deactivate a test series")
        @PutMapping("/{id}/toggle-active")
        public ResponseEntity<TestSeries> toggleActive(@PathVariable Long id) {
                TestSeries updated = testSeriesService.toggleActive(id);
                return ResponseEntity.ok(updated);
        }
}
