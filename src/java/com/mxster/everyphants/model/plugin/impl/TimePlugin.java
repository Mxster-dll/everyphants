package com.mxster.everyphants.model.plugin.impl;

import java.util.Date;

import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ProactivePlugin;
import com.mxster.everyphants.model.plugin.core.Refreshable;

public class TimePlugin extends ProactivePlugin<Date> implements Refreshable {
    private int refreshInterval = 1000;
    private RefreshableResult cachedResult;

    public TimePlugin() {
        super("时间");

        formatters.add(this::showTime);
    }

    @Override
    public int getRefreshInterval() {
        return refreshInterval;
    }

    @Override
    public void setRefreshInterval(int millis) {
        this.refreshInterval = millis;
    }

    @Override
    public Date fetch() {
        return new Date();
    }

    public Result showTime(Date date) {
        if (cachedResult == null) {
            cachedResult = new RefreshableResult(
                    date.toString(), Long.toString(date.getTime()), 2, null)
                    .withRefresh(0, () -> {
                        Date now = new Date();
                        cachedResult.setTitle(now.toString());
                        cachedResult.setDisplayText(Long.toString(now.getTime()));
                    });
        }
        return cachedResult;
    }
}
