package com.dte.service;

import com.dte.dto.ScoreResultDTO;
import com.dte.model.LikertValue;
import com.dte.model.Question;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Calculates scores and triangle position from answers
@Service
public class ScoringService {

  private static final Logger logger = LoggerFactory.getLogger(
    ScoringService.class
  );

  // Triangle corners
  // M is bottom-left: (0, 0)
  // C is bottom-right: (1, 0)
  // R is top: (0.5, 0.866)
  private static final double M_X = 0.0;
  private static final double M_Y = 0.0;
  private static final double C_X = 1.0;
  private static final double C_Y = 0.0;
  private static final double R_X = 0.5;
  private static final double R_Y = Math.sqrt(3) / 2;

  private final QuestionService questionService;

  @Value("${dte.scoring.exponent:2.0}")
  private double exponent;

  public ScoringService(QuestionService questionService) {
    this.questionService = questionService;
  }

  // Main method - takes answers and returns scores
  public ScoreResultDTO calculateScores(Map<Integer, Integer> answers) {
    // Check if answers are valid
    checkAnswers(answers);

    // Get average score for each axis
    Map<String, Double> scores = getAxisScores(answers);
    double scoreM = scores.get("M");
    double scoreC = scores.get("C");
    double scoreR = scores.get("R");

    logger.debug("Scores: M={}, C={}, R={}", scoreM, scoreC, scoreR);

    // Convert to weights (0 to 1)
    Map<String, Double> weights = getWeights(scoreM, scoreC, scoreR);
    double weightM = weights.get("M");
    double weightC = weights.get("C");
    double weightR = weights.get("R");

    logger.debug("Weights: M={}, C={}, R={}", weightM, weightC, weightR);

    // Calculate position on triangle
    double x = getTriangleX(weightM, weightC, weightR);
    double y = getTriangleY(weightM, weightC, weightR);

    logger.debug("Position: x={}, y={}", x, y);

    // Build result
    return ScoreResultDTO.builder()
      .scoreM(round(scoreM, 4))
      .scoreC(round(scoreC, 4))
      .scoreR(round(scoreR, 4))
      .weightM(round(weightM, 4))
      .weightC(round(weightC, 4))
      .weightR(round(weightR, 4))
      .x(round(x, 6))
      .y(round(y, 6))
      .build();
  }

  // Check if all answers are valid
  private void checkAnswers(Map<Integer, Integer> answers) {
    if (answers == null || answers.isEmpty()) {
      throw new IllegalArgumentException("Answers cannot be empty");
    }

    for (Integer questionId : answers.keySet()) {
      Integer value = answers.get(questionId);

      // Check if question exists
      Optional<Question> question = questionService.findById(questionId);
      if (question.isEmpty()) {
        throw new IllegalArgumentException(
          "Unknown question ID: " + questionId
        );
      }

      // Check if value is valid
      if (!LikertValue.isValid(value)) {
        throw new IllegalArgumentException(
          "Invalid value for question " +
            questionId +
            ": " +
            value +
            ". Use: -10, -5, 0, 5, 10"
        );
      }
    }
  }

  // Calculate average score for each axis
  private Map<String, Double> getAxisScores(Map<Integer, Integer> answers) {
    // Lists to hold values for each axis
    List<Integer> mValues = new ArrayList<>();
    List<Integer> cValues = new ArrayList<>();
    List<Integer> rValues = new ArrayList<>();

    // Sort answers by axis
    for (Integer questionId : answers.keySet()) {
      Integer value = answers.get(questionId);
      Question question = questionService.findById(questionId).get();

      String axis = question.getAxis();
      if (axis.equals("M")) {
        mValues.add(value);
      } else if (axis.equals("C")) {
        cValues.add(value);
      } else if (axis.equals("R")) {
        rValues.add(value);
      }
    }

    // Calculate averages
    Map<String, Double> scores = new HashMap<>();
    scores.put("M", calculateAverage(mValues));
    scores.put("C", calculateAverage(cValues));
    scores.put("R", calculateAverage(rValues));

    return scores;
  }

  // Helper to calculate average
  private double calculateAverage(List<Integer> values) {
    if (values.isEmpty()) {
      return 0.0;
    }

    int sum = 0;
    for (Integer v : values) {
      sum = sum + v;
    }
    return (double) sum / values.size();
  }

  // Convert scores to weights
  // Formula: weight = (score + 10)^exponent / total
  private Map<String, Double> getWeights(
    double scoreM,
    double scoreC,
    double scoreR
  ) {
    // Shift to positive and apply power
    double rawM = Math.pow(scoreM + 10, exponent);
    double rawC = Math.pow(scoreC + 10, exponent);
    double rawR = Math.pow(scoreR + 10, exponent);

    // Get total
    double total = rawM + rawC + rawR;

    // Handle edge case
    if (total == 0) {
      Map<String, Double> result = new HashMap<>();
      result.put("M", 1.0 / 3);
      result.put("C", 1.0 / 3);
      result.put("R", 1.0 / 3);
      return result;
    }

    // Normalize
    Map<String, Double> result = new HashMap<>();
    result.put("M", rawM / total);
    result.put("C", rawC / total);
    result.put("R", rawR / total);
    return result;
  }

  // Get X position on triangle
  private double getTriangleX(double wM, double wC, double wR) {
    return wM * M_X + wC * C_X + wR * R_X;
  }

  // Get Y position on triangle
  private double getTriangleY(double wM, double wC, double wR) {
    return wM * M_Y + wC * C_Y + wR * R_Y;
  }

  // Round to N decimal places
  private double round(double value, int places) {
    double scale = Math.pow(10, places);
    return Math.round(value * scale) / scale;
  }

  public double getExponent() {
    return exponent;
  }

  public void setExponent(double exponent) {
    this.exponent = exponent;
  }
}
