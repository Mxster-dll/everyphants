package com.mxster.everyphants.model;

import javafx.scene.paint.Color;

public class ColorPlugin extends Plugin<Color> {
    public ColorPlugin() {
        super("颜色", null);

        parsers.add(this::parseWebColor);
        formatters.add(this::hexColorText);
    }

    public Result hexColorText(Color color) {
        String colorText = color.toString().substring(2);
        if (colorText.endsWith("ff")) {
            colorText = colorText.substring(0, 6);
        }

        return new Result("#" + colorText, null, 1, null);
    }

    public Color parseWebColor(String s) {
        try {
            return Color.web(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
