package com.mxster.everyphants.model.plugin.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.mxster.everyphants.model.Result;

public class Base64EncodePlugin extends EncodePlugin {
    @Override
    public Result build(String s) {
        String encoded = Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
        return new Result(encoded, "Base64编码", 0.1);
    }

}
