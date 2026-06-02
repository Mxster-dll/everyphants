package com.mxster.everyphants;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 万象 · 毛玻璃输入框 — FXML + CSS 版本。
 *
 * 窗口为 TRANSPARENT 分层窗口，通过 JNA 调用 Windows DWM
 * 实现 Acrylic 毛玻璃模糊效果。输入框内容变化实时输出到终端。
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // ── 必须 TRANSPARENT：Acrylic 模糊需要分层窗口 ──
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("万象");

        // ── 加载 FXML ──
        FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
        Parent root = loader.load();

        // ── 注入 Stage 给控制器 ──
        MainController controller = loader.getController();
        controller.init(stage);

        // ── Scene：背景透明 ──
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm());

        stage.setScene(scene);

        // ── 窗口尺寸自适应内容（正好比输入框大一圈 padding）──
        stage.sizeToScene();

        // ── 窗口显示后启用毛玻璃 + 圆角裁剪 ──
        stage.setOnShown(e -> {
            String strategy = WindowsAcrylicUtil.enableAcrylic(stage);
            System.out.println("[Glass] 应用模糊策略: " + strategy);
            WindowsAcrylicUtil.applyRoundedCorners(stage, 12);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
