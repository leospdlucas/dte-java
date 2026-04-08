package com.dte.dto;

// Question data sent to user (without axis info)
public class QuestionDTO {

  private Integer id;
  private String text;

  public QuestionDTO() {}

  public QuestionDTO(Integer id, String text) {
    this.id = id;
    this.text = text;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
