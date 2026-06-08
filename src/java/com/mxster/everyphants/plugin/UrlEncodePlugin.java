package com.mxster.everyphants.plugin;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.mxster.everyphants.model.Result;

public class UrlEncodePlugin extends EncodePlugin {
    @Override
    public Result build(String s) {
        String encoded = URLEncoder.encode(s, StandardCharsets.UTF_8);
        return new Result(encoded, "URL编码", 0.1);
    }
}
