package com.mxster.everyphants.view;

import com.mxster.everyphants.model.PluginManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    public void init(Stage stage) {
        this.stage = stage;

        // ── 窗口拖拽 ──
        rootPane.setOnMousePressed(this::onMousePressed);
        rootPane.setOnMouseDragged(this::onMouseDragged);

        PluginManager manager = new PluginManager();

        // ── 内容变化 → 终端输出 ──
        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            resultList.getChildren().clear();
            if (newVal == null || newVal.isEmpty()) {
                updateResultVisibility();
                return;
            }

            String text = newVal.trim();

            for (var plugin : manager.getPlugins()) {
                for (var r : plugin.query(text)) {
                    var label = new Label(r.title);
                    label.getStyleClass().add("result-item");
                    resultList.getChildren().add(label);
                }
            }

            updateResultVisibility();
        });

        // 初始状态：无结果，隐藏底部区域
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
}
