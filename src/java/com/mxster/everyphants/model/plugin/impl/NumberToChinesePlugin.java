package com.mxster.everyphants.model.plugin.impl;

import java.math.BigInteger;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class NumberToChinesePlugin extends ReactivePlugin<BigInteger> {
    private static final String[] DIGITS_UPPER = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
    private static final String[] UNITS_UPPER = { "", "拾", "佰", "仟" };
    private static final String[] BIG_UNITS_UPPER = { "", "万", "亿", "兆", "京", "垓", "秭", "穰", "沟", "涧", "正", "载", "极" };

    private static final String[] DIGITS_LOWER = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
    private static final String[] UNITS_LOWER = { "", "十", "百", "千" };
    private static final String[] BIG_UNITS_LOWER = { "", "万", "亿", "兆", "京", "垓", "秭", "穰", "沟", "涧", "正", "载", "极" };

    public NumberToChinesePlugin() {
        super("数字大小写", "汉字.png");
    }

    @Override
    public BigInteger parse(String s) {
        BigInteger num = new BigInteger(s.trim());
        return num.compareTo(BigInteger.ZERO) >= 0
                ? num
                : null;
    }

    @Override
    public Result build(BigInteger num) {
        String lower = convert(num, DIGITS_LOWER, UNITS_LOWER, BIG_UNITS_LOWER);
        String upper = convert(num, DIGITS_UPPER, UNITS_UPPER, BIG_UNITS_UPPER);
        return new Result(lower, upper, 0.5);
    }

    private static String convert(BigInteger num, String[] digits, String[] units, String[] bigUnits) {
        if (num.equals(BigInteger.ZERO)) {
            return digits[0];
        }

        String str = num.toString();
        int len = str.length();

        StringBuilder sb = new StringBuilder();
        int groupIdx = 0;

        int i = len;
        while (i > 0) {
            int start = Math.max(0, i - 4);
            String group = str.substring(start, i);
            i = start;

            String groupStr = convertGroup(group, digits, units);
            if (!groupStr.isEmpty()) {
                String unit = groupIdx < bigUnits.length ? bigUnits[groupIdx] : "";
                sb.insert(0, groupStr + unit);
            } else if (sb.length() > 0 && !sb.toString().startsWith(digits[0])) {
                sb.insert(0, digits[0]);
            }

            groupIdx++;
        }

        String result = sb.toString();
        result = result.replaceAll("零+", "零");
        result = result.replaceAll("零$", "");

        if (result.endsWith("万零")) {
            result = result.substring(0, result.length() - 1);
        }

        String tenPrefix = digits[1] + units[1];
        if (result.startsWith(tenPrefix)) {
            result = result.substring(1);
        }

        return result;
    }

    private static String convertGroup(String group, String[] digits, String[] units) {
        int len = group.length();
        StringBuilder sb = new StringBuilder();

        boolean hasValue = false;
        for (int i = 0; i < len; i++) {
            int digit = group.charAt(i) - '0';

            if (digit != 0) {
                if (!hasValue && sb.length() > 0) {
                    sb.append(digits[0]);
                }
                sb.append(digits[digit]);
                sb.append(units[len - 1 - i]);
                hasValue = true;
            } else {
                hasValue = false;
            }
        }

        return sb.toString();
    }
}
