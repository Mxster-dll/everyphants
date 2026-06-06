package com.mxster.everyphants.view;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class ResultItemFactory {

    private static final Background HOVER_BG = new Background(
            new BackgroundFill(Color.rgb(255, 255, 255, 0.06), new CornerRadii(8), null));

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

        item.setOnMouseEntered(e -> item.setBackground(HOVER_BG));
        item.setOnMouseExited(e -> item.setBackground(Background.EMPTY));

        return item;
    }
}
