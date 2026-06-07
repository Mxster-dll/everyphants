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

    /**
     * 构建结果列表。仅调用一次，结果会被缓存。
     */
    protected abstract List<Result> buildResult();
}
