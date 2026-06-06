package com.mxster.everyphants;

import com.mxster.everyphants.util.WindowsAcrylicUtil;
import com.mxster.everyphants.view.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 万象 Everyphants
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet("fluent-light.css");

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("万象");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mxster/everyphants/fxml/main.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.init(stage);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(
                getClass().getResource("/com/mxster/everyphants/css/main.css").toExternalForm());

        stage.setScene(scene);
        stage.sizeToScene();

        stage.setOnShown(e -> WindowsAcrylicUtil.applyRoundedCorners(stage, 12));

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
