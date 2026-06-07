package com.mxster.everyphants.model.plugin.impl;

import java.util.Date;
import java.util.List;

import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ProactivePlugin;

public class TimePlugin extends ProactivePlugin {
    private RefreshableResult cachedResult;

    public TimePlugin() {
        super("时间", "时间.png");
    }

    @Override
    protected List<Result> buildResult() {
        if (cachedResult == null) {
            Date now = new Date();
            cachedResult = new RefreshableResult(
                    now.toString(), Long.toString(now.getTime()), 2)
                    .withRefresh(0, () -> {
                        Date n = new Date();
                        cachedResult.setTitle(n.toString());
                        cachedResult.setDisplayText(Long.toString(n.getTime()));
                    });
        }
        return List.of(cachedResult);
    }
}
