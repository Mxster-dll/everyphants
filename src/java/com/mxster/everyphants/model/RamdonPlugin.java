package com.mxster.everyphants.model;

import java.util.Random;

public class RamdonPlugin extends Plugin<String>{
    public RamdonPlugin() {
        super("生成随机数", null);

        parsers.add(this::parseToRandom);
        formatters.add(this::rand);
    }

    public String parseToRandom(String s){
        if (s.toUpperCase().equals("RANDOM")) {
            return s;  
        } else {
            return null;
        }
    }

     public Result rand(String s) {
        Random random = new Random();
        int n = random.nextInt(90) + 10;
        String str = Integer.toString(n);

        Result result = new Result(str, null, 1, null);

        return result;
    }
}


