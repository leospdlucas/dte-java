package com.dte.dto;

import java.util.List;

// Stats for all submissions together
public class AggregateResultDTO {

    // Counts
    private Long totalSubmissions;
    private Long userSubmissions;
    private Long guestSubmissions;

    // Average scores
    private Double avgScoreM;
    private Double avgScoreC;
    private Double avgScoreR;

    // Average position
    private Double avgX;
    private Double avgY;

    // All points for the chart
    private List<CoordinatePoint> allPoints;

    public AggregateResultDTO() {}

    // One point on the triangle
    public static class CoordinatePoint {
        private Double x;
        private Double y;
        private String type; // "user" or "guest"

        public CoordinatePoint() {}

        public CoordinatePoint(Double x, Double y, String type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }

        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    // Builder helper
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long totalSubmissions;
        private Long userSubmissions;
        private Long guestSubmissions;
        private Double avgScoreM;
        private Double avgScoreC;
        private Double avgScoreR;
        private Double avgX;
        private Double avgY;
        private List<CoordinatePoint> allPoints;

        public Builder totalSubmissions(Long val) { this.totalSubmissions = val; return this; }
        public Builder userSubmissions(Long val) { this.userSubmissions = val; return this; }
        public Builder guestSubmissions(Long val) { this.guestSubmissions = val; return this; }
        public Builder avgScoreM(Double val) { this.avgScoreM = val; return this; }
        public Builder avgScoreC(Double val) { this.avgScoreC = val; return this; }
        public Builder avgScoreR(Double val) { this.avgScoreR = val; return this; }
        public Builder avgX(Double val) { this.avgX = val; return this; }
        public Builder avgY(Double val) { this.avgY = val; return this; }
        public Builder allPoints(List<CoordinatePoint> val) { this.allPoints = val; return this; }

        public AggregateResultDTO build() {
            AggregateResultDTO dto = new AggregateResultDTO();
            dto.totalSubmissions = this.totalSubmissions;
            dto.userSubmissions = this.userSubmissions;
            dto.guestSubmissions = this.guestSubmissions;
            dto.avgScoreM = this.avgScoreM;
            dto.avgScoreC = this.avgScoreC;
            dto.avgScoreR = this.avgScoreR;
            dto.avgX = this.avgX;
            dto.avgY = this.avgY;
            dto.allPoints = this.allPoints;
            return dto;
        }
    }

    // Getters and Setters
    public Long getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(Long val) { this.totalSubmissions = val; }

    public Long getUserSubmissions() { return userSubmissions; }
    public void setUserSubmissions(Long val) { this.userSubmissions = val; }

    public Long getGuestSubmissions() { return guestSubmissions; }
    public void setGuestSubmissions(Long val) { this.guestSubmissions = val; }

    public Double getAvgScoreM() { return avgScoreM; }
    public void setAvgScoreM(Double val) { this.avgScoreM = val; }

    public Double getAvgScoreC() { return avgScoreC; }
    public void setAvgScoreC(Double val) { this.avgScoreC = val; }

    public Double getAvgScoreR() { return avgScoreR; }
    public void setAvgScoreR(Double val) { this.avgScoreR = val; }

    public Double getAvgX() { return avgX; }
    public void setAvgX(Double val) { this.avgX = val; }

    public Double getAvgY() { return avgY; }
    public void setAvgY(Double val) { this.avgY = val; }

    public List<CoordinatePoint> getAllPoints() { return allPoints; }
    public void setAllPoints(List<CoordinatePoint> val) { this.allPoints = val; }
}
