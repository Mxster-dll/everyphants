package com.mxster.everyphants.model;

import java.math.BigInteger;

public class BaseConversionPlugin extends Plugin<BigInteger> {

    public BaseConversionPlugin() {
        super("进制转换", null);

        parsers.add(this::parseFromDec);
        parsers.add(this::parseFromBin);
        parsers.add(this::parseFromHex);

        formatters.add(this::simpleBase);
    }

    public BigInteger parseFromDec(String s) {
        try {
            return new BigInteger(s);
        } catch (Exception e) {
            return null;
        }
    }

    public BigInteger parseFromBin(String s) {
        if (!s.toLowerCase().startsWith("0b")) {
            return null;
        }

        try {
            return new BigInteger(s.substring(2), 2);
        } catch (Exception e) {
            return null;
        }
    }

    public BigInteger parseFromHex(String s) {
        if (!s.toLowerCase().startsWith("0x")) {
            return null;
        }

        try {
            return new BigInteger(s.substring(2), 16);
        } catch (Exception e) {
            return null;
        }
    }

    public Result simpleBase(BigInteger num) {
        String dec = addSpacesFromLow(num.toString(), 3);

        String hex = addSpacesFromLow(num.toString(16).toUpperCase(), 4);
        String bin = addSpacesFromLow(num.toString(2), 4);
        String oct = addSpacesFromLow(num.toString(8), 4);

        String text = String.format("Hex %s  ·  Bin %s  ·  Oct %s", hex, bin, oct);
        Result result = new Result(dec, text, 1, null);

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
