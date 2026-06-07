package com.mxster.everyphants.model.plugin.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mxster.everyphants.model.Result;

public abstract class ProactivePlugin<T> extends Plugin {
    protected List<Function<T, Result>> formatters = new ArrayList<>();

    public ProactivePlugin(String name) {
        super(name);
    }

    public ProactivePlugin(String name, String iconPath) {
        super(name, iconPath);
    }

    public List<Result> query() {
        T t = fetch();
        List<Result> results = formatters.stream()
                .map(formatter -> formatter.apply(t))
                .collect(Collectors.toList());

        // 自动为所有结果附上插件的图标路径
        if (iconFile != null && !iconFile.isEmpty()) {
            for (Result r : results) {
                if (r.getIconPath() == null || r.getIconPath().isEmpty()) {
                    r.setIconPath(iconFile);
                }
            }
        }

        return results;
    }

    public abstract T fetch();
}
