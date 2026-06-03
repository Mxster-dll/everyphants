package com.mxster.everyphants.model;

import java.util.ArrayList;
import java.util.List;

public class PluginManager {
    private static final List<Plugin<?>> plugins = new ArrayList<>();

    public void addPlugin(Plugin<?> plugin) {
        plugins.add(plugin);
    }

    public List<Plugin<?>> getPlugins() {
        return plugins;
    }
}
