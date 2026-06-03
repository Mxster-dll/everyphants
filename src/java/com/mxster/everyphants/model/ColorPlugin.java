package com.mxster.everyphants.model;

import javafx.scene.paint.Color;

public class ColorPlugin extends Plugin<Color> {

    public ColorPlugin(String name, String iconPath) {
        super(name, iconPath);

        mappers.add(ColorPlugin::parseWebColor);
    }

    @Override
    public Result toResult(Color t) {
        return new Result(t.toString(), null, 1, null);
    }

    private static Color parseWebColor(String s) {
        try {
            return Color.web(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
