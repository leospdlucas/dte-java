package com.dte.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

// This class holds one question from the quiz
public class Question {

  private Integer id;
  private String axis; // M, C, or R
  private String text;

  // Empty constructor for JSON
  public Question() {}

  public Question(Integer id, String axis, String text) {
    this.id = id;
    this.axis = axis;
    this.text = text;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getAxis() {
    return axis;
  }

  public void setAxis(String axis) {
    this.axis = axis;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "Question [id=" + id + ", axis=" + axis + "]";
  }
}
