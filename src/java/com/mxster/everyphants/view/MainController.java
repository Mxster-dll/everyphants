package com.mxster.everyphants.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mxster.everyphants.model.PluginManager;
import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ProactivePlugin;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
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
    private StackPane infoBar;
    @FXML
    private Label brandLabel;
    @FXML
    private Label feedbackLabel;
    @FXML
    private Label countLabel;

    private Stage stage;
    private WindowDragHandler dragHandler;
    private InputThrottle inputThrottle;
    private List<Result> currentResults = List.of();
    private final Map<Result, Node> nodeCache = new LinkedHashMap<>();

    public void init(Stage stage) {
        this.stage = stage;

        dragHandler = new WindowDragHandler(stage);
        rootPane.setOnMousePressed(dragHandler::onPressed);
        rootPane.setOnMouseDragged(dragHandler::onDragged);

        PluginManager manager = new PluginManager();

        for (var plugin : manager.getPlugins()) {
            if (plugin instanceof ReactivePlugin<?> rp) {
                rp.addResultChangedListener(() -> refreshFromCache(manager));
            }
        }

        AnimationTimer frameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                refreshCurrentResults();
            }
        };
        frameTimer.start();

        inputThrottle = new InputThrottle(() -> doUpdate(manager));

        // 反馈标签初始不可见（FXML 中已定义，此处仅设置初始状态）
        feedbackLabel.setOpacity(0);

        ResultItemFactory.onCopyFeedback = text -> {
            feedbackLabel.setText("已复制: " + text);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), feedbackLabel);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            PauseTransition hold = new PauseTransition(Duration.seconds(1.2));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), feedbackLabel);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            SequentialTransition seq = new SequentialTransition(feedbackLabel, fadeIn, hold, fadeOut);
            seq.play();
        };
        ResultItemFactory.installCopyFeedback();

        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().equals(oldVal.trim())) {
                return;
            }
            inputThrottle.trigger();
        });

        VBox contentBox = (VBox) rootPane.getChildren().get(0);

        Label clearBtn = new Label("✕");
        clearBtn.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.3); -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0 12 0 0;");
        clearBtn.setOnMouseClicked(e -> inputField.clear());
        clearBtn.setOnMouseEntered(e -> clearBtn.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0 12 0 0;"));
        clearBtn.setOnMouseExited(e -> clearBtn.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.3); -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0 12 0 0;"));

        contentBox.getChildren().remove(inputField);
        StackPane inputWrapper = new StackPane(inputField, clearBtn);
        StackPane.setAlignment(clearBtn, javafx.geometry.Pos.CENTER_RIGHT);
        clearBtn.setTranslateX(-5);
        clearBtn.setTranslateY(-5);
        contentBox.getChildren().add(0, inputWrapper);

        updateResultVisibility();
    }

    private void doUpdate(PluginManager manager) {
        String text = inputField.getText();
        if (text == null || text.isEmpty()) {
            currentResults = List.of();
            renderResults();
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

        currentResults = results;
        renderResults();
        updateResultVisibility();
    }

    private void refreshCurrentResults() {
        boolean needsRender = false;
        for (var r : currentResults) {
            if (r instanceof RefreshableResult rr && rr.getRefreshInterval() == 0) {
                rr.refresh();
                needsRender = true;
            }
        }
        if (needsRender) {
            renderResults();
        }
    }

    private void refreshFromCache(PluginManager manager) {
        doUpdate(manager);
    }

    private void renderResults() {
        List<Result> sorted = currentResults.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .toList();

        nodeCache.keySet().removeIf(r -> !sorted.contains(r));

        for (Result r : sorted) {
            Node node = nodeCache.get(r);
            if (node == null) {
                node = ResultItemFactory.create(r.getTitle(), r.getDisplayText(),
                        r.getIconPath(), r.getBackgroundColor());
                nodeCache.put(r, node);
            } else {
                ResultItemFactory.updateText(node, r.getTitle(), r.getDisplayText());
            }
        }

        resultList.getChildren().setAll(sorted.stream().map(nodeCache::get).toList());
        countLabel.setText(String.valueOf(sorted.size()));
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
        infoBar.setVisible(hasResults);
        infoBar.setManaged(hasResults);

        stage.sizeToScene();
    }
}
