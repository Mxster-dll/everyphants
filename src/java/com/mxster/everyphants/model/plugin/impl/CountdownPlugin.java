package com.mxster.everyphants.model.plugin.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ProactivePlugin;

public class CountdownPlugin extends ProactivePlugin<Void> {
    public record CountdownItem(String eventName, LocalDateTime targetTime, String alertTitle) {
    }

    private final List<CountdownItem> items;
    private final Map<CountdownItem, RefreshableResult> cache = new ConcurrentHashMap<>();

    public CountdownPlugin() {
        super("倒计时", "日历.png");

        items = List.of(
                new CountdownItem("2026年高考", LocalDateTime.of(2026, 6, 7, 9, 0), "高考加油！"),
                new CountdownItem("2026年CET4考试", LocalDateTime.of(2026, 6, 13, 9, 0), "一遍过！"));
    }

    @Override
    public Void fetch() {
        return null;
    }

    @Override
    public List<Result> query() {
        List<Result> results = new ArrayList<>();

        for (CountdownItem item : items) {
            RefreshableResult result = cache.computeIfAbsent(item, k -> {
                RefreshableResult r = new RefreshableResult("", "", 1.8, null);
                r.withRefresh(0, () -> refreshResult(r, k));
                return r;
            });

            refreshResult(result, item);
            results.add(result);
        }

        if (iconFile != null && !iconFile.isEmpty()) {
            for (Result r : results) {
                if (r.getIconPath() == null || r.getIconPath().isEmpty()) {
                    r.setIconPath(iconFile);
                }
            }
        }

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
