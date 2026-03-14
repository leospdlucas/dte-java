package com.dte.controller;

import com.dte.dto.GuestSubmitRequest;
import com.dte.dto.QuestionDTO;
import com.dte.dto.ScoreResultDTO;
import com.dte.dto.SubmitAnswersRequest;
import com.dte.service.QuestionService;
import com.dte.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Tests for the REST API endpoints
@WebMvcTest(QuizController.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private SubmissionService submissionService;

    // ==================== Questions Tests ====================

    @Test
    @DisplayName("Should return list of questions")
    void testGetQuestions() throws Exception {
        // Setup
        when(questionService.getBalancedQuestions()).thenReturn(Arrays.asList(
            new QuestionDTO(1, "Question 1"),
            new QuestionDTO(2, "Question 2"),
            new QuestionDTO(3, "Question 3")
        ));

        // Test
        mockMvc.perform(get("/api/questions"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].text").value("Question 1"));
    }

    // ==================== Submit Tests ====================

    @Test
    @DisplayName("Should submit answers and return scores")
    void testSubmitAnswers() throws Exception {
        // Setup
        Map<Integer, Integer> answers = makeValidAnswers();
        SubmitAnswersRequest request = new SubmitAnswersRequest(answers);

        ScoreResultDTO result = ScoreResultDTO.builder()
            .scoreM(2.5)
            .scoreC(-1.0)
            .scoreR(3.0)
            .weightM(0.35)
            .weightC(0.25)
            .weightR(0.40)
            .x(0.475)
            .y(0.346)
            .submissionId(1L)
            .build();

        when(submissionService.submitForUser(anyLong(), any())).thenReturn(result);

        // Test
        mockMvc.perform(post("/api/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", "123")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.scoreM").value(2.5))
            .andExpect(jsonPath("$.scoreC").value(-1.0))
            .andExpect(jsonPath("$.scoreR").value(3.0))
            .andExpect(jsonPath("$.x").value(0.475))
            .andExpect(jsonPath("$.y").value(0.346))
            .andExpect(jsonPath("$.submissionId").value(1));
    }

    @Test
    @DisplayName("Should reject empty answers")
    void testRejectEmptyAnswers() throws Exception {
        SubmitAnswersRequest request = new SubmitAnswersRequest(new HashMap<>());

        mockMvc.perform(post("/api/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", "123")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject wrong number of answers")
    void testRejectWrongCount() throws Exception {
        // Only 25 answers instead of 30
        Map<Integer, Integer> answers = new HashMap<>();
        for (int i = 1; i <= 25; i++) {
            answers.put(i, 5);
        }
        SubmitAnswersRequest request = new SubmitAnswersRequest(answers);

        mockMvc.perform(post("/api/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", "123")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should submit guest answers")
    void testSubmitGuestAnswers() throws Exception {
        Map<Integer, Integer> answers = makeValidAnswers();
        GuestSubmitRequest request = new GuestSubmitRequest("invite123", answers);

        ScoreResultDTO result = ScoreResultDTO.builder()
            .scoreM(0.0)
            .scoreC(0.0)
            .scoreR(0.0)
            .weightM(0.3333)
            .weightC(0.3333)
            .weightR(0.3334)
            .x(0.5)
            .y(0.289)
            .submissionId(42L)
            .build();

        when(submissionService.submitForGuest(any(), any())).thenReturn(result);

        mockMvc.perform(post("/api/submit-guest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.submissionId").value(42));
    }

    // ==================== Results Tests ====================

    @Test
    @DisplayName("Should return latest result")
    void testGetLatestResult() throws Exception {
        ScoreResultDTO result = ScoreResultDTO.builder()
            .scoreM(5.0)
            .scoreC(3.0)
            .scoreR(7.0)
            .weightM(0.30)
            .weightC(0.25)
            .weightR(0.45)
            .x(0.475)
            .y(0.390)
            .submissionId(99L)
            .build();

        when(submissionService.getLatestForUser(123L)).thenReturn(Optional.of(result));

        mockMvc.perform(get("/api/my-latest")
                .header("X-User-Id", "123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.submissionId").value(99))
            .andExpect(jsonPath("$.scoreR").value(7.0));
    }

    @Test
    @DisplayName("Should return 404 when no submissions")
    void testNoSubmissions() throws Exception {
        when(submissionService.getLatestForUser(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/my-latest")
                .header("X-User-Id", "123"))
            .andExpect(status().isNotFound());
    }

    // ==================== Utility Tests ====================

    @Test
    @DisplayName("Should return health status")
    void testHealth() throws Exception {
        when(questionService.getTotalQuestionCount()).thenReturn(90);

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.service").value("DTE API"))
            .andExpect(jsonPath("$.questionsLoaded").value(90));
    }

    @Test
    @DisplayName("Should return pong")
    void testPing() throws Exception {
        mockMvc.perform(get("/api/ping"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("pong"));
    }

    // Helper - create 30 valid answers
    private Map<Integer, Integer> makeValidAnswers() {
        Map<Integer, Integer> answers = new HashMap<>();
        for (int i = 1; i <= 30; i++) {
            answers.put(i, 0); // all neutral
        }
        return answers;
    }
}
