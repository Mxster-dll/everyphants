package com.mxster.everyphants.view;

import java.io.InputStream;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class ResultItemFactory {

    private static final String ICON_RESOURCE_PATH = "/com/mxster/everyphants/icon/";
    private static final double ICON_SIZE = 24;

    public static Consumer<String> onCopyFeedback;

    private ResultItemFactory() {
    }

    public static Node create(String title, String body) {
        return create(title, body, null, null);
    }

    public static Node create(String title, String body, String iconPath) {
        return create(title, body, iconPath, null);
    }

    /**
     * 创建结果条目。
     *
     * @param title           标题
     * @param body            正文（可为 null）
     * @param iconPath        图标文件名，为 null 则不显示图标
     * @param backgroundColor CSS 背景色（如 "#ff0000"），为 null 则无彩色背景
     */
    public static Node create(String title, String body, String iconPath, String backgroundColor) {
        VBox item = new VBox();
        item.getStyleClass().add("result-item");

        // ---- 标题 ----
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("result-title");
        titleLabel.setOnMouseClicked(e -> copyToClipboard(titleLabel));

        // ---- 正文 ----
        Label bodyLabel = null;
        if (body != null && !body.isEmpty()) {
            Label lbl = new Label(body);
            lbl.getStyleClass().add("result-body");
            lbl.setOnMouseClicked(e -> copyToClipboard(lbl));
            bodyLabel = lbl;
        }

        // ---- 图标（放在标题+正文整体的左边） ----
        Node iconNode = buildIconNode(iconPath);
        if (iconNode != null) {
            // 文字组：标题在上，正文在下
            VBox textGroup = new VBox(titleLabel);
            if (bodyLabel != null)
                textGroup.getChildren().add(bodyLabel);

            HBox row = new HBox(iconNode, textGroup);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setSpacing(15);
            HBox.setMargin(iconNode, new Insets(0, 0, 0, 9));
            item.getChildren().add(row);
        } else {
            item.getChildren().add(titleLabel);
            if (bodyLabel != null)
                item.getChildren().add(bodyLabel);
        }

        // ---- 左侧高亮条 ----
        Region bar = new Region();
        bar.setPrefWidth(3);
        bar.setMaxWidth(3);
        bar.setMinWidth(3);
        bar.setMaxHeight(Region.USE_PREF_SIZE);
        bar.setStyle("-fx-background-color: #4cc2ff; -fx-background-radius: 3;");
        bar.setVisible(false);
        bar.setManaged(false);

        StackPane wrapper;
        if (backgroundColor != null && !backgroundColor.isEmpty()) {
            // 有色卡片：在最底层加一个圆角背景 Region
            Region bg = new Region();
            bg.setStyle("-fx-background-color: " + backgroundColor
                    + "; -fx-background-radius: 8;");
            bg.setMaxHeight(Region.USE_PREF_SIZE);
            wrapper = new StackPane(bg, item, bar);
            // 让背景填满 wrapper
            bg.prefWidthProperty().bind(wrapper.widthProperty());
            bg.prefHeightProperty().bind(wrapper.heightProperty());
        } else {
            wrapper = new StackPane(item, bar);
        }
        StackPane.setAlignment(bar, Pos.CENTER_LEFT);

        wrapper.hoverProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                bar.prefHeightProperty().bind(wrapper.heightProperty().multiply(0.35));
                bar.setVisible(true);
                bar.setManaged(true);
            } else {
                bar.prefHeightProperty().unbind();
                bar.setVisible(false);
                bar.setManaged(false);
            }
        });

        return wrapper;
    }

    /**
     * 更新已有条目的标题和正文（不改变图标）。
     */
    public static void updateText(Node wrapperNode, String title, String body) {
        StackPane wrapper = (StackPane) wrapperNode;
        VBox item = (VBox) wrapper.lookup(".result-item");
        if (item == null)
            return;

        // 查找标题并更新
        Label titleLabel = (Label) item.lookup(".result-title");
        if (titleLabel != null) {
            titleLabel.setText(title);
        }

        // 查找或创建正文
        Label bodyLabel = (Label) item.lookup(".result-body");
        if (body != null && !body.isEmpty()) {
            if (bodyLabel != null) {
                bodyLabel.setText(body);
            } else if (titleLabel != null) {
                // 把新 bodyLabel 加在 titleLabel 的同一父容器中
                Label newBodyLabel = new Label(body);
                newBodyLabel.getStyleClass().add("result-body");
                newBodyLabel.setOnMouseClicked(e -> copyToClipboard(newBodyLabel));
                ((javafx.scene.layout.Pane) titleLabel.getParent()).getChildren()
                        .add(newBodyLabel);
            }
        } else {
            if (bodyLabel != null) {
                ((javafx.scene.layout.Pane) bodyLabel.getParent()).getChildren()
                        .remove(bodyLabel);
            }
        }
    }

    // ---------- 图标构建 ----------

    /**
     * 根据图标文件名创建静态 ImageView。资源不存在时返回 null。
     */
    private static Node buildIconNode(String iconPath) {
        if (iconPath == null || iconPath.isEmpty()) {
            return null;
        }

        String resourcePath = ICON_RESOURCE_PATH + iconPath;
        InputStream stream = ResultItemFactory.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            return null;
        }

        try {
            Image img = new Image(stream);
            ImageView iv = new ImageView(img);
            iv.setPreserveRatio(true);
            iv.setFitWidth(ICON_SIZE);
            iv.setFitHeight(ICON_SIZE);
            iv.setSmooth(true);
            return iv;
        } catch (Exception e) {
            System.err.println("[ResultItemFactory] 无法加载图标 " + iconPath + ": " + e.getMessage());
            return null;
        }
    }

    // ---------- 剪贴板 ----------

    private static void copyToClipboard(Label label) {
        String text = label.getText();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);

        if (onCopyFeedback != null) {
            onCopyFeedback.accept(text);
        }
    }
}
