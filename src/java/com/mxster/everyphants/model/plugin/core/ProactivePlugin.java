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
        return formatters.stream()
                .map(formatter -> formatter.apply(t))
                .collect(Collectors.toList());
    }

    public abstract T fetch();
}
