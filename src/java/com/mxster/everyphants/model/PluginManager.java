package com.mxster.everyphants.model;

import java.time.LocalDateTime;
import java.util.List;

import com.mxster.everyphants.plugin.AnswerBookPlugin;
import com.mxster.everyphants.plugin.Base64DecodePlugin;
import com.mxster.everyphants.plugin.Base64EncodePlugin;
import com.mxster.everyphants.plugin.BaseConversionPlugin;
import com.mxster.everyphants.plugin.CaesarEncryptionPlugin;
import com.mxster.everyphants.plugin.ColorPlugin;
import com.mxster.everyphants.plugin.CountdownPlugin;
import com.mxster.everyphants.plugin.FenceEncryptionPlugin;
import com.mxster.everyphants.plugin.NumberToChinesePlugin;
import com.mxster.everyphants.plugin.PrimeFactorizationPlugin;
import com.mxster.everyphants.plugin.RandomPlugin;
import com.mxster.everyphants.plugin.TimePlugin;
import com.mxster.everyphants.plugin.TranslatePlugin;
import com.mxster.everyphants.plugin.UrlDecodePlugin;
import com.mxster.everyphants.plugin.UrlEncodePlugin;
import com.mxster.everyphants.plugin.Utf8DecodePlugin;
import com.mxster.everyphants.plugin.Utf8EncodePlugin;

public class PluginManager {
    private static final List<Plugin> plugins = List.of(
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
            new CountdownPlugin("2026年考研", LocalDateTime.of(2026, 12, 25, 9, 0), "必上岸！"),
            new CountdownPlugin("2026年CET4考试", LocalDateTime.of(2026, 6, 13, 9, 0), "四级必过！"),
            new TimePlugin());

    public List<Plugin> getPlugins() {
        return plugins;
    }
}
