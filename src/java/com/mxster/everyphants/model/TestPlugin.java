package com.mxster.everyphants.model;

public class TestPlugin extends Plugin<Integer> {

    public TestPlugin(){
        super("进制转换", null);

        mappers.add(TestPlugin:: parseToBinary);
    }

    public static Integer parseToBinary(String s) {

        // Integer.toBinaryString(0);
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public Result toResult(Integer num){
        Result result = new Result(Integer.toBinaryString(num), null, 1, null);
        return result;
    }

}
