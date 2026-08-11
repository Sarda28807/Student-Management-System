package com.novastudent.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

/**
 * Glass toast notification system.
 * Displays success/warning/error/info messages at the top-right of the screen.
 */
public class ToastNotification {

    public enum ToastType {
        SUCCESS, WARNING, ERROR, INFO
    }

    private static VBox toastContainer;

    /**
     * Initializes the toast container. Call once during app setup.
     */
    public static void initialize(StackPane rootPane) {
        toastContainer = new VBox(8);
        toastContainer.setAlignment(Pos.TOP_RIGHT);
        toastContainer.setPadding(new Insets(20, 20, 0, 0));
        toastContainer.setPickOnBounds(false);
        toastContainer.setMouseTransparent(false);
        toastContainer.setMaxWidth(360);
        toastContainer.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane.setAlignment(toastContainer, Pos.TOP_RIGHT);
        rootPane.getChildren().add(toastContainer);
    }

    /**
     * Shows a success toast.
     */
    public static void success(String message) {
        show(message, ToastType.SUCCESS);
    }

    /**
     * Shows a warning toast.
     */
    public static void warning(String message) {
        show(message, ToastType.WARNING);
    }

    /**
     * Shows an error toast.
     */
    public static void error(String message) {
        show(message, ToastType.ERROR);
    }

    /**
     * Shows an info toast.
     */
    public static void info(String message) {
        show(message, ToastType.INFO);
    }

    /**
     * Shows a toast notification.
     */
    public static void show(String message, ToastType type) {
        if (toastContainer == null) return;

        javafx.application.Platform.runLater(() -> {
            HBox toast = createToast(message, type);

            // Start hidden
            toast.setOpacity(0);
            toast.setTranslateX(50);

            toastContainer.getChildren().add(toast);

            // Animate in
            Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(toast.opacityProperty(), 0),
                    new KeyValue(toast.translateXProperty(), 50)
                ),
                new KeyFrame(Duration.millis(300),
                    new KeyValue(toast.opacityProperty(), 1, Interpolator.EASE_OUT),
                    new KeyValue(toast.translateXProperty(), 0, Interpolator.EASE_OUT)
                )
            );

            // Auto-dismiss after 4 seconds
            Timeline dismiss = new Timeline(
                new KeyFrame(Duration.seconds(4), e -> dismissToast(toast))
            );

            fadeIn.play();
            dismiss.play();
        });
    }

    private static void dismissToast(HBox toast) {
        Timeline fadeOut = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(toast.opacityProperty(), 1),
                new KeyValue(toast.translateXProperty(), 0)
            ),
            new KeyFrame(Duration.millis(250),
                new KeyValue(toast.opacityProperty(), 0, Interpolator.EASE_IN),
                new KeyValue(toast.translateXProperty(), 50, Interpolator.EASE_IN)
            )
        );
        fadeOut.setOnFinished(e -> toastContainer.getChildren().remove(toast));
        fadeOut.play();
    }

    private static HBox createToast(String message, ToastType type) {
        HBox toast = new HBox(10);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.setPadding(new Insets(12, 16, 12, 16));
        toast.setMaxWidth(340);
        toast.setMinWidth(280);

        String bgColor, borderColor, iconColor, iconPath;

        switch (type) {
            case SUCCESS:
                bgColor = "rgba(34, 197, 94, 0.12)";
                borderColor = "rgba(34, 197, 94, 0.3)";
                iconColor = "#22C55E";
                iconPath = IconFactory.CHECK;
                break;
            case WARNING:
                bgColor = "rgba(245, 158, 11, 0.12)";
                borderColor = "rgba(245, 158, 11, 0.3)";
                iconColor = "#F59E0B";
                iconPath = IconFactory.WARNING;
                break;
            case ERROR:
                bgColor = "rgba(239, 68, 68, 0.12)";
                borderColor = "rgba(239, 68, 68, 0.3)";
                iconColor = "#EF4444";
                iconPath = IconFactory.CLOSE;
                break;
            default: // INFO
                bgColor = "rgba(139, 92, 246, 0.12)";
                borderColor = "rgba(139, 92, 246, 0.3)";
                iconColor = "#8B5CF6";
                iconPath = IconFactory.INFO;
                break;
        }

        toast.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);"
        );

        // Icon
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.setFill(Color.web(iconColor));
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);

        // Message
        Label label = new Label(message);
        label.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 13px; -fx-font-weight: 500;");
        label.setWrapText(true);
        label.setMaxWidth(260);

        // Close button
        Label close = new Label("\u2715");
        close.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px; -fx-cursor: hand;");
        close.setOnMouseClicked(e -> dismissToast(toast));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toast.getChildren().addAll(icon, label, spacer, close);
        return toast;
    }
}
