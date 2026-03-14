package com.dte.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Map;

// Request body when user submits answers
public class SubmitAnswersRequest {

    // Map: question id -> answer value
    // Must have exactly 30 answers
    @NotEmpty(message = "Answers cannot be empty")
    @Size(min = 30, max = 30, message = "Need exactly 30 answers")
    private Map<Integer, Integer> answers;

    public SubmitAnswersRequest() {}

    public SubmitAnswersRequest(Map<Integer, Integer> answers) {
        this.answers = answers;
    }

    public Map<Integer, Integer> getAnswers() { return answers; }
    public void setAnswers(Map<Integer, Integer> answers) { this.answers = answers; }

    @Override
    public String toString() {
        return "SubmitAnswersRequest [count=" + (answers != null ? answers.size() : 0) + "]";
    }
}
