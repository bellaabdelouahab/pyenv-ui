package com.github.bellaabdelouahab.pyenvcontroller.src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class HelloApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(HelloApplication.class.getName());

    @Override
    public void start(Stage stage) {
        try {
            LOGGER.info("Starting application...");
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
            stage.setTitle("PyEnv Version Manager");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
            LOGGER.info("Application started successfully.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load FXML file.", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred.", e);
        }
    }

    public static void main(String[] args) {
        try {
            // Specify an absolute path for the log file
            FileHandler fileHandler = new FileHandler("C:/GitHub/pyenv-ui/source/pyenv-ui-runtime.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.info("Launching application...");
            launch();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to set up logger.", e);
        }
    }
}