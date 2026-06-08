package com.mxster.everyphants.model.plugin.impl;

import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public abstract class EncodePlugin extends ReactivePlugin<String> {
    public EncodePlugin() {
        super("编码", "编码.png");
    }

    @Override
    public String parse(String query) {
        return query.matches("\\d+") ? null : query;
    }
}
