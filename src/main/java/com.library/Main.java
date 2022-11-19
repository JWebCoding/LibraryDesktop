package com.library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException{

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/MainStage.fxml"));
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("Library");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1075, 855));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
