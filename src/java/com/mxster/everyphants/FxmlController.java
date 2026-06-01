package com.mxster.everyphants;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class FxmlController {
    @FXML
    private Label label;
    @FXML
    private Button button;

    @FXML
    private void onClick() {
        label.setText("Hello from FXML!");
    }
}
