package com.mxster.everyphants.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mxster.everyphants.model.plugin.core.Plugin;
import com.mxster.everyphants.model.plugin.impl.AnswerBookPlugin;
import com.mxster.everyphants.model.plugin.impl.Base64DecodePlugin;
import com.mxster.everyphants.model.plugin.impl.Base64EncodePlugin;
import com.mxster.everyphants.model.plugin.impl.BaseConversionPlugin;
import com.mxster.everyphants.model.plugin.impl.CaesarEncryptionPlugin;
import com.mxster.everyphants.model.plugin.impl.ColorPlugin;
import com.mxster.everyphants.model.plugin.impl.CountdownPlugin;
import com.mxster.everyphants.model.plugin.impl.EncodePlugin;
import com.mxster.everyphants.model.plugin.impl.EncryptionPlugin;
import com.mxster.everyphants.model.plugin.impl.FenceEncryptionPlugin;
import com.mxster.everyphants.model.plugin.impl.NumberToChinesePlugin;
import com.mxster.everyphants.model.plugin.impl.PrimeFactorizationPlugin;
import com.mxster.everyphants.model.plugin.impl.RandomPlugin;
import com.mxster.everyphants.model.plugin.impl.TimePlugin;
import com.mxster.everyphants.model.plugin.impl.TranslatePlugin;
import com.mxster.everyphants.model.plugin.impl.UrlDecodePlugin;
import com.mxster.everyphants.model.plugin.impl.UrlEncodePlugin;
import com.mxster.everyphants.model.plugin.impl.Utf8DecodePlugin;
import com.mxster.everyphants.model.plugin.impl.Utf8EncodePlugin;

public class PluginManager {
    private static final List<Plugin> plugins = new ArrayList<>(Arrays.asList(
            new BaseConversionPlugin(),
            new ColorPlugin(),
            new CaesarEncryptionPlugin(),
            new FenceEncryptionPlugin(),
            new NumberToChinesePlugin(),
            new Base64EncodePlugin(),
            new Utf8EncodePlugin(),
            new UrlEncodePlugin(),
            new PrimeFactorizationPlugin(),
            new RandomPlugin(),
            new TranslatePlugin(),
            new UrlDecodePlugin(),
            new Base64DecodePlugin(),
            new Utf8DecodePlugin(),
            new AnswerBookPlugin(),
            new CountdownPlugin("2026年高考", LocalDateTime.of(2026, 6, 7, 9, 0), "高考加油！"),
            // new CountdownPlugin("2026年CET4考试", LocalDateTime.of(2026, 6, 13, 9, 0),
            // "一遍过！"),
            new TimePlugin()));

    public List<Plugin> getPlugins() {
        return plugins;
    }
}
