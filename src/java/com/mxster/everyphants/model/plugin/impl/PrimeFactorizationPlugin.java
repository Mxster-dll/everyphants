package com.mxster.everyphants.model.plugin.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.LoadingResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class PrimeFactorizationPlugin extends ReactivePlugin<BigInteger> {
    private static final BigInteger TWO = BigInteger.TWO;

    private final Map<BigInteger, RefreshableResult> cache = new ConcurrentHashMap<>();

    public PrimeFactorizationPlugin() {
        super("质因数分解", "计算.png");

        formatters.add(this::formatPrimeFactorization);
    }

    @Override
    public BigInteger parse(String s) {
        BigInteger num = new BigInteger(s);
        return num.compareTo(BigInteger.ONE) > 0
                ? num
                : null;
    }

    public Result formatPrimeFactorization(BigInteger num) {
        return cache.computeIfAbsent(num, key -> {
            LoadingResult result = new LoadingResult("计算中", key + " 正在分解", 1);

            new Thread(() -> {
                try {
                    List<BigInteger> factors = primeFactorize(key);

                    StringBuilder sb = new StringBuilder();
                    BigInteger prev = null;
                    int count = 0;
                    for (BigInteger f : factors) {
                        if (f.equals(prev)) {
                            count++;
                        } else {
                            if (prev != null) {
                                appendFactorTitle(sb, prev, count);
                                sb.append(" × ");
                            }
                            prev = f;
                            count = 1;
                        }
                    }
                    if (prev != null) {
                        appendFactorTitle(sb, prev, count);
                    }

                    String titleStr = sb.toString();
                    String displayStr = toDisplayFormat(titleStr);
                    result.finish(titleStr, key + " = " + displayStr);
                } catch (Exception e) {
                    result.finish("分解失败", key + " 分解失败");
                }
                fireResultChanged();
            }, "PrimeFactorDivider").start();

            return result;
        });
    }

    private static void appendFactorTitle(StringBuilder sb, BigInteger prime, int exp) {
        sb.append(prime);
        if (exp > 1) {
            sb.append(toSuperscript(exp));
        }
    }

    private static String toDisplayFormat(String title) {
        return title
                .replace(" × ", " * ")
                .replace("\u2070", "^0")
                .replace("\u00B9", "^1")
                .replace("\u00B2", "^2")
                .replace("\u00B3", "^3")
                .replace("\u2074", "^4")
                .replace("\u2075", "^5")
                .replace("\u2076", "^6")
                .replace("\u2077", "^7")
                .replace("\u2078", "^8")
                .replace("\u2079", "^9");
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
