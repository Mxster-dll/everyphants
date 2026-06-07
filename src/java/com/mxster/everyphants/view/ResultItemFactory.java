package com.mxster.everyphants.view;

import java.util.function.Consumer;

import javafx.scene.Node;

/**
 * 结果条目工厂 —— 创建和更新 {@link ResultItem} 组件。
 * <p>
 * 布局由 ResultItem.fxml 定义，样式由 controls.css 定义。
 */
public final class ResultItemFactory {

    private ResultItemFactory() {
    }

    /** @see ResultItem#onCopyFeedback */
    public static Consumer<String> onCopyFeedback;

    /**
     * @see #create(String, String, String, String)
     */
    public static Node create(String title, String body) {
        return create(title, body, null, null);
    }

    /**
     * @see #create(String, String, String, String)
     */
    public static Node create(String title, String body, String iconPath) {
        return create(title, body, iconPath, null);
    }

    /**
     * 创建结果条目，布局和样式由 FXML + CSS 驱动。
     */
    public static Node create(String title, String body, String iconPath, String backgroundColor) {
        ResultItem item = new ResultItem();
        item.setTitle(title);
        item.setBody(body);
        item.setIconPath(iconPath);
        item.setBackgroundColor(backgroundColor);
        return item;
    }

    /**
     * 更新已有条目的标题和正文（不改变图标和背景）。
     */
    public static void updateText(Node wrapperNode, String title, String body) {
        if (wrapperNode instanceof ResultItem item) {
            item.updateText(title, body);
        }
    }

    /**
     * 将全局复制回调注入到 ResultItem 组件。
     * MainController 初始化时调用。
     */
    public static void installCopyFeedback() {
        ResultItem.onCopyFeedback = onCopyFeedback;
    }
}
