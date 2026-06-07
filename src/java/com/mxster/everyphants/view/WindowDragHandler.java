package com.mxster.everyphants.view;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class WindowDragHandler {
    private final Stage stage;

    private double dragX;
    private double dragY;

    public WindowDragHandler(Stage stage) {
        this.stage = stage;
    }

    public void onPressed(MouseEvent e) {
        if (!e.isPrimaryButtonDown()) {
            return;
        }
        dragX = stage.getX() - e.getScreenX();
        dragY = stage.getY() - e.getScreenY();
    }

    public void onDragged(MouseEvent e) {
        if (!e.isPrimaryButtonDown()) {
            return;
        }
        stage.setX(e.getScreenX() + dragX);
        stage.setY(e.getScreenY() + dragY);
    }
}
