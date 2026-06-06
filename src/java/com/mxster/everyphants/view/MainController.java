package com.mxster.everyphants.view;

import java.util.ArrayList;
import java.util.List;

import com.mxster.everyphants.model.PluginManager;
import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ProactivePlugin;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

import javafx.animation.AnimationTimer;
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

    private static final long MIN_UPDATE_INTERVAL = 250; // ms
    private long lastUpdateTime = 0;
    private final PauseTransition throttle = new PauseTransition();

    public void init(Stage stage) {
        this.stage = stage;

        rootPane.setOnMousePressed(this::onMousePressed);
        rootPane.setOnMouseDragged(this::onMouseDragged);

        PluginManager manager = new PluginManager();

        for (var plugin : manager.getPlugins()) {
            if (plugin instanceof ReactivePlugin<?> rp) {
                rp.addResultChangedListener(() -> doUpdate(manager));
            }
        }

        AnimationTimer frameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                doUpdate(manager);
            }
        };
        frameTimer.start();

        throttle.setOnFinished(e -> {
            lastUpdateTime = System.currentTimeMillis();
            doUpdate(manager);
        });

        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().equals(oldVal.trim())) {
                return;
            }

            long now = System.currentTimeMillis();
            long elapsed = now - lastUpdateTime;

            if (elapsed >= MIN_UPDATE_INTERVAL) {
                // 距上次更新已超过 250ms，立即更新
                throttle.stop();
                lastUpdateTime = System.currentTimeMillis();
                doUpdate(manager);
            } else {
                // 距上次更新不足 250ms，推迟到剩余时间后更新
                throttle.stop();
                throttle.setDuration(Duration.millis(MIN_UPDATE_INTERVAL - elapsed));
                throttle.play();
            }
        });

        updateResultVisibility();
    }

    private void doUpdate(PluginManager manager) {
        String text = inputField.getText();
        resultList.getChildren().clear();
        if (text == null || text.isEmpty()) {
            updateResultVisibility();
            return;
        }

        String trimmed = text.trim();

        List<Result> results = new ArrayList<>();
        for (var plugin : manager.getPlugins()) {
            if (plugin instanceof ReactivePlugin<?> rp) {
                results.addAll(rp.query(trimmed));
            } else if (plugin instanceof ProactivePlugin<?> pp) {
                results.addAll(pp.query());
            }
        }

        // 驱动 interval=0 的 RefreshableResult 每帧自我更新
        for (var r : results) {
            if (r instanceof RefreshableResult rr && rr.getRefreshInterval() == 0) {
                rr.refresh();
            }
        }

        results.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .forEach(r -> resultList.getChildren().add(createResultItem(r.getTitle(), r.getDisplayText())));

        updateResultVisibility();
    }

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
