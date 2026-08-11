package com.novastudent.ui;

import com.novastudent.controller.*;
import com.novastudent.security.SessionManager;
import com.novastudent.database.DatabaseConnection;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Main application layout with sidebar, top bar, and content area.
 * Implements the futuristic glassmorphism interface structure.
 */
public class MainLayout {

    private final Stage stage;
    private final BorderPane root;
    private final StackPane contentWrapper;
    private final VBox contentArea;
    private final Label pageTitle;
    private final Label pageSubtitle;
    private final TextField globalSearch;
    private VBox navContainer;
    private Button activeNavButton;

    // Controllers
    private DashboardController dashboardController;
    private StudentController studentController;
    private CourseController courseController;
    private AttendanceController attendanceController;
    private ResultController resultController;
    private AnalyticsController analyticsController;
    private ReportController reportController;
    private SettingsController settingsController;

    public MainLayout(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.contentWrapper = new StackPane();
        this.contentArea = new VBox();
        this.pageTitle = new Label("Dashboard");
        this.pageSubtitle = new Label("University overview and student activity");
        this.globalSearch = GlassComponents.searchField("Search students, courses...");

        initControllers();
        buildLayout();
    }

    private void initControllers() {
        dashboardController = new DashboardController();
        studentController = new StudentController();
        courseController = new CourseController();
        attendanceController = new AttendanceController();
        resultController = new ResultController();
        analyticsController = new AnalyticsController();
        reportController = new ReportController();
        settingsController = new SettingsController(stage);
    }

    private void buildLayout() {
        // Build the background with subtle glow effects
        StackPane backgroundPane = new StackPane();
        backgroundPane.getStyleClass().add("main-background");

        // Ambient glow effects
        Region purpleGlow = new Region();
        purpleGlow.setMaxSize(400, 400);
        purpleGlow.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 50%, rgba(139,92,246,0.08), transparent);");
        StackPane.setAlignment(purpleGlow, Pos.TOP_LEFT);
        StackPane.setMargin(purpleGlow, new Insets(-100, 0, 0, -100));

