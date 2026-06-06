package com.mxster.everyphants.view;

import javafx.scene.Node;
import javafx.scene.control.Label;
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

        return item;
    }
}
