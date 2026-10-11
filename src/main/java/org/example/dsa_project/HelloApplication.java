package org.example.dsa_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 820);
        scene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
        stage.setTitle("Smart Emergency Response & Ambulance Routing | DSA Project");
        stage.setMinWidth(1050); stage.setMinHeight(700); stage.setScene(scene); stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}