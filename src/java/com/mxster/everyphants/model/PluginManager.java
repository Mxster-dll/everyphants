package com.mxster.everyphants.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mxster.everyphants.model.plugin.core.Plugin;
import com.mxster.everyphants.model.plugin.impl.AnswerBookPlugin;
import com.mxster.everyphants.model.plugin.impl.Base64DecodePlugin;
import com.mxster.everyphants.model.plugin.impl.BaseConversionPlugin;
import com.mxster.everyphants.model.plugin.impl.ColorPlugin;
import com.mxster.everyphants.model.plugin.impl.EncodingPlugin;
import com.mxster.everyphants.model.plugin.impl.EncryptionPlugin;
import com.mxster.everyphants.model.plugin.impl.NumberToChinesePlugin;
import com.mxster.everyphants.model.plugin.impl.PrimeFactorizationPlugin;
import com.mxster.everyphants.model.plugin.impl.RandomPlugin;
import com.mxster.everyphants.model.plugin.impl.TimePlugin;
import com.mxster.everyphants.model.plugin.impl.TranslatePlugin;
import com.mxster.everyphants.model.plugin.impl.UrlDecodePlugin;
import com.mxster.everyphants.model.plugin.impl.Utf8DecodePlugin;

public class PluginManager {
    private static final List<Plugin> plugins = new ArrayList<>(Arrays.asList(
            new BaseConversionPlugin(),
            new ColorPlugin(),
            new EncryptionPlugin(),
            new NumberToChinesePlugin(),
            new EncodingPlugin(),
            new PrimeFactorizationPlugin(),
            new RandomPlugin(),
            new TranslatePlugin(),
            new UrlDecodePlugin(),
            new Base64DecodePlugin(),
            new Utf8DecodePlugin(),
            new AnswerBookPlugin(),
            new TimePlugin()));

    public List<Plugin> getPlugins() {
        return plugins;
    }
}
