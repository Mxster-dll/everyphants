package com.mxster.everyphants.model.plugin.impl;

import java.math.BigInteger;
import java.util.Random;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class RandomPlugin extends ReactivePlugin<BigInteger> {
    public RandomPlugin() {
        super("生成随机数");

        parsers.add(this::parseToUpperBound);
        formatters.add(this::rand);
    }

    public BigInteger parseToUpperBound(String s) {
        try {
            var num = new BigInteger(s);

            if (num.compareTo(BigInteger.ZERO) <= 0) {
                return null;
            }

            return num;
        } catch (Exception e) {
            return null;
        }
    }

    public Result rand(BigInteger num) {
        Random random = new Random();

        BigInteger result;
        do {
            result = new BigInteger(num.bitLength(), random);
        } while (result.compareTo(num) > 0);

        String range = "随机数 [0, " + num.toString() + "]";
        return new Result(result.toString(), range, 1, null);
    }
}
