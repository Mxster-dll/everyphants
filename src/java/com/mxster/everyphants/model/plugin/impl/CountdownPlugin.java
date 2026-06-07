package com.mxster.everyphants.model.plugin.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ProactivePlugin;

public class CountdownPlugin extends ProactivePlugin {
    public record CountdownItem(String eventName, LocalDateTime targetTime, String alertTitle) {
    }

    private final List<CountdownItem> items;

    public CountdownPlugin() {
        super("倒计时", "日历.png");

        items = List.of(
                new CountdownItem("2026年高考", LocalDateTime.of(2026, 6, 7, 9, 0), "高考加油！"),
                new CountdownItem("2026年CET4考试", LocalDateTime.of(2026, 6, 13, 9, 0), "一遍过！"));
    }

    @Override
    protected List<Result> buildResult() {
        List<Result> results = new ArrayList<>();

        for (CountdownItem item : items) {
            RefreshableResult r = new RefreshableResult("", "", 1.8);
            r.withRefresh(0, () -> refreshResult(r, item));
            refreshResult(r, item);
            results.add(r);
        }

        applyPluginIcon(results);

        return results;
    }

    private void refreshResult(RefreshableResult result, CountdownItem item) {
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(now, item.targetTime);

        long totalSeconds = diff.getSeconds();
        boolean isPast = totalSeconds <= 0;
        long absSeconds = Math.abs(totalSeconds);

        long days = absSeconds / 86_400;
        long hours = (absSeconds % 86_400) / 3_600;
        long minutes = (absSeconds % 3_600) / 60;
        long seconds = absSeconds % 60;

        String sign = isPast ? "已过" : "还有";
        String displayText = String.format("%s %d天 %02d:%02d:%02d", sign, days, hours, minutes, seconds);

        String title = isPast
                ? item.alertTitle
                : ("距离 " + item.eventName + " 还有 " + days + " 天");

        result.setTitle(title);
        result.setDisplayText(displayText);
    }
}
