package com.novastudent.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

/**
 * Factory for creating glassmorphism-styled UI components.
 * Ensures consistent look and feel across the application.
 */
public class GlassComponents {

    // ===== BUTTONS =====

    /**
     * Creates a primary glass button (purple gradient).
     */
    public static Button primaryButton(String text, String iconPath) {
        Button btn = new Button(text);
        btn.getStyleClass().addAll("glass-button", "glass-button-primary");
        if (iconPath != null) {
            btn.setGraphic(createIcon(iconPath, 14, Color.WHITE));
        }
        return btn;
    }

    /**
     * Creates a secondary glass button (transparent).
     */
    public static Button secondaryButton(String text, String iconPath) {
        Button btn = new Button(text);
        btn.getStyleClass().add("glass-button");
        if (iconPath != null) {
            btn.setGraphic(createIcon(iconPath, 14, Color.web("#94A3B8")));
        }
        return btn;
    }

    /**
     * Creates a danger glass button (red tint).
     */
    public static Button dangerButton(String text, String iconPath) {
        Button btn = new Button(text);
        btn.getStyleClass().addAll("glass-button", "glass-button-danger");
        if (iconPath != null) {
            btn.setGraphic(createIcon(iconPath, 14, Color.web("#FCA5A5")));
        }
        return btn;
    }

    /**
     * Creates a success glass button (green tint).
     */
    public static Button successButton(String text, String iconPath) {
        Button btn = new Button(text);
        btn.getStyleClass().addAll("glass-button", "glass-button-success");
        if (iconPath != null) {
            btn.setGraphic(createIcon(iconPath, 14, Color.web("#86EFAC")));
        }
        return btn;
    }

    /**
     * Creates a small glass button.
     */
    public static Button smallButton(String text, String styleClass) {
        Button btn = new Button(text);
        btn.getStyleClass().addAll("glass-button", "glass-button-small");
        if (styleClass != null) btn.getStyleClass().add(styleClass);
        return btn;
    }

    // ===== INPUTS =====

