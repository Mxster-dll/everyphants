package com.mxster.everyphants.model.plugin.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64DecodePlugin extends DecodePlugin {
    public Base64DecodePlugin() {
        super("Base64解码", "解码.png");
    }

    @Override
    public String parse(String s) {
        byte[] decoded = Base64.getDecoder().decode(s.trim());
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
