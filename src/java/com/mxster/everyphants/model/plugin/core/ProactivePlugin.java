package com.mxster.everyphants.model.plugin.core;

import java.util.List;

import com.mxster.everyphants.model.Result;

public abstract class ProactivePlugin extends Plugin {
    private List<Result> cachedResults;

    public ProactivePlugin(String name) {
        super(name);
    }

    public ProactivePlugin(String name, String iconPath) {
        super(name, iconPath);
    }

    @Override
    public final List<Result> query(String input) {
        if (cachedResults == null) {
            cachedResults = buildResult();
            applyPluginIcon(cachedResults);
        }
        return cachedResults;
    }

    protected abstract List<Result> buildResult();
}
