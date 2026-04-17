package com.extreme;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.awt.Desktop;
import java.awt.Toolkit;

public class ModernDashboard extends Application {

    private BorderPane root = new BorderPane();
    private FaceDetector activeDetector;

    private final String BG_COLOR = "-fx-background-color: #f1f5f9;";
    private final String CARD_STYLE = "-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);";

    @Override
    public void start(Stage stage) {
        root.setStyle(BG_COLOR);
        showTeacherDashboard(stage);

        Scene scene = new Scene(root, 1280, 800);
        stage.setScene(scene);
        stage.setTitle("Sentinel AI - Premium Academic Portal");
        stage.show();
    }

    /**
     * LANDING PAGE: Redesigned for a "Premium" look
     */
    public void showTeacherDashboard(Stage stage) {
        HBox mainHub = new HBox(50);
        mainHub.setAlignment(Pos.CENTER);
        mainHub.setPadding(new Insets(60));

        // --- LEFT SIDE: THE SETUP CARD ---
        VBox setupCard = new VBox(25);
        setupCard.setPadding(new Insets(40));
        setupCard.setPrefWidth(450);
        setupCard.setStyle(CARD_STYLE);

        Label welcome = new Label("Welcome Back, Professor");
        welcome.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        Label title = new Label("Session Manager");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        ComboBox<String> course = createStyledCombo("Select Course", "BCA-6A", "BCA-6B", "CSE-4C");
        ComboBox<String> subject = createStyledCombo("Select Subject", "Java FX", "OpenCV AI", "Web Dev");

        Button startBtn = new Button("INITIALIZE PORTAL");
        startBtn.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-cursor: hand; -fx-background-radius: 8;");
        startBtn.setMaxWidth(Double.MAX_VALUE);

        startBtn.setOnAction(e -> {
            if (course.getValue() != null && subject.getValue() != null) {
                SessionManager.loadStudents();
                showStudentProfile(stage, subject.getValue() + "_" + course.getValue());
            }
        });

        setupCard.getChildren().addAll(welcome, title, new Separator(), new Label("CLASS DETAILS"), course, subject, startBtn);

        // --- RIGHT SIDE: QUICK STATS / ARCHIVE ---
        VBox statsPanel = new VBox(20);
        statsPanel.setPrefWidth(400);

        Label archiveTitle = new Label("RECENT ACTIVITY");
        archiveTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");

        Button openLogs = new Button("📂 BROWSE ATTENDANCE ARCHIVE (.xlsx)");
        openLogs.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-padding: 20; -fx-cursor: hand;");
        openLogs.setMaxWidth(Double.MAX_VALUE);
        openLogs.setOnAction(e -> openExcelArchive(stage));

        statsPanel.getChildren().addAll(archiveTitle, openLogs);

        mainHub.getChildren().addAll(setupCard, statsPanel);
        root.setCenter(mainHub);

        // This ensures the root is correctly attached to the scene during a "Return Home"
        if (stage.getScene() != null) stage.getScene().setRoot(root);
    }

    /**
     * STUDENT PROFILE: Full screen instructions & identity
     */
    private void showStudentProfile(Stage stage, String sessionID) {
        Student s = SessionManager.getCurrentStudent();
        if (s == null) {
            EndSessionUI.show(stage, sessionID);
            return;
        }

        VBox profileRoot = new VBox(35);
        profileRoot.setAlignment(Pos.CENTER);
        profileRoot.setPadding(new Insets(50));

        Label avatar = new Label(s.getName().substring(0, 1).toUpperCase());
        avatar.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-size: 70px; -fx-min-width: 140px; -fx-min-height: 140px; -fx-background-radius: 70px; -fx-alignment: center;");

        Label nameLabel = new Label(s.getName());
        nameLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        VBox instructionBox = new VBox(10);
        instructionBox.setAlignment(Pos.CENTER);
        instructionBox.getChildren().addAll(
                new Label("📋 RULES FOR VERIFICATION"),
                new Label("• Stand precisely in front of the camera"),
                new Label("• Maintain a neutral expression"),
                new Label("• Wait for the success 'Beep'")
        );
        instructionBox.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16px;");

        HBox btns = new HBox(20);
        btns.setAlignment(Pos.CENTER);
        Button scan = new Button("PROCEED TO SCAN");
        Button skip = new Button("MARK ABSENT");

        scan.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-padding: 15 40; -fx-font-weight: bold;");
        skip.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #ef4444; -fx-padding: 15 40;");

        btns.getChildren().addAll(skip, scan);
        profileRoot.getChildren().addAll(avatar, nameLabel, instructionBox, btns);
        root.setCenter(profileRoot);

        scan.setOnAction(e -> startVerificationScan(stage, sessionID, s.getName()));
        skip.setOnAction(e -> {
            SessionManager.markAbsent();
            showStudentProfile(stage, sessionID);
        });
    }

    private void startVerificationScan(Stage stage, String sessionID, String studentName) {
        VBox scanView = new VBox(20);
        scanView.setAlignment(Pos.CENTER);
        scanView.setStyle("-fx-background-color: #0f172a;");

        ImageView feed = new ImageView();
        feed.setFitHeight(550);
        feed.setPreserveRatio(true);

        Label status = new Label("INITIALIZING CAMERA...");
        status.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 20px;");

        scanView.getChildren().addAll(status, feed);
        root.setCenter(scanView);

        activeDetector = new FaceDetector();
        activeDetector.startCameraFX(feed, () -> {
            Platform.runLater(() -> {
                Toolkit.getDefaultToolkit().beep();
                status.setText("✅ SUCCESS: " + studentName.toUpperCase() + " MARKED");
                status.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 24px; -fx-font-weight: bold;");

                new Thread(() -> {
                    try { Thread.sleep(1500); } catch (Exception ex) {}
                    Platform.runLater(() -> {
                        SessionManager.markPresent();
                        showStudentProfile(stage, sessionID);
                    });
                }).start();
            });
        });
    }

    private ComboBox<String> createStyledCombo(String prompt, String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.setPromptText(prompt);
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-padding: 5;");
        return cb;
    }

    private void openExcelArchive(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("attendance-records"));
        File file = fc.showOpenDialog(stage);
        if (file != null) { try { Desktop.getDesktop().open(file); } catch (Exception e) {} }
    }
}