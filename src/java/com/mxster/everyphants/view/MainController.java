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
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private WindowDragHandler dragHandler;
    private InputThrottle inputThrottle;
    private List<Result> currentResults = List.of();
    private final Map<Result, VBox> nodeCache = new LinkedHashMap<>();

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

        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().equals(oldVal.trim())) {
                return;
            }
            inputThrottle.trigger();
        });

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
            VBox node = nodeCache.get(r);
            if (node == null) {
                node = (VBox) ResultItemFactory.create(r.getTitle(), r.getDisplayText());
                nodeCache.put(r, node);
            } else {
                Label titleLabel = (Label) node.getChildren().get(0);
                titleLabel.setText(r.getTitle());
                if (node.getChildren().size() > 1) {
                    Label bodyLabel = (Label) node.getChildren().get(1);
                    String body = r.getDisplayText();
                    if (body != null && !body.isEmpty()) {
                        bodyLabel.setText(body);
                    }
                }
            }
        }

        resultList.getChildren().setAll(sorted.stream().map(nodeCache::get).toList());
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
}
