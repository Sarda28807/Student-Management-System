package com.novastudent;

import com.novastudent.controller.LoginController;
import com.novastudent.database.DatabaseConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * NovaStudent — Smart Student Management System
 * Main application entry point.
 *
 * A futuristic university administration platform with a premium glassmorphism interface.
 *
 * @author NovaStudent
 * @version 1.0.0
 */
public class Main extends Application {

    private static final String APP_TITLE = "NovaStudent — Smart Student Management";
    private static final double MIN_WIDTH = 1100;
    private static final double MIN_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load custom fonts
            loadFonts();

            // Create login screen
            LoginController loginController = new LoginController(primaryStage);
            Scene loginScene = loginController.createScene();

            // Apply theme
            String themeCss = getClass().getResource("/css/theme.css") != null
                ? getClass().getResource("/css/theme.css").toExternalForm()
                : null;
            if (themeCss != null) {
                loginScene.getStylesheets().add(themeCss);
            }

            // Configure stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(loginScene);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
            primaryStage.setWidth(1280);
            primaryStage.setHeight(800);
            primaryStage.centerOnScreen();

            // Handle close
            primaryStage.setOnCloseRequest(event -> {
                DatabaseConnection.getInstance().closeConnection();
                Platform.exit();
                System.exit(0);
            });

            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Failed to start NovaStudent: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * Attempts to load the Inter font family.
     */
    private void loadFonts() {
        try {
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-Regular.ttf"), 13);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-Bold.ttf"), 13);
        } catch (Exception e) {
            // Font files not bundled — system fonts will be used as fallback
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
