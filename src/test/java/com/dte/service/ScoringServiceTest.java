package com.dte.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.dte.dto.ScoreResultDTO;
import com.dte.model.Question;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Tests for the scoring algorithm
@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {

  @Mock
  private QuestionService questionService;

  private ScoringService scoringService;

  @BeforeEach
  void setUp() {
    scoringService = new ScoringService(questionService);
    scoringService.setExponent(2.0);
  }

  // Helper - make a question
  private Question makeQuestion(int id, String axis) {
    return new Question(id, axis, "Test question " + id);
  }

  // Helper - setup mocks for standard 30 questions
  private void setupMocks() {
    // Questions 1-10: M axis
    for (int i = 1; i <= 10; i++) {
      when(questionService.findById(i)).thenReturn(
        Optional.of(makeQuestion(i, "M"))
      );
    }
    // Questions 11-20: C axis
    for (int i = 11; i <= 20; i++) {
      when(questionService.findById(i)).thenReturn(
        Optional.of(makeQuestion(i, "C"))
      );
    }
    // Questions 21-30: R axis
    for (int i = 21; i <= 30; i++) {
      when(questionService.findById(i)).thenReturn(
        Optional.of(makeQuestion(i, "R"))
      );
    }
  }

  // ==================== Score Tests ====================

  @Test
  @DisplayName("Neutral answers should give equal weights")
  void testNeutralAnswers() {
    setupMocks();

    // All answers are 0 (neutral)
    Map<Integer, Integer> answers = new HashMap<>();
    for (int i = 1; i <= 30; i++) {
      answers.put(i, 0);
    }

    ScoreResultDTO result = scoringService.calculateScores(answers);

    // All scores should be 0
    assertEquals(0.0, result.getScoreM(), 0.001);
    assertEquals(0.0, result.getScoreC(), 0.001);
    assertEquals(0.0, result.getScoreR(), 0.001);

    // Weights should be equal (1/3 each)
    assertEquals(0.3333, result.getWeightM(), 0.01);
    assertEquals(0.3333, result.getWeightC(), 0.01);
    assertEquals(0.3333, result.getWeightR(), 0.01);

    // Position should be center of triangle
    assertEquals(0.5, result.getX(), 0.01);
    assertEquals(0.289, result.getY(), 0.01);
  }

  @Test
  @DisplayName("High M answers should favor M axis")
  void testHighMAnswers() {
    setupMocks();

    Map<Integer, Integer> answers = new HashMap<>();
    // M questions: strongly agree
    for (int i = 1; i <= 10; i++) {
      answers.put(i, 10);
    }
    // C and R questions: neutral
    for (int i = 11; i <= 30; i++) {
      answers.put(i, 0);
    }

    ScoreResultDTO result = scoringService.calculateScores(answers);

    // M score should be highest
    assertEquals(10.0, result.getScoreM(), 0.001);
    assertEquals(0.0, result.getScoreC(), 0.001);
    assertEquals(0.0, result.getScoreR(), 0.001);

    // M weight should be biggest
    assertTrue(result.getWeightM() > result.getWeightC());
    assertTrue(result.getWeightM() > result.getWeightR());

    // Position should be near M corner (0, 0)
    assertTrue(result.getX() < 0.5);
    assertTrue(result.getY() < 0.3);
  }

  @Test
  @DisplayName("High C answers should favor C axis")
  void testHighCAnswers() {
    setupMocks();

    Map<Integer, Integer> answers = new HashMap<>();
    // M questions: neutral
    for (int i = 1; i <= 10; i++) {
      answers.put(i, 0);
    }
    // C questions: strongly agree
    for (int i = 11; i <= 20; i++) {
      answers.put(i, 10);
    }
    // R questions: neutral
    for (int i = 21; i <= 30; i++) {
      answers.put(i, 0);
    }

    ScoreResultDTO result = scoringService.calculateScores(answers);

    // C score should be highest
    assertEquals(0.0, result.getScoreM(), 0.001);
    assertEquals(10.0, result.getScoreC(), 0.001);
    assertEquals(0.0, result.getScoreR(), 0.001);

    // C weight should be biggest
    assertTrue(result.getWeightC() > result.getWeightM());
    assertTrue(result.getWeightC() > result.getWeightR());

    // Position should be near C corner (1, 0)
    assertTrue(result.getX() > 0.5);
    assertTrue(result.getY() < 0.3);
  }

  @Test
  @DisplayName("High R answers should favor R axis")
  void testHighRAnswers() {
    setupMocks();

    Map<Integer, Integer> answers = new HashMap<>();
    // M and C questions: neutral
    for (int i = 1; i <= 20; i++) {
      answers.put(i, 0);
    }
    // R questions: strongly agree
    for (int i = 21; i <= 30; i++) {
      answers.put(i, 10);
    }

    ScoreResultDTO result = scoringService.calculateScores(answers);

    // R score should be highest
    assertEquals(0.0, result.getScoreM(), 0.001);
    assertEquals(0.0, result.getScoreC(), 0.001);
    assertEquals(10.0, result.getScoreR(), 0.001);

    // R weight should be biggest
    assertTrue(result.getWeightR() > result.getWeightM());
    assertTrue(result.getWeightR() > result.getWeightC());

    // Position should be near R corner (0.5, 0.866)
    assertTrue(result.getY() > 0.4);
  }

  // ==================== Validation Tests ====================

  @Test
  @DisplayName("Should throw error for empty answers")
  void testEmptyAnswers() {
    Map<Integer, Integer> answers = new HashMap<>();

    assertThrows(IllegalArgumentException.class, () -> {
      scoringService.calculateScores(answers);
    });
  }

  @Test
  @DisplayName("Should throw error for null answers")
  void testNullAnswers() {
    assertThrows(IllegalArgumentException.class, () -> {
      scoringService.calculateScores(null);
    });
  }

  @Test
  @DisplayName("Should throw error for invalid value")
  void testInvalidValue() {
    when(questionService.findById(1)).thenReturn(
      Optional.of(makeQuestion(1, "M"))
    );

    Map<Integer, Integer> answers = new HashMap<>();
    answers.put(1, 7); // 7 is not valid

    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> {
        scoringService.calculateScores(answers);
      }
    );

    assertTrue(ex.getMessage().contains("Invalid value"));
  }

  @Test
  @DisplayName("Should throw error for unknown question")
  void testUnknownQuestion() {
    when(questionService.findById(anyInt())).thenReturn(Optional.empty());

    Map<Integer, Integer> answers = new HashMap<>();
    answers.put(999, 5);

    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> {
        scoringService.calculateScores(answers);
      }
    );

    assertTrue(ex.getMessage().contains("Unknown question"));
  }

  // ==================== Edge Cases ====================

  @Test
  @DisplayName("Should handle all disagree answers")
  void testAllDisagree() {
    setupMocks();

    Map<Integer, Integer> answers = new HashMap<>();
    for (int i = 1; i <= 30; i++) {
      answers.put(i, -10);
    }

    ScoreResultDTO result = scoringService.calculateScores(answers);

    // All scores should be -10
    assertEquals(-10.0, result.getScoreM(), 0.001);
    assertEquals(-10.0, result.getScoreC(), 0.001);
    assertEquals(-10.0, result.getScoreR(), 0.001);

    // Weights should still be equal
    assertEquals(0.3333, result.getWeightM(), 0.01);
    assertEquals(0.3333, result.getWeightC(), 0.01);
    assertEquals(0.3333, result.getWeightR(), 0.01);
  }

  @Test
  @DisplayName("Should handle mixed answers")
  void testMixedAnswers() {
    setupMocks();

    Map<Integer, Integer> answers = new HashMap<>();
    // M: half +10, half -10 (average = 0)
    for (int i = 1; i <= 5; i++) {
      answers.put(i, 10);
    }
    for (int i = 6; i <= 10; i++) {
      answers.put(i, -10);
    }
    // C and R: neutral
    for (int i = 11; i <= 30; i++) {
      answers.put(i, 0);
    }

    ScoreResultDTO result = scoringService.calculateScores(answers);

    // M score should be 0
    assertEquals(0.0, result.getScoreM(), 0.001);
  }

  // ==================== Position Tests ====================

  @Test
  @DisplayName("M dominant should be near M corner")
  void testMDominantPosition() {
    setupMocks();

    Map<Integer, Integer> answers = new HashMap<>();
    // M: strongly agree
    for (int i = 1; i <= 10; i++) {
      answers.put(i, 10);
    }
    // C and R: strongly disagree
    for (int i = 11; i <= 30; i++) {
      answers.put(i, -10);
    }

    ScoreResultDTO result = scoringService.calculateScores(answers);

    // Should be close to (0, 0)
    assertTrue(result.getX() < 0.2);
    assertTrue(result.getY() < 0.2);
  }

  @Test
  @DisplayName("Position should always be inside triangle")
  void testPositionBounds() {
    setupMocks();

    // Test different patterns
    int[][] patterns = {
      { 10, 10, 10 },
      { -10, -10, -10 },
      { 10, -10, 0 },
      { 0, 0, 0 },
      { 10, 0, -10 },
    };

    for (int[] pattern : patterns) {
      Map<Integer, Integer> answers = new HashMap<>();
      for (int i = 1; i <= 10; i++) {
        answers.put(i, pattern[0]);
      }
      for (int i = 11; i <= 20; i++) {
        answers.put(i, pattern[1]);
      }
      for (int i = 21; i <= 30; i++) {
        answers.put(i, pattern[2]);
      }

      ScoreResultDTO result = scoringService.calculateScores(answers);

      // X should be between 0 and 1
      assertTrue(
        result.getX() >= 0 && result.getX() <= 1,
        "X out of bounds: " + result.getX()
      );

      // Y should be between 0 and ~0.866
      assertTrue(
        result.getY() >= 0 && result.getY() <= 0.867,
        "Y out of bounds: " + result.getY()
      );
    }
  }
}
