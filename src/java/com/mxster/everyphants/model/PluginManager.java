package com.mxster.everyphants.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginManager {
    private static final List<Plugin<?>> plugins = new ArrayList<>(Arrays.asList(
            new TimePlugin(),
            new BaseConversionPlugin(),
            new ColorPlugin()));

    public List<Plugin<?>> getPlugins() {
        return plugins;
    }
}
