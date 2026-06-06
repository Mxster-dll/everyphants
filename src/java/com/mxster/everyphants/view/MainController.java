package com.mxster.everyphants.view;

import java.util.ArrayList;
import java.util.List;

import com.mxster.everyphants.model.PluginManager;
import com.mxster.everyphants.model.RefreshableResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ProactivePlugin;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
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

    public void init(Stage stage) {
        this.stage = stage;

        dragHandler = new WindowDragHandler(stage);
        rootPane.setOnMousePressed(dragHandler::onPressed);
        rootPane.setOnMouseDragged(dragHandler::onDragged);

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

        for (var r : results) {
            if (r instanceof RefreshableResult rr && rr.getRefreshInterval() == 0) {
                rr.refresh();
            }
        }

        results.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .forEach(r -> resultList.getChildren().add(
                        ResultItemFactory.create(r.getTitle(), r.getDisplayText())));

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
