package com.mxster.everyphants.model.plugin.impl;

import java.util.Date;

import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ProactivePlugin;

public class TimePlugin extends ProactivePlugin {
    private RefreshableResult cachedResult;

    public TimePlugin() {
        super("时间", "时间.png");
    }

    @Override
    public Result build() {
        if (cachedResult == null) {
            Date now = new Date();
            cachedResult = new RefreshableResult(
                    now.toString(), Long.toString(now.getTime()), 2)
                    .withRefresh(() -> {
                        Date n = new Date();
                        cachedResult.setTitle(n.toString());
                        cachedResult.setDisplayText(Long.toString(n.getTime()));
                    });
        }
        return cachedResult;
    }
}
