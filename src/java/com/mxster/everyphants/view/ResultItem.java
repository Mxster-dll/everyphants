package com.mxster.everyphants.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * 结果卡片组件（FXML + CSS 驱动布局和样式）。
 * <p>
 * 使用 {@link #setTitle} / {@link #setBody} / {@link #setIconPath} /
 * {@link #setBackgroundColor} 绑定数据。
 */
public class ResultItem extends StackPane {

    private static final String ICON_RESOURCE_PATH = "/com/mxster/everyphants/icon/";

    public static Consumer<String> onCopyFeedback;

    @FXML private Region colorBg;
    @FXML private ImageView iconView;
    @FXML private Label titleLabel;
    @FXML private Label bodyLabel;
    @FXML private Region bar;

    public ResultItem() {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mxster/everyphants/fxml/ResultItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("无法加载 ResultItem.fxml", e);
        }

        // 标题点击 → 复制
        titleLabel.setOnMouseClicked(e -> copyToClipboard(titleLabel.getText()));
        bodyLabel.setOnMouseClicked(e -> copyToClipboard(bodyLabel.getText()));

        // 高亮条固定在左侧
        bar.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        javafx.scene.layout.StackPane.setAlignment(bar, javafx.geometry.Pos.CENTER_LEFT);

        // 悬停时显示高亮条
        hoverProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                bar.prefHeightProperty().bind(heightProperty().multiply(0.35));
                bar.setVisible(true);
                bar.setManaged(true);
            } else {
                bar.prefHeightProperty().unbind();
                bar.setVisible(false);
                bar.setManaged(false);
            }
        });
    }

    // ────────── 数据绑定 ──────────

    public void setTitle(String text) {
        titleLabel.setText(text != null ? text : "");
    }

    public void setBody(String text) {
        if (text != null && !text.isEmpty()) {
            bodyLabel.setText(text);
            bodyLabel.setVisible(true);
            bodyLabel.setManaged(true);
        } else {
            bodyLabel.setText("");
            bodyLabel.setVisible(false);
            bodyLabel.setManaged(false);
        }
    }

    /** 同时更新标题和正文（用于 RefreshableResult 刷新场景）。 */
    public void updateText(String title, String body) {
        setTitle(title);
        setBody(body);
    }

    public void setIconPath(String iconPath) {
        Image icon = loadIcon(iconPath);
        if (icon != null) {
            iconView.setImage(icon);
            iconView.setVisible(true);
            iconView.setManaged(true);
        } else {
            iconView.setImage(null);
            iconView.setVisible(false);
            iconView.setManaged(false);
        }
    }

    public void setBackgroundColor(String cssColor) {
        if (cssColor != null && !cssColor.isEmpty()) {
            colorBg.setStyle("-fx-background-color: " + cssColor + ";");
            colorBg.setVisible(true);
            colorBg.setManaged(true);
            // 让背景填满卡片
            colorBg.prefWidthProperty().bind(widthProperty());
            colorBg.prefHeightProperty().bind(heightProperty());
        } else {
            colorBg.setStyle(null);
            colorBg.setVisible(false);
            colorBg.setManaged(false);
            colorBg.prefWidthProperty().unbind();
            colorBg.prefHeightProperty().unbind();
        }
    }

    // ────────── 辅助 ──────────

    private static Image loadIcon(String iconPath) {
        if (iconPath == null || iconPath.isEmpty()) {
            return null;
        }
        InputStream stream = ResultItem.class.getResourceAsStream(
                ICON_RESOURCE_PATH + iconPath);
        if (stream == null) {
            return null;
        }
        try {
            return new Image(stream);
        } catch (Exception e) {
            System.err.println("[ResultItem] 无法加载图标 " + iconPath + ": " + e.getMessage());
            return null;
        }
    }

    private static void copyToClipboard(String text) {
        if (text == null || text.isEmpty()) return;
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);

        if (onCopyFeedback != null) {
            onCopyFeedback.accept(text);
        }
    }
}
