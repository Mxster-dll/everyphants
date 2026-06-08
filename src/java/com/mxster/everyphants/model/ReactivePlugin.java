package com.mxster.everyphants.model;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

public abstract class ReactivePlugin<T> extends Plugin {
    private final List<Runnable> resultChangedListeners = new ArrayList<>();

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

    public abstract Result build(T t);

    @Override
    public Result query(String text) {
        T t = safeParse(text);
        if (t == null) {
            return null;
        }

        Result result = build(t);
        if (result == null) {
            return null;
        }

        applyPluginIcon(result);

        return result;
    }

    private T safeParse(String text) {
        try {
            return parse(text);
        } catch (Exception e) {
            return null;
        }
    }
}
