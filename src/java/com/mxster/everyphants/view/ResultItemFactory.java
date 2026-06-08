package com.mxster.everyphants.view;

import javafx.scene.Node;

public final class ResultItemFactory {

    private ResultItemFactory() {
    }

    public static Node create(String title, String body) {
        return create(title, body, null, null);
    }

    public static Node create(String title, String body, String iconPath) {
        return create(title, body, iconPath, null);
    }

    public static Node create(String title, String body, String iconPath, String backgroundColor) {
        ResultItem item = new ResultItem();
        item.setTitle(title);
        item.setBody(body);
        item.setIconPath(iconPath);
        item.setBackgroundColor(backgroundColor);
        return item;
    }

    public static void updateText(Node wrapperNode, String title, String body) {
        if (wrapperNode instanceof ResultItem item) {
            item.setTitle(title);
            item.setBody(body);
        }
    }
}
