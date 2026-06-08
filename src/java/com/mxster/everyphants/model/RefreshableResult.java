package com.mxster.everyphants.model;

public class RefreshableResult extends Result {
    private boolean refreshing;
    private Runnable refreshAction;

    public RefreshableResult(String title, String displayText, double score) {
        super(title, displayText, score);
    }

    public RefreshableResult withRefresh(Runnable action) {
        this.refreshing = true;
        this.refreshAction = action;
        return this;
    }

    public void refresh() {
        if (refreshAction != null) {
            refreshAction.run();
        }
    }

    public void stopRefreshing() {
        this.refreshing = false;
    }

    public boolean isRefreshing() {
        return refreshing && refreshAction != null;
    }
}
