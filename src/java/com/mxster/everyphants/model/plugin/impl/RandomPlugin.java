package com.mxster.everyphants.model.plugin.impl;

import java.math.BigInteger;
import java.util.Random;

import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class RandomPlugin extends ReactivePlugin<BigInteger> {
    private static final Random RANDOM = new Random();

    public RandomPlugin() {
        super("生成随机数", "随机.png");

        formatters.add(this::buildResult);
    }

    @Override
    public BigInteger parse(String s) {
        BigInteger num = new BigInteger(s);
        return num.compareTo(BigInteger.ZERO) > 0
                ? num
                : null;
    }

    public Result buildResult(BigInteger num) {
        String range = String.format("随机数 [0, %s]", num.toString());
        RefreshableResult result = new RefreshableResult(
                random(num).toString(), range, 1);
        result.withRefresh(0, () -> result.setTitle(random(num).toString()));
        return result;
    }

    private static BigInteger random(BigInteger bound) {
        BigInteger result;
        do {
            result = new BigInteger(bound.bitLength(), RANDOM);
        } while (result.compareTo(bound) > 0);

        return result;
    }
}