    /**
     * Creates a glass text field.
     */
    public static TextField glassTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("glass-input");
        return field;
    }

    /**
     * Creates a glass password field.
     */
    public static PasswordField glassPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.getStyleClass().add("glass-input");
        return field;
    }

    /**
     * Creates a glass text area.
     */
    public static TextArea glassTextArea(String prompt, int rows) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setPrefRowCount(rows);
        area.getStyleClass().add("glass-text-area");
        area.setWrapText(true);
        return area;
    }

    /**
     * Creates a glass combo box.
     */
    public static <T> ComboBox<T> glassComboBox(String prompt) {
        ComboBox<T> combo = new ComboBox<>();
        combo.setPromptText(prompt);
        combo.getStyleClass().add("glass-combo");
        combo.setMaxWidth(Double.MAX_VALUE);
        return combo;
    }

    /**
     * Creates a glass date picker.
     */
    public static DatePicker glassDatePicker(String prompt) {
        DatePicker picker = new DatePicker();
        picker.setPromptText(prompt);
        picker.getStyleClass().add("glass-date-picker");
        picker.setMaxWidth(Double.MAX_VALUE);
        return picker;
    }

    /**
     * Creates a search field.
     */
    public static TextField searchField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("search-field");
        return field;
    }

    // ===== GLASS PANELS =====

    /**
     * Creates a glass card panel.
     */
    public static VBox glassCard(Node... children) {
        VBox card = new VBox(8);
        card.getStyleClass().add("glass-card");
        card.getChildren().addAll(children);
        return card;
    }

    /**
     * Creates a glass panel.
     */
    public static VBox glassPanel(Node... children) {
        VBox panel = new VBox(12);
        panel.getStyleClass().add("glass-panel");
        panel.getChildren().addAll(children);
        return panel;
    }

    // ===== STAT CARDS =====

    /**
     * Creates a dashboard statistics card.
     */
    public static VBox statCard(String iconSvg, String title, String value, String trend, Color accentColor) {
        VBox card = new VBox(8);
        card.getStyleClass().add("glass-card");
        card.setPadding(new Insets(20));
        card.setMinWidth(200);

        // Icon row
        HBox iconRow = new HBox();
        iconRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBg = new StackPane();
        iconBg.setMinSize(40, 40);
        iconBg.setMaxSize(40, 40);
        iconBg.setStyle("-fx-background-color: " + toRgba(accentColor, 0.15) + ";" +
                        "-fx-background-radius: 10;");
        SVGPath icon = new SVGPath();
        icon.setContent(iconSvg);
        icon.setFill(accentColor);
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);
        iconBg.getChildren().add(icon);
        iconRow.getChildren().add(iconBg);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("text-stat-label");

        // Value
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("text-stat");

        // Trend
        Label trendLabel = new Label(trend);
        trendLabel.getStyleClass().add(trend.startsWith("+") || trend.startsWith("\u2191") ? "text-trend-up" : "text-trend-down");

        card.getChildren().addAll(iconRow, titleLabel, valueLabel, trendLabel);
        return card;
    }

    // ===== BADGES =====

    /**
     * Creates a status badge.
     */
    public static Label statusBadge(String text) {
        Label badge = new Label(text);
        badge.getStyleClass().add("badge");

        switch (text.toUpperCase()) {
            case "ACTIVE":    badge.getStyleClass().add("badge-active"); break;
            case "INACTIVE":
            case "SUSPENDED": badge.getStyleClass().add("badge-inactive"); break;
            case "GRADUATED": badge.getStyleClass().add("badge-graduated"); break;
            case "PRESENT":   badge.getStyleClass().add("badge-present"); break;
            case "ABSENT":    badge.getStyleClass().add("badge-absent"); break;
            case "LATE":      badge.getStyleClass().add("badge-late"); break;
            default:          badge.getStyleClass().add("badge-active"); break;
        }

        return badge;
    }

    /**
     * Creates a grade badge.
     */
    public static Label gradeBadge(String grade) {
        Label badge = new Label(grade);
        badge.getStyleClass().addAll("badge", com.novastudent.util.GradeCalculator.getGradeStyleClass(grade));
        return badge;
    }

    // ===== TABLE =====

    /**
     * Creates a glass-styled TableView.
     */
    public static <T> TableView<T> glassTable() {
        TableView<T> table = new TableView<>();
        table.getStyleClass().add("glass-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No data available"));
        return table;
    }

    // ===== FORM HELPERS =====

    /**
     * Creates a form field with label.
     */
    public static VBox formField(String labelText, Node input) {
        VBox field = new VBox(4);
        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");
        field.getChildren().addAll(label, input);
        return field;
    }

    /**
     * Creates a form field with label and error label.
     */
    public static VBox formFieldWithError(String labelText, Node input, Label errorLabel) {
        VBox field = new VBox(2);
        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        field.getChildren().addAll(label, input, errorLabel);
        return field;
    }

    /**
     * Creates a section title.
     */
    public static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text-section");
        label.setPadding(new Insets(8, 0, 4, 0));
        return label;
    }

    // ===== ICONS =====

    /**
     * Creates an SVG icon.
     */
    public static SVGPath createIcon(String svgContent, double size, Color color) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgContent);
        icon.setFill(color);
        // Scale based on size (icons designed at ~24px viewbox)
        double scale = size / 24.0;
        icon.setScaleX(scale);
        icon.setScaleY(scale);
        return icon;
    }

    /**
     * Creates a circular avatar with initials.
     */
    public static StackPane avatar(String initials, double size) {
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("avatar");
        avatar.setMinSize(size, size);
        avatar.setMaxSize(size, size);

        Text text = new Text(initials);
        text.getStyleClass().add("avatar-text");
        text.setFill(Color.web("#F8FAFC"));
        text.setStyle("-fx-font-size: " + (size * 0.38) + "px; -fx-font-weight: 700;");

        avatar.getChildren().add(text);
        return avatar;
    }

    /**
     * Creates a status indicator dot.
     */
    public static Circle statusDot(boolean online) {
        Circle dot = new Circle(4);
        dot.setFill(online ? Color.web("#22C55E") : Color.web("#EF4444"));
        return dot;
    }

    // ===== HELPERS =====

    private static String toRgba(Color color, double alpha) {
        return String.format("rgba(%d, %d, %d, %.2f)",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255),
            alpha);
    }
}
