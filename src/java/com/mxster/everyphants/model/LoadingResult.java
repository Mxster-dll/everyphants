package com.mxster.everyphants.model;

public class LoadingResult extends RefreshableResult {

    private final String titlePrefix;
    private final String displayPrefix;

    public LoadingResult(String titlePrefix, String displayPrefix, double score, String iconPath) {
        super(titlePrefix + ".", displayPrefix + ".", score, iconPath);
        this.titlePrefix = titlePrefix;
        this.displayPrefix = displayPrefix;

        withRefresh(0, () -> {
            int dots = (int) ((System.currentTimeMillis() / 500) % 3) + 1;
            String dotStr = ".".repeat(dots);
            setTitle(titlePrefix + dotStr);
            setDisplayText(displayPrefix + dotStr);
        });
    }

    public void finish(String title, String displayText) {
        stopRefreshing();
        setTitle(title);
        setDisplayText(displayText);
    }
}
