package com.mxster.everyphants.model;

import com.mxster.everyphants.model.plugin.core.Refreshable;

public class RefreshableResult extends Result implements Refreshable {
    private int refreshInterval = 0;
    private Runnable refreshAction;

    public RefreshableResult(String title, String displayText, double score, String iconPath) {
        super(title, displayText, score, iconPath);
    }

    public RefreshableResult withRefresh(int intervalMs, Runnable action) {
        this.refreshInterval = intervalMs;
        this.refreshAction = action;
        return this;
    }

    public void refresh() {
        if (refreshAction != null) {
            refreshAction.run();
        }
    }

    @Override
    public int getRefreshInterval() {
        return refreshInterval;
    }

    @Override
    public void setRefreshInterval(int millis) {
        this.refreshInterval = millis;
    }
}
