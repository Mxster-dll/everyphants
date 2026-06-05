package com.mxster.everyphants.model;

import java.util.Date;

public class TimePlugin extends ProactivePlugin<Date> implements Refreshable {
    protected int refreshInterval = 1000;

    public TimePlugin() {
        super("时间", null);

        formatters.add(this::showTime);
    }

    @Override
    public int getRefreshInterval() {
        return refreshInterval;
    }

    @Override
    public void setRefreshInterval(int millis) {
        this.refreshInterval = millis;
    }

    @Override
    public Date fetch() {
        return new Date();
    }

    public Result showTime(Date date) {
        String time = date.toString();
        String timestamp = Long.toString(date.getTime());

        return new Result(time, timestamp, 2, null);
    }
}
