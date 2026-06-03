package com.mxster.everyphants.model;

import java.math.BigInteger;

public class BaseConversionPlugin extends Plugin<BigInteger> {

    public BaseConversionPlugin(){
        super("进制转换", null);

        mappers.add(BaseConversionPlugin:: parseToBinary);
    }

    public static BigInteger parseToBinary(String s) {
        try {
            return new BigInteger(s);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public Result toResult(BigInteger num){
        String title = "Bin:" + num.toString(2) + " Oct:" + num.toString(8) + " Hex:" + num.toString(16);
        Result result = new Result(title, null, 1, null);
        
        return result;
    }

}
