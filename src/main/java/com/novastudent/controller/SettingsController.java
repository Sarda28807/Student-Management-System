package com.novastudent.controller;

import com.novastudent.model.User;
import com.novastudent.security.SessionManager;
import com.novastudent.service.AuthenticationService;
import com.novastudent.ui.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Controller for application settings and user profile.
 */
public class SettingsController {

    private final Stage stage;
    private final AuthenticationService authService;
    private VBox contentContainer;

    public SettingsController(Stage stage) {
        this.stage = stage;
        this.authService = new AuthenticationService();
    }

    public void loadContent(VBox container) {
        this.contentContainer = container;
        container.getChildren().clear();

        VBox layout = new VBox(24);
        layout.setPadding(new Insets(10, 0, 0, 0));

        // Security / Password Change Card
        VBox securityCard = GlassComponents.glassPanel();
        securityCard.setMaxWidth(500);
        
        Label secTitle = GlassComponents.sectionTitle("Account Security");
        Label secDesc = new Label("Change your administrator password");
        secDesc.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 0 0 12 0;");

        PasswordField currentPass = GlassComponents.glassPasswordField("Current password");
        PasswordField newPass = GlassComponents.glassPasswordField("New password (min 6 chars)");
        PasswordField confirmPass = GlassComponents.glassPasswordField("Confirm new password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button updateBtn = GlassComponents.primaryButton("Update Password", IconFactory.SAVE);
        updateBtn.setOnAction(e -> {
            String cp = currentPass.getText();
            String np = newPass.getText();
            String cnp = confirmPass.getText();

            if (cp.isEmpty() || np.isEmpty() || cnp.isEmpty()) {
                showError(errorLabel, "All fields are required");
                return;
            }
            if (np.length() < 6) {
                showError(errorLabel, "New password must be at least 6 characters");
                return;
            }
            if (!np.equals(cnp)) {
                showError(errorLabel, "New passwords do not match");
                return;
            }

            try {
                if (authService.changePassword(cp, np)) {
                    ToastNotification.success("Password updated successfully");
                    currentPass.clear();
                    newPass.clear();
                    confirmPass.clear();
                    errorLabel.setVisible(false);
                    errorLabel.setManaged(false);
                } else {
                    showError(errorLabel, "Incorrect current password");
                }
            } catch (Exception ex) {
                showError(errorLabel, "Failed to update password: " + ex.getMessage());
            }
        });

        HBox btnRow = new HBox(updateBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.setPadding(new Insets(10, 0, 0, 0));

        securityCard.getChildren().addAll(
            secTitle, secDesc,
            GlassComponents.formField("Current Password", currentPass),
            GlassComponents.formField("New Password", newPass),
            GlassComponents.formField("Confirm Password", confirmPass),
            errorLabel, btnRow
        );

        // System Info Card
        VBox sysCard = GlassComponents.glassPanel();
        sysCard.setMaxWidth(500);

        Label sysTitle = GlassComponents.sectionTitle("System Information");
        
        GridPane grid = new GridPane();
        grid.setVgap(12);
        grid.setHgap(30);
        
        User user = SessionManager.getInstance().getCurrentUser();
        
        addInfoRow(grid, 0, "Current User", user != null ? user.getFullName() : "Demo User");
        addInfoRow(grid, 1, "Role", user != null ? user.getRole().name() : "ADMIN");
        addInfoRow(grid, 2, "Database", "MySQL 8.0 Connected");
        addInfoRow(grid, 3, "Version", "NovaStudent v1.0.0 (Build 2026)");
        addInfoRow(grid, 4, "Java Version", System.getProperty("java.version"));
        
        sysCard.getChildren().addAll(sysTitle, grid);

        layout.getChildren().addAll(securityCard, sysCard);
        container.getChildren().add(layout);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px; -fx-font-weight: 600;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 13px;");
        grid.add(l, 0, row);
        grid.add(v, 1, row);
    }
}
