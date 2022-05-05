package com.example.network_chat;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class UIClient extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UIClient.class.getResource("client-gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("GB Chat UI");
        stage.setScene(scene);
        stage.show();
        Controller controller = fxmlLoader.getController();
        stage.setOnCloseRequest(controller.getCloseEventHandler());
    }

    public static void main(String[] args) {
        launch();
    }
}