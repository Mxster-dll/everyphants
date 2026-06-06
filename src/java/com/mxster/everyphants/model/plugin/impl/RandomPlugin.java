package com.mxster.everyphants.model.plugin.impl;

import java.math.BigInteger;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class RandomPlugin extends ReactivePlugin<BigInteger> {

    private final Map<BigInteger, RefreshableResult> cache = new ConcurrentHashMap<>();

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
        return cache.computeIfAbsent(num, key -> {
            String range = "随机数 [0, " + key.toString() + "]";
            RefreshableResult result = new RefreshableResult(
                    generate(key).toString(), range, 1, null);
            result.withRefresh(0, () -> {
                result.setTitle(generate(key).toString());
            });
            return result;
        });
    }

    private static BigInteger generate(BigInteger bound) {
        Random random = new Random();
        BigInteger result;
        do {
            result = new BigInteger(bound.bitLength(), random);
        } while (result.compareTo(bound) > 0);
        return result;
    }
}
