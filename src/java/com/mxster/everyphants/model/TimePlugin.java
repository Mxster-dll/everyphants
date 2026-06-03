package com.mxster.everyphants.model;

import java.util.Date;

public class TimePlugin extends Plugin<Date> {
    public TimePlugin() {
        super("获取时间", null);

        parsers.add(this::parseToTime);
        formatters.add(this::toResult);
    }

    public Date parseToTime(String s) {
        if (s.toUpperCase().equals("TIME"))
            return new Date();
        else
            return null;
    }

    public Result toResult(Date date) {
        String s = date.toString();
        Result result = new Result(s, null, 1, null);

        return result;
    }
}
