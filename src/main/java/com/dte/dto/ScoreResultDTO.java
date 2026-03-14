package com.dte.dto;

// Result data sent back after quiz submission
public class ScoreResultDTO {

    // Scores per axis (-10 to +10)
    private Double scoreM;
    private Double scoreC;
    private Double scoreR;

    // Weights (add up to 1.0)
    private Double weightM;
    private Double weightC;
    private Double weightR;

    // Position on triangle
    private Double x;
    private Double y;

    // ID saved in database
    private Long submissionId;

    public ScoreResultDTO() {}

    public ScoreResultDTO(Double scoreM, Double scoreC, Double scoreR,
                          Double weightM, Double weightC, Double weightR,
                          Double x, Double y, Long submissionId) {
        this.scoreM = scoreM;
        this.scoreC = scoreC;
        this.scoreR = scoreR;
        this.weightM = weightM;
        this.weightC = weightC;
        this.weightR = weightR;
        this.x = x;
        this.y = y;
        this.submissionId = submissionId;
    }

    // Builder helper
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Double scoreM;
        private Double scoreC;
        private Double scoreR;
        private Double weightM;
        private Double weightC;
        private Double weightR;
        private Double x;
        private Double y;
        private Long submissionId;

        public Builder scoreM(Double val) { this.scoreM = val; return this; }
        public Builder scoreC(Double val) { this.scoreC = val; return this; }
        public Builder scoreR(Double val) { this.scoreR = val; return this; }
        public Builder weightM(Double val) { this.weightM = val; return this; }
        public Builder weightC(Double val) { this.weightC = val; return this; }
        public Builder weightR(Double val) { this.weightR = val; return this; }
        public Builder x(Double val) { this.x = val; return this; }
        public Builder y(Double val) { this.y = val; return this; }
        public Builder submissionId(Long val) { this.submissionId = val; return this; }

        public ScoreResultDTO build() {
            return new ScoreResultDTO(scoreM, scoreC, scoreR, 
                                       weightM, weightC, weightR, 
                                       x, y, submissionId);
        }
    }

    // Getters and Setters
    public Double getScoreM() { return scoreM; }
    public void setScoreM(Double scoreM) { this.scoreM = scoreM; }

    public Double getScoreC() { return scoreC; }
    public void setScoreC(Double scoreC) { this.scoreC = scoreC; }

    public Double getScoreR() { return scoreR; }
    public void setScoreR(Double scoreR) { this.scoreR = scoreR; }

    public Double getWeightM() { return weightM; }
    public void setWeightM(Double weightM) { this.weightM = weightM; }

    public Double getWeightC() { return weightC; }
    public void setWeightC(Double weightC) { this.weightC = weightC; }

    public Double getWeightR() { return weightR; }
    public void setWeightR(Double weightR) { this.weightR = weightR; }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }

    @Override
    public String toString() {
        return "ScoreResultDTO [x=" + x + ", y=" + y + ", id=" + submissionId + "]";
    }
}
