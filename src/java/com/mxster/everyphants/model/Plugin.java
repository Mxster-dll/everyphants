package com.mxster.everyphants.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Plugin<T> {
    protected String name;
    protected String iconPath;

    protected List<Function<String, T>> parsers = new ArrayList<>();
    protected List<Function<T, Result>> formatters = new ArrayList<>();

    public Plugin(String name, String iconPath) {
        this.name = name;
        this.iconPath = iconPath;
    }

    public String getName() {
        return name;
    }

    public List<Result> query(String query) {
        List<T> nonNullResults = parsers.stream()
                .map(parser -> parser.apply(query))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (nonNullResults.isEmpty()) {
            return Collections.emptyList();
        }

        if (nonNullResults.size() > 1) {
            System.out.println("[Warning]: parser 冲突 (" + nonNullResults.size() + "), 已采用第一个匹配");
        }

        T selected = nonNullResults.get(0);
        return formatters.stream()
                .map(formatter -> formatter.apply(selected))
                .collect(Collectors.toList());
    }
}
