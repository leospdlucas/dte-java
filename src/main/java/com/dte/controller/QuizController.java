package com.dte.controller;

import com.dte.dto.*;
import com.dte.service.QuestionService;
import com.dte.service.SubmissionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// REST API endpoints for the quiz
@RestController
@RequestMapping("/api")
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    private final QuestionService questionService;
    private final SubmissionService submissionService;

    public QuizController(QuestionService questionService, SubmissionService submissionService) {
        this.questionService = questionService;
        this.submissionService = submissionService;
    }

    // ==================== Questions ====================

    // Get questions for logged in user
    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestions() {
        logger.info("Getting questions for user");
        List<QuestionDTO> questions = questionService.getBalancedQuestions();
        return ResponseEntity.ok(questions);
    }

    // Get questions for guest
    @GetMapping("/questions-guest")
    public ResponseEntity<List<QuestionDTO>> getQuestionsForGuest() {
        logger.info("Getting questions for guest");
        List<QuestionDTO> questions = questionService.getBalancedQuestions();
        return ResponseEntity.ok(questions);
    }

    // ==================== Submissions ====================

    // Submit answers (logged in user)
    @PostMapping("/submit")
    public ResponseEntity<ScoreResultDTO> submitAnswers(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody SubmitAnswersRequest request) {
        
        logger.info("Got submission from user: {}", userId);

        // Use default user if none provided
        if (userId == null) {
            userId = 1L;
            logger.warn("No user ID, using default: {}", userId);
        }

        ScoreResultDTO result = submissionService.submitForUser(userId, request.getAnswers());
        return ResponseEntity.ok(result);
    }

    // Submit answers (guest)
    @PostMapping("/submit-guest")
    public ResponseEntity<ScoreResultDTO> submitGuestAnswers(
            @Valid @RequestBody GuestSubmitRequest request) {
        
        logger.info("Got guest submission, code: {}", request.getInviteCode());

        ScoreResultDTO result = submissionService.submitForGuest(
            request.getInviteCode(), 
            request.getAnswers()
        );
        return ResponseEntity.ok(result);
    }

    // ==================== Results ====================

    // Get latest result for user
    @GetMapping("/my-latest")
    public ResponseEntity<ScoreResultDTO> getMyLatestResult(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId == null) {
            userId = 1L;
        }

        logger.info("Getting latest result for user: {}", userId);

        var result = submissionService.getLatestForUser(userId);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.get());
    }

    // Get all results for user
    @GetMapping("/my-history")
    public ResponseEntity<List<ScoreResultDTO>> getMyHistory(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId == null) {
            userId = 1L;
        }

        logger.info("Getting history for user: {}", userId);
        List<ScoreResultDTO> history = submissionService.getAllForUser(userId);
        return ResponseEntity.ok(history);
    }

    // Get stats for all submissions
    @GetMapping("/aggregate")
    public ResponseEntity<AggregateResultDTO> getAggregateResults() {
        logger.info("Getting aggregate results");
        AggregateResultDTO results = submissionService.getAggregateResults();
        return ResponseEntity.ok(results);
    }

    // Get simple stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.info("Getting statistics");
        Map<String, Object> stats = submissionService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    // ==================== Utility ====================

    // Health check - To know if service is running and questions are loaded
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "DTE API");
        health.put("questionsLoaded", questionService.getTotalQuestionCount());
        return ResponseEntity.ok(health);
    }

    // Ping - If the user is slow to answer, the app can ping this endpoint to keep the session alive and show a loading indicator
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "pong");
        return ResponseEntity.ok(response);
    }
}
