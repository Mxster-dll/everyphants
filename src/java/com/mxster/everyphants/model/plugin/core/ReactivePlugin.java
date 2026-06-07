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

    public abstract T parse(String query);

    @Override
    public List<Result> query(String text) {
        T t = safeParse(text);
        if (t == null) {
            return Collections.emptyList();
        }

        List<Result> results = formatters.stream()
                .map(formatter -> formatter.apply(t))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        applyPluginIcon(results);

        return results;
    }

    private T safeParse(String text) {
        try {
            return parse(text);
        } catch (Exception e) {
            return null;
        }
    }
}
