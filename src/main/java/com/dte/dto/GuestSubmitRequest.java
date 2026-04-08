package com.dte.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Map;

// Request body when guest submits answers
public class GuestSubmitRequest {

  // Optional invite code
  private String inviteCode;

  // Map: question id -> answer value
  @NotEmpty(message = "Answers cannot be empty")
  @Size(min = 30, max = 30, message = "Need exactly 30 answers")
  private Map<Integer, Integer> answers;

  public GuestSubmitRequest() {}

  public GuestSubmitRequest(String inviteCode, Map<Integer, Integer> answers) {
    this.inviteCode = inviteCode;
    this.answers = answers;
  }

  public String getInviteCode() {
    return inviteCode;
  }

  public void setInviteCode(String inviteCode) {
    this.inviteCode = inviteCode;
  }

  public Map<Integer, Integer> getAnswers() {
    return answers;
  }

  public void setAnswers(Map<Integer, Integer> answers) {
    this.answers = answers;
  }

  @Override
  public String toString() {
    return (
      "GuestSubmitRequest [inviteCode=" +
      inviteCode +
      ", count=" +
      (answers != null ? answers.size() : 0) +
      "]"
    );
  }
}
