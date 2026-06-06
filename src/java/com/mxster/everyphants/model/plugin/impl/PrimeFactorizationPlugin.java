package com.mxster.everyphants.model.plugin.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class PrimeFactorizationPlugin extends ReactivePlugin<BigInteger> {

    private static final BigInteger TWO = BigInteger.TWO;

    private final Map<BigInteger, Result> cache = new ConcurrentHashMap<>();

    public PrimeFactorizationPlugin() {
        super("质因数分解");

        parsers.add(this::parseToPositiveInteger);
        formatters.add(this::formatPrimeFactorization);
    }

    public BigInteger parseToPositiveInteger(String s) {
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

    public Result formatPrimeFactorization(BigInteger num) {
        return cache.computeIfAbsent(num, key -> {
            Result result = new Result("计算中…", key + " 正在分解…", 1, null);

            new Thread(() -> {
                List<BigInteger> factors = primeFactorize(key);

                StringBuilder sb = new StringBuilder();
                BigInteger prev = null;
                int count = 0;
                for (BigInteger f : factors) {
                    if (f.equals(prev)) {
                        count++;
                    } else {
                        if (prev != null) {
                            appendFactor(sb, prev, count);
                            sb.append(" × ");
                        }
                        prev = f;
                        count = 1;
                    }
                }
                appendFactor(sb, prev, count);

                result.setTitle(sb.toString());
                fireResultChanged();
            }, "PrimeFactorDivider").start();

            return result;
        });
    }

    private static void appendFactor(StringBuilder sb, BigInteger prime, int exp) {
        sb.append(prime);
        if (exp > 1) {
            sb.append(toSuperscript(exp));
        }
    }

    private static String toSuperscript(int n) {
        StringBuilder sb = new StringBuilder();
        for (char c : String.valueOf(n).toCharArray()) {
            char indexBit = switch (c) {
                case '0' -> '\u2070';
                case '1' -> '\u00B9';
                case '2' -> '\u00B2';
                case '3' -> '\u00B3';
                case '4' -> '\u2074';
                case '5' -> '\u2075';
                case '6' -> '\u2076';
                case '7' -> '\u2077';
                case '8' -> '\u2078';
                case '9' -> '\u2079';
                default -> throw new IllegalArgumentException("未知的字符：" + c);
            };

            sb.append(indexBit);
        }
        return sb.toString();
    }

    private List<BigInteger> primeFactorize(BigInteger num) {
        List<BigInteger> factors = new ArrayList<>();
        if (num.compareTo(BigInteger.ONE) <= 0) {
            return factors;
        }

        BigInteger n = num;

        while (n.mod(TWO).equals(BigInteger.ZERO)) {
            factors.add(TWO);
            n = n.divide(TWO);
        }

        BigInteger i = BigInteger.valueOf(3);
        while (i.multiply(i).compareTo(n) <= 0) {
            while (n.mod(i).equals(BigInteger.ZERO)) {
                factors.add(i);
                n = n.divide(i);
            }
            i = i.add(TWO);
        }

        if (n.compareTo(BigInteger.ONE) > 0) {
            factors.add(n);
        }

        return factors;
    }
}
