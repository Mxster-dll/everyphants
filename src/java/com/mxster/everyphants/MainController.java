package com.mxster.everyphants;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private TextField inputField;

    @FXML
    public void initialize() {
        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("输入内容变化: " + newValue);
        });
    }
}
