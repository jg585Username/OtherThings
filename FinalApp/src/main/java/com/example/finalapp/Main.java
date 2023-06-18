package com.example.finalapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
//runner class to compile program
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("GroceryGUI.fxml")); //GUI
        Scene scene = new Scene(fxmlLoader.load(), 725, 500); //set window height/width
        stage.setResizable(false); //lock window size
        stage.setTitle("Red Apple Corner Store Order Builder (Tel. 236-456-7890)");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}