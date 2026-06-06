package com.mxster.everyphants.model.plugin.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mxster.everyphants.model.Result;

import javafx.application.Platform;

public abstract class ReactivePlugin<T> extends Plugin {
    protected List<Function<String, T>> parsers = new ArrayList<>();
    protected List<Function<T, Result>> formatters = new ArrayList<>();

    private final List<Runnable> resultChangedListeners = new ArrayList<>();

    public ReactivePlugin(String name) {
        super(name);
    }

    public ReactivePlugin(String name, String iconPath) {
        super(name, iconPath);
    }

    public void addResultChangedListener(Runnable listener) {
        resultChangedListeners.add(listener);
    }

    protected void fireResultChanged() {
        if (resultChangedListeners.isEmpty()) {
            return;
        }
        Platform.runLater(() -> {
            for (Runnable listener : resultChangedListeners) {
                listener.run();
            }
        });
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
