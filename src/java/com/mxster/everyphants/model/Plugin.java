package com.mxster.everyphants.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class Plugin<T> {
    protected String name;
    protected String iconPath;

    protected List<Function<String, T>> mappers = new ArrayList<>();

    public Plugin(String name, String iconPath) {
        this.name = name;
        this.iconPath = iconPath;
    }

    public String getName() {
        return name;
    }

    public List<Result> query(String query) {
        return mappers.stream()
                .map(m -> m.apply(query))
                .filter(Objects::nonNull)
                .map(t -> toResult(t))
                .toList();
    }

    public abstract Result toResult(T t);
}
