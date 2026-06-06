package com.mxster.everyphants.view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class ResultItemFactory {

    private ResultItemFactory() {
    }

    public static Node create(String title, String body) {
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

        Region bar = new Region();
        bar.setPrefWidth(3);
        bar.setMaxWidth(3);
        bar.setMinWidth(3);
        bar.setMaxHeight(Region.USE_PREF_SIZE);
        bar.setStyle("-fx-background-color: #4cc2ff; -fx-background-radius: 3;");
        bar.setVisible(false);
        bar.setManaged(false);

        StackPane wrapper = new StackPane(item, bar);
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

    public static void updateText(Node wrapperNode, String title, String body) {
        StackPane wrapper = (StackPane) wrapperNode;
        VBox item = (VBox) wrapper.getChildren().get(0);
        Label titleLabel = (Label) item.getChildren().get(0);
        titleLabel.setText(title);
        if (body != null && !body.isEmpty()) {
            if (item.getChildren().size() > 1) {
                Label bodyLabel = (Label) item.getChildren().get(1);
                bodyLabel.setText(body);
            } else {
                Label bodyLabel = new Label(body);
                bodyLabel.getStyleClass().add("result-body");
                item.getChildren().add(bodyLabel);
            }
        }
    }
}
