package com.mxster.everyphants.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mxster.everyphants.model.PluginManager;
import com.mxster.everyphants.model.ProactivePlugin;
import com.mxster.everyphants.model.ReactivePlugin;
import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;

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
    private List<Result> persistentResults = List.of();
    private final Map<Result, Node> nodeCache = new LinkedHashMap<>();

    public void init(Stage stage) {
        this.stage = stage;

        dragHandler = new WindowDragHandler(stage);
        rootPane.setOnMousePressed(dragHandler::onPressed);
        rootPane.setOnMouseDragged(dragHandler::onDragged);

        PluginManager manager = new PluginManager();
        persistentResults = registerPlugins(manager);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                refreshCurrentResults();
            }
        }.start();

        inputThrottle = new InputThrottle(() -> doUpdate(manager));
        setupCopyFeedback();
        setupInputListener();
        setupClearButton();

        updateResultVisibility();
    }

    private List<Result> registerPlugins(PluginManager manager) {
        List<Result> results = new ArrayList<>();
        for (var plugin : manager.getPlugins()) {
            if (plugin instanceof ProactivePlugin) {
                Result r = plugin.query("");
                if (r != null) {
                    results.add(r);
                }
            }
            if (plugin instanceof ReactivePlugin<?> rp) {
                rp.addResultChangedListener(() -> refreshFromCache(manager));
            }
        }
        return results;
    }

    private void setupCopyFeedback() {
        feedbackLabel.setOpacity(0);

        ResultItem.onCopyFeedback = text -> {
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
    }

    private void setupInputListener() {
        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().equals(oldVal.trim())) {
                inputThrottle.trigger();
            }
        });
    }

    private void setupClearButton() {
        VBox contentBox = (VBox) rootPane.getChildren().get(0);

        Label clearBtn = new Label("✕");
        String baseStyle = "-fx-text-fill: rgba(255,255,255,0.3); -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0 12 0 0;";
        String hoverStyle = "-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0 12 0 0;";

        clearBtn.setStyle(baseStyle);
        clearBtn.setOnMouseClicked(e -> inputField.clear());
        clearBtn.setOnMouseEntered(e -> clearBtn.setStyle(hoverStyle));
        clearBtn.setOnMouseExited(e -> clearBtn.setStyle(baseStyle));

        StackPane inputWrapper = new StackPane(inputField, clearBtn);
        StackPane.setAlignment(clearBtn, javafx.geometry.Pos.CENTER_RIGHT);
        clearBtn.setTranslateX(-5);
        clearBtn.setTranslateY(-5);

        contentBox.getChildren().remove(inputField);
        contentBox.getChildren().add(0, inputWrapper);
    }

    private void doUpdate(PluginManager manager) {
        String text = inputField.getText();
        String trimmed = (text == null || text.isEmpty()) ? "" : text.trim();

        List<Result> results;

        if (!trimmed.isEmpty()) {
            results = new ArrayList<>(persistentResults);
            for (var plugin : manager.getPlugins()) {
                if (!(plugin instanceof ProactivePlugin)) {
                    Result r = plugin.query(trimmed);
                    if (r != null) {
                        results.add(r);
                    }
                }
            }
        } else {
            results = List.of();
        }

        currentResults = results;
        renderResults();
        updateResultVisibility();
    }

    private void refreshCurrentResults() {
        boolean needsRender = false;
        for (var r : currentResults) {
            if (r instanceof RefreshableResult rr && rr.isRefreshing()) {
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
                ResultItem item = new ResultItem();

                item.setTitle(r.getTitle());
                item.setBody(r.getDisplayText());
                item.setIconPath(r.getIconPath());
                item.setBackgroundColor(r.getBackgroundColor());

                node = item;
                nodeCache.put(r, node);
            } else {
                if (node instanceof ResultItem item) {
                    item.setTitle(r.getTitle());
                    item.setBody(r.getDisplayText());
                }
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