        Region cyanGlow = new Region();
        cyanGlow.setMaxSize(350, 350);
        cyanGlow.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 50%, rgba(34,211,238,0.05), transparent);");
        StackPane.setAlignment(cyanGlow, Pos.TOP_RIGHT);
        StackPane.setMargin(cyanGlow, new Insets(-80, -80, 0, 0));

        Region bottomGlow = new Region();
        bottomGlow.setMaxSize(300, 300);
        bottomGlow.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 50%, rgba(139,92,246,0.04), transparent);");
        StackPane.setAlignment(bottomGlow, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(bottomGlow, new Insets(0, 100, -100, 0));

        // Build sidebar
        VBox sidebar = buildSidebar();

        // Build top bar
        HBox topBar = buildTopBar();

        // Content area
        contentArea.setPadding(new Insets(28));
        contentArea.setSpacing(20);

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Main content container
        VBox mainContent = new VBox();
        mainContent.getChildren().addAll(topBar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        // Root stack with background + layout + toast layer
        backgroundPane.getChildren().addAll(purpleGlow, cyanGlow, bottomGlow, root);

        contentWrapper.getChildren().add(backgroundPane);

        // Init toast system
        ToastNotification.initialize(contentWrapper);

        // Show dashboard by default
        navigateTo("Dashboard");
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(230);

        // Brand header
        VBox brand = new VBox(2);
        brand.getStyleClass().add("sidebar-brand");
        brand.setPadding(new Insets(24, 20, 20, 20));

        Label brandTitle = new Label("NOVA STUDENT");
        brandTitle.getStyleClass().add("sidebar-brand-title");

        Label brandSubtitle = new Label("Smart Student Management");
        brandSubtitle.getStyleClass().add("sidebar-brand-subtitle");

        Label version = new Label("Version 1.0.0");
        version.getStyleClass().add("sidebar-version");

        brand.getChildren().addAll(brandTitle, brandSubtitle, version);

        // Separator
        Separator sep1 = new Separator();
        sep1.getStyleClass().add("glass-separator");
        sep1.setPadding(new Insets(0, 16, 0, 16));

        // Navigation
        navContainer = new VBox(4);
        navContainer.setPadding(new Insets(12, 12, 12, 12));

        Button dashBtn = createNavButton("Dashboard", IconFactory.DASHBOARD);
        Button studentsBtn = createNavButton("Students", IconFactory.STUDENTS);
        Button coursesBtn = createNavButton("Courses", IconFactory.COURSES);
        Button attendanceBtn = createNavButton("Attendance", IconFactory.ATTENDANCE);
        Button resultsBtn = createNavButton("Results", IconFactory.RESULTS);
        Button analyticsBtn = createNavButton("Analytics", IconFactory.ANALYTICS);
        Button reportsBtn = createNavButton("Reports", IconFactory.REPORTS);
        Button settingsBtn = createNavButton("Settings", IconFactory.SETTINGS);

        navContainer.getChildren().addAll(
            dashBtn, studentsBtn, coursesBtn, attendanceBtn,
            resultsBtn, analyticsBtn, reportsBtn, settingsBtn
        );

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // System status
        VBox statusBox = new VBox(6);
        statusBox.setPadding(new Insets(12, 20, 20, 20));

        Label statusTitle = new Label("System Status");
        statusTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: #64748B;");

        HBox dbStatus = createStatusItem("Database Connected", DatabaseConnection.getInstance().testConnection());
        HBox sysStatus = createStatusItem("System Online", true);

        statusBox.getChildren().addAll(statusTitle, dbStatus, sysStatus);

        // Separator
        Separator sep2 = new Separator();
        sep2.getStyleClass().add("glass-separator");
        sep2.setPadding(new Insets(0, 16, 0, 16));

        // Logout
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().addAll("glass-button", "glass-button-small");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-margin: 12;");
        VBox logoutBox = new VBox(logoutBtn);
        logoutBox.setPadding(new Insets(8, 16, 16, 16));
        logoutBtn.setOnAction(e -> {
            SessionManager.getInstance().logout();
            LoginController loginController = new LoginController(stage);
            Scene loginScene = loginController.createScene();
            String themeCss = getClass().getResource("/css/theme.css") != null
                ? getClass().getResource("/css/theme.css").toExternalForm() : null;
            if (themeCss != null) loginScene.getStylesheets().add(themeCss);
            stage.setScene(loginScene);
        });

        sidebar.getChildren().addAll(brand, sep1, navContainer, spacer, sep2, statusBox, logoutBox);
        return sidebar;
    }

    private Button createNavButton(String text, String iconPath) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-nav-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.setFill(Color.web("#64748B"));
        icon.setScaleX(0.65);
        icon.setScaleY(0.65);
        btn.setGraphic(icon);
        btn.setGraphicTextGap(10);

        btn.setOnAction(e -> navigateTo(text));
        return btn;
    }

    private HBox createStatusItem(String text, boolean online) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);

        Circle dot = GlassComponents.statusDot(online);
        Label label = new Label(text);
        label.getStyleClass().add("sidebar-status-text");

        item.getChildren().addAll(dot, label);
        return item;
    }

    private HBox buildTopBar() {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Left: page info
        VBox pageInfo = new VBox(2);
        pageTitle.getStyleClass().add("top-bar-title");
        pageSubtitle.getStyleClass().add("top-bar-subtitle");
        pageInfo.getChildren().addAll(pageTitle, pageSubtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right: search + notification + profile
        HBox rightBox = new HBox(12);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        globalSearch.setPrefWidth(220);
        globalSearch.setOnAction(e -> {
            String query = globalSearch.getText().trim();
            if (!query.isEmpty()) {
                navigateTo("Students");
                studentController.performSearch(query);
            }
        });

        // Notification icon button
        Button notifBtn = new Button();
        notifBtn.getStyleClass().add("icon-button");
        SVGPath notifIcon = new SVGPath();
        notifIcon.setContent(IconFactory.NOTIFICATION);
        notifIcon.setFill(Color.web("#94A3B8"));
        notifIcon.setScaleX(0.6);
        notifIcon.setScaleY(0.6);
        notifBtn.setGraphic(notifIcon);

        // User profile
        HBox profile = new HBox(8);
        profile.setAlignment(Pos.CENTER);

        String userName = SessionManager.getInstance().getCurrentUser() != null
            ? SessionManager.getInstance().getCurrentUser().getFullName() : "Administrator";
        String userRole = SessionManager.getInstance().getCurrentUser() != null
            ? SessionManager.getInstance().getCurrentUser().getRole().name() : "ADMIN";

        StackPane avatar = GlassComponents.avatar(
            userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "AD", 34);

        VBox userInfo = new VBox(1);
        Label nameLabel = new Label(userName);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #F8FAFC;");
        Label roleLabel = new Label(userRole);
        roleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
        userInfo.getChildren().addAll(nameLabel, roleLabel);

        profile.getChildren().addAll(avatar, userInfo);

        rightBox.getChildren().addAll(globalSearch, notifBtn, profile);

        topBar.getChildren().addAll(pageInfo, spacer, rightBox);
        return topBar;
    }

    /**
     * Navigates to a specific page.
     */
    public void navigateTo(String page) {
        contentArea.getChildren().clear();

        // Update nav active state
        for (Node node : navContainer.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.getStyleClass().remove("active");
                if (btn.getText().equals(page)) {
                    btn.getStyleClass().add("active");
                    // Update icon color
                    if (btn.getGraphic() instanceof SVGPath) {
                        ((SVGPath) btn.getGraphic()).setFill(Color.web("#8B5CF6"));
                    }
                } else {
                    if (btn.getGraphic() instanceof SVGPath) {
                        ((SVGPath) btn.getGraphic()).setFill(Color.web("#64748B"));
                    }
                }
            }
        }

        // Load page content
        switch (page) {
            case "Dashboard":
                pageTitle.setText("Dashboard");
                pageSubtitle.setText("University overview and student activity");
                dashboardController.loadContent(contentArea);
                break;
            case "Students":
                pageTitle.setText("Students");
                pageSubtitle.setText("Manage student profiles and academic information");
                studentController.loadContent(contentArea);
                break;
            case "Courses":
                pageTitle.setText("Courses");
                pageSubtitle.setText("Manage courses and departments");
                courseController.loadContent(contentArea);
                break;
            case "Attendance":
                pageTitle.setText("Attendance");
                pageSubtitle.setText("Monitor and manage student attendance");
                attendanceController.loadContent(contentArea);
                break;
            case "Results":
                pageTitle.setText("Results");
                pageSubtitle.setText("Academic results and grade management");
                resultController.loadContent(contentArea);
                break;
            case "Analytics":
                pageTitle.setText("Analytics");
                pageSubtitle.setText("Data insights and performance metrics");
                analyticsController.loadContent(contentArea);
                break;
            case "Reports":
                pageTitle.setText("Reports");
                pageSubtitle.setText("Generate and export academic reports");
                reportController.loadContent(contentArea);
                break;
            case "Settings":
                pageTitle.setText("Settings");
                pageSubtitle.setText("Application configuration and preferences");
                settingsController.loadContent(contentArea);
                break;
        }
    }

    /**
     * Creates the scene with the main layout.
     */
    public Scene createScene() {
        Scene scene = new Scene(contentWrapper, 1280, 800);
        String themeCss = getClass().getResource("/css/theme.css") != null
            ? getClass().getResource("/css/theme.css").toExternalForm() : null;
        if (themeCss != null) {
            scene.getStylesheets().add(themeCss);
        }
        return scene;
    }
}
