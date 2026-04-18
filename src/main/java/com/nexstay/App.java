package com.nexstay;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/nexstay/MainLayout.fxml")
        );

        Scene scene = new Scene(loader.load(), 1100, 700);
        scene.getStylesheets().add(
            getClass().getResource("/styles/style.css").toExternalForm()
        );

        primaryStage.setTitle("NexStay");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
