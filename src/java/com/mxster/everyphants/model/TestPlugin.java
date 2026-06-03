package com.mxster.everyphants.model;

import java.math.BigInteger;

public class TestPlugin extends Plugin<BigInteger> {

    public TestPlugin(){
        super("进制转换", null);

        mappers.add(TestPlugin:: parseToBinary);
    }

    public static BigInteger parseToBinary(String s) {

        // Integer.toBinaryString(0);
        try {
            return new BigInteger(s);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public Result toResult(BigInteger num){
        Result result = new Result(num.toString(2), null, 1, null);
        return result;
    }

}
