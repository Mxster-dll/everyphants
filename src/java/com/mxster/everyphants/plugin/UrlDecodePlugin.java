package com.mxster.everyphants.plugin;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class UrlDecodePlugin extends DecodePlugin {
    public UrlDecodePlugin() {
        super("URL解码", "解码.png");
    }

    @Override
    public String parse(String s) {
        String decoded = URLDecoder.decode(s, StandardCharsets.UTF_8);
        return decoded.equals(s)
                ? null
                : decoded;
    }
}
