package com.mxster.everyphants.plugin;

import java.time.Duration;
import java.time.LocalDateTime;

import com.mxster.everyphants.model.ProactivePlugin;
import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;

public class CountdownPlugin extends ProactivePlugin {
    private String eventName;
    private LocalDateTime targetTime;
    private String alertTitle;

    public CountdownPlugin(String eventName, LocalDateTime targetTime, String alertTitle) {
        super("倒计时", "日历.png");

        this.eventName = eventName;
        this.targetTime = targetTime;
        this.alertTitle = alertTitle;
    }

    @Override
    public Result build() {
        RefreshableResult result = new RefreshableResult("", "", 1.8);
        result.withRefresh(() -> refreshResult(result));
        refreshResult(result);
        return result;
    }

    private void refreshResult(RefreshableResult result) {
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(now, targetTime);

        long totalSeconds = diff.getSeconds();
        boolean isPast = totalSeconds <= 0;
        long absSeconds = Math.abs(totalSeconds);

        long days = absSeconds / 86400;
        long hours = (absSeconds % 86400) / 3600;
        long minutes = (absSeconds % 3600) / 60;
        long seconds = absSeconds % 60;

        String sign = isPast ? "已过" : "还有";
        String displayText = String.format("%s %d天 %02d:%02d:%02d", sign, days, hours, minutes, seconds);

        String title = isPast
                ? alertTitle
                : ("距离 " + eventName + " 还有 " + days + " 天");

        result.setTitle(title);
        result.setDisplayText(displayText);
    }
}
