package com.mxster.everyphants.model.plugin.core;

public interface Refreshable {
    public int getRefreshInterval();

    public void setRefreshInterval(int millis);
}
