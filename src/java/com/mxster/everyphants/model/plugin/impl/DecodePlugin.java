package com.mxster.everyphants.model.plugin.impl;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.Plugin;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public abstract class DecodePlugin extends ReactivePlugin<String> {
    public DecodePlugin(String name, String iconPath) {
        super(name, iconPath);
    }

    @Override
    public Result build(String s) {
        if (!Plugin.isReadableText(s)) {
            return null;
        }
        return new Result(s, super.name, 0.1);
    }
}
