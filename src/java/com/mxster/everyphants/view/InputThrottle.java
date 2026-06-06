package com.mxster.everyphants.view;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class InputThrottle {
    private static final long MIN_INTERVAL_MS = 250;

    private long lastUpdateTime;
    private final PauseTransition throttle = new PauseTransition();
    private final Runnable action;

    public InputThrottle(Runnable action) {
        this.action = action;
        throttle.setOnFinished(e -> {
            lastUpdateTime = System.currentTimeMillis();
            action.run();
        });
    }

    public void trigger() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastUpdateTime;

        if (elapsed >= MIN_INTERVAL_MS) {
            throttle.stop();
            lastUpdateTime = System.currentTimeMillis();
            action.run();
        } else {
            throttle.stop();
            throttle.setDuration(Duration.millis(MIN_INTERVAL_MS - elapsed));
            throttle.play();
        }
    }
}
