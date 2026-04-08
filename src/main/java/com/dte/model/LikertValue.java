package com.dte.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

// Valid answer values for the quiz
// Goes from -10 (strongly disagree) to +10 (strongly agree)
public enum LikertValue {
  STRONGLY_DISAGREE(-10),
  DISAGREE(-5),
  NEUTRAL(0),
  AGREE(5),
  STRONGLY_AGREE(10);

  private final int value;

  LikertValue(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  // All valid numbers in a set for easy checking
  private static final Set<Integer> VALID_VALUES = Arrays.stream(values())
    .map(LikertValue::getValue)
    .collect(Collectors.toSet());

  // Check if number is valid
  public static boolean isValid(int value) {
    return VALID_VALUES.contains(value);
  }

  // Get enum from number
  public static LikertValue fromInt(int value) {
    for (LikertValue lv : values()) {
      if (lv.value == value) {
        return lv;
      }
    }
    throw new IllegalArgumentException(
      "Invalid value: " + value + ". Use: -10, -5, 0, 5, or 10"
    );
  }

  // Get readable name
  public String getLabel() {
    return switch (this) {
      case STRONGLY_DISAGREE -> "Strongly Disagree";
      case DISAGREE -> "Disagree";
      case NEUTRAL -> "Neutral";
      case AGREE -> "Agree";
      case STRONGLY_AGREE -> "Strongly Agree";
    };
  }
}
