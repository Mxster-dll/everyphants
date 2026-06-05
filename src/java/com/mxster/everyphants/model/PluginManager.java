package com.mxster.everyphants.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginManager {
    private static final List<Plugin> plugins = new ArrayList<>(Arrays.asList(
            new BaseConversionPlugin(),
            new ColorPlugin(),
            new RandomPlugin(),
            new TranslatePlugin(),
            new AnswerBookPlugin(),
            new TimePlugin()));

    public List<Plugin> getPlugins() {
        return plugins;
    }
}
