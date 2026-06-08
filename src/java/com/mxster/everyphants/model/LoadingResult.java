package com.mxster.everyphants.model;

public class LoadingResult extends RefreshableResult {
    public LoadingResult(String titlePrefix, String displayPrefix, double score) {
        super(titlePrefix + ".", displayPrefix + ".", score);

        withRefresh(() -> {
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
