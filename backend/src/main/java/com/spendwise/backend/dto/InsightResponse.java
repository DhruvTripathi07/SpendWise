package com.spendwise.backend.dto;

public class InsightResponse {

    private String type;
    private String title;
    private String message;
    private String recommendation;

    public InsightResponse(
            String type,
            String title,
            String message,
            String recommendation) {

        this.type = type;
        this.title = title;
        this.message = message;
        this.recommendation = recommendation;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
