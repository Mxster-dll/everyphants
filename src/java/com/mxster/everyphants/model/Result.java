package com.mxster.everyphants.model;

public class Result {
    private String title;
    private String displayText;
    private double score;
    private String iconPath;

    public Result(String title, String displayText, double score, String iconPath) {
        this.title = title;
        this.displayText = displayText;
        this.score = score;
        this.iconPath = iconPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}
