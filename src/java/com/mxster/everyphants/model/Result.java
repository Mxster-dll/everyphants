package com.mxster.everyphants.model;

public class Result {
    public final String title;
    public final String displayText;
    public final double score;
    public final String iconPath;

    public Result(String title, String displayText, double score, String iconPath) {
        this.title = title;
        this.displayText = displayText;
        this.score = score;
        this.iconPath = iconPath;
    }
}
