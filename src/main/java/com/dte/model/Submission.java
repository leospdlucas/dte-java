package com.dte.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Saves quiz results for logged in users
@Entity
@Table(name = "submissions")
public class Submission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // User ID - will come from login later
  @Column(name = "user_id")
  private Long userId;

  // Scores for each axis (-10 to +10)
  @Column(name = "score_m", nullable = false)
  private Double scoreM;

  @Column(name = "score_c", nullable = false)
  private Double scoreC;

  @Column(name = "score_r", nullable = false)
  private Double scoreR;

  // Weights for triangle (add up to 1.0)
  @Column(name = "weight_m", nullable = false)
  private Double weightM;

  @Column(name = "weight_c", nullable = false)
  private Double weightC;

  @Column(name = "weight_r", nullable = false)
  private Double weightR;

  // Position on the triangle
  @Column(name = "coord_x", nullable = false)
  private Double coordX;

  @Column(name = "coord_y", nullable = false)
  private Double coordY;

  // Original answers as JSON
  @Column(name = "raw_answers", columnDefinition = "TEXT")
  private String rawAnswers;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // Set dates automatically
  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public Submission() {}

  public Submission(
    Long userId,
    Double scoreM,
    Double scoreC,
    Double scoreR,
    Double weightM,
    Double weightC,
    Double weightR,
    Double coordX,
    Double coordY,
    String rawAnswers
  ) {
    this.userId = userId;
    this.scoreM = scoreM;
    this.scoreC = scoreC;
    this.scoreR = scoreR;
    this.weightM = weightM;
    this.weightC = weightC;
    this.weightR = weightR;
    this.coordX = coordX;
    this.coordY = coordY;
    this.rawAnswers = rawAnswers;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Double getScoreM() {
    return scoreM;
  }

  public void setScoreM(Double scoreM) {
    this.scoreM = scoreM;
  }

  public Double getScoreC() {
    return scoreC;
  }

  public void setScoreC(Double scoreC) {
    this.scoreC = scoreC;
  }

  public Double getScoreR() {
    return scoreR;
  }

  public void setScoreR(Double scoreR) {
    this.scoreR = scoreR;
  }

  public Double getWeightM() {
    return weightM;
  }

  public void setWeightM(Double weightM) {
    this.weightM = weightM;
  }

  public Double getWeightC() {
    return weightC;
  }

  public void setWeightC(Double weightC) {
    this.weightC = weightC;
  }

  public Double getWeightR() {
    return weightR;
  }

  public void setWeightR(Double weightR) {
    this.weightR = weightR;
  }

  public Double getCoordX() {
    return coordX;
  }

  public void setCoordX(Double coordX) {
    this.coordX = coordX;
  }

  public Double getCoordY() {
    return coordY;
  }

  public void setCoordY(Double coordY) {
    this.coordY = coordY;
  }

  public String getRawAnswers() {
    return rawAnswers;
  }

  public void setRawAnswers(String rawAnswers) {
    this.rawAnswers = rawAnswers;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return (
      "Submission [id=" +
      id +
      ", userId=" +
      userId +
      ", x=" +
      coordX +
      ", y=" +
      coordY +
      "]"
    );
  }
}
