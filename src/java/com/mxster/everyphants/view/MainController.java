package com.mxster.everyphants.view;

import com.mxster.everyphants.model.ColorPlugin;
import com.mxster.everyphants.model.Plugin;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
    private Pane resultList;
    @FXML
    private Pane infoPane;

    private Stage stage;
    private double dragX, dragY;

    /** 由 App.java 注入 Stage，设置拖拽与毛玻璃 */
    public void init(Stage stage) {
        this.stage = stage;

        // ── 窗口拖拽 ──
        rootPane.setOnMousePressed(this::onMousePressed);
        rootPane.setOnMouseDragged(this::onMouseDragged);
        var plugin = new ColorPlugin("颜色", null);
        // ── 内容变化 → 终端输出 ──
        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            resultList.getChildren().clear();
            if (newVal != null) {
                System.out.println(newVal);
                var rs = plugin.query(newVal);
                resultList.getChildren().clear();
                for (var r : rs) {
                    resultList.getChildren().add(new Label(r.title));
                }
            }
        });
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
