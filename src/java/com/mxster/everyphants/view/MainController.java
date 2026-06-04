package com.mxster.everyphants.view;

import com.mxster.everyphants.model.PluginManager;
import com.mxster.everyphants.model.TranslatePlugin;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML 控制器 —— 输入框内容变化时输出到终端。
 */
public class MainController {

    @FXML
    private StackPane rootPane;
    @FXML
    private TextField inputField;
    @FXML
    private Separator divider1;
    @FXML
    private VBox resultList;
    @FXML
    private Separator divider2;
    @FXML
    private Pane infoPane;

    private Stage stage;
    private double dragX, dragY;

    /** 节流：两次更新之间至少间隔 250ms，首次更新立即执行 */
    private static final long MIN_UPDATE_INTERVAL = 250; // ms
    private long lastUpdateTime = 0;
    private final PauseTransition throttle = new PauseTransition();

    public void init(Stage stage) {
        this.stage = stage;

        // ── 窗口拖拽 ──
        rootPane.setOnMousePressed(this::onMousePressed);
        rootPane.setOnMouseDragged(this::onMouseDragged);

        PluginManager manager = new PluginManager();

        // ── 为翻译插件设置异步回调，翻译完成后自动刷新界面 ──
        for (var plugin : manager.getPlugins()) {
            if (plugin instanceof TranslatePlugin tp) {
                tp.setOnResultReady(() -> doUpdate(manager));
            }
        }

        // ── 节流更新：延迟到期时用最新文本刷新结果列表 ──
        throttle.setOnFinished(e -> {
            doUpdate(manager);
        });

        // ── 内容变化 → 节流处理 ──
        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            long now = System.currentTimeMillis();
            long elapsed = now - lastUpdateTime;

            if (elapsed >= MIN_UPDATE_INTERVAL) {
                // 距上次更新已超过 250ms，立即更新
                throttle.stop();
                doUpdate(manager);
            } else {
                // 距上次更新不足 250ms，推迟到剩余时间后更新
                throttle.stop();
                throttle.setDuration(Duration.millis(MIN_UPDATE_INTERVAL - elapsed));
                throttle.play();
            }
        });

        // 初始状态：无结果，隐藏底部区域
        updateResultVisibility();
    }

    /** 执行一次结果列表更新，并记录更新时间 */
    private void doUpdate(PluginManager manager) {
        lastUpdateTime = System.currentTimeMillis();

        String text = inputField.getText();
        resultList.getChildren().clear();
        if (text == null || text.isEmpty()) {
            updateResultVisibility();
            return;
        }

        String trimmed = text.trim();
        for (var plugin : manager.getPlugins()) {
            for (var r : plugin.query(trimmed)) {
                resultList.getChildren().add(createResultItem(r.title, r.displayText));
            }
        }

        updateResultVisibility();
    }

    /** 根据 resultList 是否有子组件来控制底部区域的显隐 */
    private void updateResultVisibility() {
        boolean hasResults = !resultList.getChildren().isEmpty();
        resultList.setVisible(hasResults);
        resultList.setManaged(hasResults);
        divider1.setVisible(hasResults);
        divider1.setManaged(hasResults);
        divider2.setVisible(hasResults);
        divider2.setManaged(hasResults);
        infoPane.setVisible(hasResults);
        infoPane.setManaged(hasResults);
        stage.sizeToScene();
    }

    private void onMousePressed(MouseEvent e) {
        dragX = stage.getX() - e.getScreenX();
        dragY = stage.getY() - e.getScreenY();
    }

    private void onMouseDragged(MouseEvent e) {
        stage.setX(e.getScreenX() + dragX);
        stage.setY(e.getScreenY() + dragY);
    }

    /** 构建一条结果项：标题竖直居中左对齐；有正文时二者作为整体竖直居中左对齐 */
    private Node createResultItem(String title, String body) {
        VBox item = new VBox();
        item.getStyleClass().add("result-item");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("result-title");
        item.getChildren().add(titleLabel);

        if (body != null && !body.isEmpty()) {
            Label bodyLabel = new Label(body);
            bodyLabel.getStyleClass().add("result-body");
            item.getChildren().add(bodyLabel);
        }

        return item;
    }
}
