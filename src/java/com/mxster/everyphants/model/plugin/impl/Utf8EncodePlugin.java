package com.mxster.everyphants.model.plugin.impl;

import java.nio.charset.StandardCharsets;

import com.mxster.everyphants.model.Result;

public class Utf8EncodePlugin extends EncodePlugin {

    @Override
    public Result build(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X ", b));
        }
        return new Result(hex.toString().trim(), "UTF-8 字节", 0.1);
    }
}
