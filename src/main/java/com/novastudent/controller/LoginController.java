package com.novastudent.controller;

import com.novastudent.model.User;
import com.novastudent.service.AuthenticationService;
import com.novastudent.ui.*;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the login screen.
 * Creates a futuristic glassmorphism login interface.
 */
public class LoginController {

    private final Stage stage;
    private final AuthenticationService authService;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;
    private Button loginButton;

    public LoginController(Stage stage) {
        this.stage = stage;
        this.authService = new AuthenticationService();
    }

    /**
     * Creates the login scene.
     */
    public Scene createScene() {
        StackPane root = new StackPane();

        // Background with gradient and glow
        StackPane background = new StackPane();
        background.setStyle("-fx-background-color: linear-gradient(to bottom right, #070B14 0%, #0D0B20 40%, #0B1120 100%);");

        // Ambient glow effects
        Region purpleGlow = new Region();
        purpleGlow.setMaxSize(500, 500);
        purpleGlow.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 50%, rgba(139,92,246,0.12), transparent);");
        StackPane.setAlignment(purpleGlow, Pos.TOP_LEFT);
        StackPane.setMargin(purpleGlow, new Insets(-150, 0, 0, -150));

        Region cyanGlow = new Region();
        cyanGlow.setMaxSize(400, 400);
        cyanGlow.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 50%, rgba(34,211,238,0.06), transparent);");
        StackPane.setAlignment(cyanGlow, Pos.TOP_RIGHT);
        StackPane.setMargin(cyanGlow, new Insets(-100, -100, 0, 0));

        Region bottomGlow = new Region();
        bottomGlow.setMaxSize(350, 350);
        bottomGlow.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 50%, rgba(99,102,241,0.06), transparent);");
        StackPane.setAlignment(bottomGlow, Pos.BOTTOM_RIGHT);

        background.getChildren().addAll(purpleGlow, cyanGlow, bottomGlow);

        // Login card
        VBox loginCard = new VBox(20);
        loginCard.getStyleClass().add("login-card");
        loginCard.setMaxWidth(420);
        loginCard.setAlignment(Pos.CENTER);

        // Brand logo
        VBox brandBox = new VBox(4);
        brandBox.setAlignment(Pos.CENTER);

        // Logo icon
        StackPane logoIcon = new StackPane();
        logoIcon.setMinSize(56, 56);
        logoIcon.setMaxSize(56, 56);
        logoIcon.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, rgba(139,92,246,0.3), rgba(99,102,241,0.3));" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(139,92,246,0.4);" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;"
        );
        Text logoText = new Text("N");
        logoText.setStyle("-fx-font-size: 24px; -fx-font-weight: 800;");
        logoText.setFill(Color.web("#A78BFA"));
        logoIcon.getChildren().add(logoText);

        Label brandLabel = new Label("NOVA STUDENT");
        brandLabel.getStyleClass().add("login-brand");

        brandBox.getChildren().addAll(logoIcon, brandLabel);

        // Title
        VBox titleBox = new VBox(4);
        titleBox.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Welcome Back");
        titleLabel.getStyleClass().add("login-title");
        Label subtitleLabel = new Label("Sign in to your administration portal");
        subtitleLabel.getStyleClass().add("login-subtitle");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        // Error label
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setMaxWidth(Double.MAX_VALUE);

        // Username field
        VBox usernameBox = new VBox(6);
        Label usernameLabel = new Label("Username");
        usernameLabel.getStyleClass().add("form-label");
        usernameField = GlassComponents.glassTextField("Enter your username");
        usernameField.setMaxWidth(Double.MAX_VALUE);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password field
        VBox passwordBox = new VBox(6);
        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("form-label");
        passwordField = GlassComponents.glassPasswordField("Enter your password");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Remember me + Forgot password
        HBox optionsRow = new HBox();
        optionsRow.setAlignment(Pos.CENTER_LEFT);
        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.getStyleClass().add("glass-checkbox");
        Region optSpacer = new Region();
        HBox.setHgrow(optSpacer, Priority.ALWAYS);
        Hyperlink forgotLink = new Hyperlink("Forgot password?");
        forgotLink.getStyleClass().add("glass-link");
        forgotLink.setOnAction(e -> ToastNotification.info("Contact system administrator to reset password"));
        optionsRow.getChildren().addAll(rememberMe, optSpacer, forgotLink);

        // Login button
        loginButton = new Button("SIGN IN");
        loginButton.getStyleClass().add("login-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> handleLogin());

        // Enter key support
        passwordField.setOnAction(e -> handleLogin());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        // Footer
        Label footerLabel = new Label("Secure administrative access");
        footerLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px;");
        footerLabel.setAlignment(Pos.CENTER);
        footerLabel.setMaxWidth(Double.MAX_VALUE);

        // Demo credentials hint
        Label demoHint = new Label("Demo: admin / Admin@123");
        demoHint.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px; -fx-font-style: italic;");
        demoHint.setAlignment(Pos.CENTER);
        demoHint.setMaxWidth(Double.MAX_VALUE);

        loginCard.getChildren().addAll(
            brandBox, titleBox, errorLabel,
            usernameBox, passwordBox, optionsRow,
            loginButton, footerLabel, demoHint
        );

        root.getChildren().addAll(background, loginCard);

        // Entrance animation
        loginCard.setOpacity(0);
        loginCard.setTranslateY(30);
        Timeline fadeIn = new Timeline(
            new KeyFrame(Duration.millis(600),
                new KeyValue(loginCard.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(loginCard.translateYProperty(), 0, Interpolator.EASE_OUT)
            )
        );
        fadeIn.play();

        Scene scene = new Scene(root, 1280, 800);
        return scene;
    }

    /**
     * Handles login button click.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate
        if (username.isEmpty()) {
            showError("Please enter your username");
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password");
            return;
        }

        loginButton.setText("Signing in...");
        loginButton.setDisable(true);

        // Run auth on background thread
        new Thread(() -> {
            try {
                User user = authService.authenticate(username, password);

                javafx.application.Platform.runLater(() -> {
                    if (user != null) {
                        // Success — navigate to main app
                        MainLayout mainLayout = new MainLayout(stage);
                        Scene mainScene = mainLayout.createScene();
                        stage.setScene(mainScene);
                        ToastNotification.success("Welcome back, " + user.getFullName() + "!");
                    } else {
                        showError("Invalid username or password");
                        loginButton.setText("SIGN IN");
                        loginButton.setDisable(false);
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    showError("Connection failed. Check database configuration.");
                    loginButton.setText("SIGN IN");
                    loginButton.setDisable(false);
                });
            }
        }).start();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);

        // Shake animation
        Timeline shake = new Timeline(
            new KeyFrame(Duration.millis(0), new KeyValue(errorLabel.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(50), new KeyValue(errorLabel.translateXProperty(), -8)),
            new KeyFrame(Duration.millis(100), new KeyValue(errorLabel.translateXProperty(), 8)),
            new KeyFrame(Duration.millis(150), new KeyValue(errorLabel.translateXProperty(), -4)),
            new KeyFrame(Duration.millis(200), new KeyValue(errorLabel.translateXProperty(), 0))
        );
        shake.play();
    }

    // Import needed for MainLayout
    private static class MainLayout extends com.novastudent.ui.MainLayout {
        MainLayout(Stage stage) { super(stage); }
    }
}
