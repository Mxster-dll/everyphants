package com.mxster.everyphants.model.plugin.impl;

import java.math.BigInteger;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class BaseConversionPlugin extends ReactivePlugin<BigInteger> {
    public BaseConversionPlugin() {
        super("进制转换", "更改.png");

        formatters.add(this::simpleBase);
    }

    @Override
    public BigInteger parse(String s) {
        return new BigInteger(s);
    }

    public Result simpleBase(BigInteger num) {
        String dec = addSpacesFromLow(num.toString(), 3);

        String hex = addSpacesFromLow(num.toString(16).toUpperCase(), 4);
        String bin = addSpacesFromLow(num.toString(2), 4);
        String oct = addSpacesFromLow(num.toString(8), 4);

        String text = String.format("Hex %s  ·  Bin %s  ·  Oct %s", hex, bin, oct);
        Result result = new Result(dec, text, 1);

        return result;
    }

    // 每添加 space 个字符，就加一个空格（最后一段不添加）
    private static String addSpacesFromLow(String text, int space) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder sb = new StringBuilder();

        int count = 0;
        for (int i = text.length() - 1; i >= 0; i--) {
            sb.append(text.charAt(i));
            count++;

            if (count % space == 0 && i != 0) {
                sb.append(' ');
            }
        }

        return sb.reverse().toString();
    }
}
