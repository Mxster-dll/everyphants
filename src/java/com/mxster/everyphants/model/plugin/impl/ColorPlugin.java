package com.mxster.everyphants.model.plugin.impl;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

import javafx.scene.paint.Color;

public class ColorPlugin extends ReactivePlugin<Color> {
    public ColorPlugin() {
        super("颜色", "颜色.png");
    }

    @Override
    public Color parse(String s) {
        return Color.web(s);
    }

    @Override
    public Result build(Color color) {
        String hex = color.toString().substring(2);

        String title = "#" +
                (hex.endsWith("ff") ? hex.substring(0, 6) : hex);

        double alpha = color.getOpacity() * 100;

        int red = (int) Math.round(color.getRed() * 255);
        int green = (int) Math.round(color.getGreen() * 255);
        int blue = (int) Math.round(color.getBlue() * 255);

        double hue = color.getHue();
        double saturation = color.getSaturation() * 100;
        double brightness = color.getBrightness() * 100;

        String displayText = String.format(
                "[%.0f%%]  #%s  ·  RGB %d %d %d  ·  HSB %.1f° %.1f%% %.1f%%",
                alpha, hex, red, green, blue, hue, saturation, brightness);

        Result result = new Result(title, displayText, 1);
        // 卡片背景色 = 用户输入的颜色
        result.setBackgroundColor(String.format("#%02X%02X%02X", red, green, blue));
        return result;
    }
}
