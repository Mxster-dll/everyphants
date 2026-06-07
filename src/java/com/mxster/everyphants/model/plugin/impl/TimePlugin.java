package com.mxster.everyphants.model.plugin.impl;

import java.util.Date;

import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ProactivePlugin;

public class TimePlugin extends ProactivePlugin<Date> {
    private RefreshableResult cachedResult;

    public TimePlugin() {
        super("时间", "时间.png");

        formatters.add(this::showTime);
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
