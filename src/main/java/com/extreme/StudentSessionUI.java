package com.extreme;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StudentSessionUI {

    private static FaceDetector activeDetector;

    public static void show(Stage stage) {
        // 1. Cleanup previous detector to avoid memory leaks
        if (activeDetector != null) {
            activeDetector.stop();
        }

        Student student = SessionManager.getCurrentStudent();

        // 2. Handle End of Session - Redirect to Summary
        if (student == null) {
            EndSessionUI.show(stage, "Session_Report");
            return;
        }

        // --- UI LAYOUT (Academic Light Theme) ---
        HBox root = new HBox(40);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f1f5f9;"); // Light Slate

        // LEFT: Camera Feed Panel
        VBox cameraPanel = new VBox(20);
        cameraPanel.setAlignment(Pos.CENTER);

        StackPane videoContainer = new StackPane();
        videoContainer.setStyle("-fx-background-color: #e2e8f0; -fx-background-radius: 10; -fx-border-color: #cbd5e1; -fx-border-radius: 10;");
        videoContainer.setPrefSize(550, 400);

        ImageView cameraView = new ImageView();
        cameraView.setFitWidth(530);
        cameraView.setPreserveRatio(true);

        Label statusLabel = new Label("CAMERA STANDBY");
        statusLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 16px;");

        videoContainer.getChildren().addAll(statusLabel, cameraView);
        cameraPanel.getChildren().addAll(videoContainer);

        // RIGHT: Student Info Card
        VBox infoCard = new VBox(25);
        infoCard.setPadding(new Insets(35));
        infoCard.setPrefWidth(400);
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 5);");

        Label header = new Label("STUDENT VERIFICATION");
        header.setStyle("-fx-text-fill: #0369a1; -fx-font-size: 14px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        Label name = new Label(student.getName().toUpperCase());
        name.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 26px; -fx-font-weight: bold;");

        VBox studentDetails = new VBox(12);
        studentDetails.getChildren().addAll(
                createDetailRow("ROLL NO", student.getId()),
                createDetailRow("COURSE", student.getCourse()),
                createDetailRow("PARENTAGE", student.getFather())
        );

        Button verifyBtn = new Button("START VERIFICATION");
        verifyBtn.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-cursor: hand;");
        verifyBtn.setMaxWidth(Double.MAX_VALUE);

        Button skipBtn = new Button("MARK ABSENT / SKIP");
        skipBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc2626; -fx-border-color: #fca5a5; -fx-padding: 12; -fx-cursor: hand;");
        skipBtn.setMaxWidth(Double.MAX_VALUE);

        infoCard.getChildren().addAll(header, name, new Separator(), studentDetails, verifyBtn, skipBtn);

        // --- LOGIC ---
        activeDetector = new FaceDetector();

        verifyBtn.setOnAction(e -> {
            statusLabel.setText("INITIALIZING CAMERA...");
            verifyBtn.setDisable(true);

            activeDetector.startCameraFX(cameraView, () -> {
                Platform.runLater(() -> {
                    SessionManager.markPresent();
                    show(stage); // Move to next student
                });
            });
        });

        skipBtn.setOnAction(e -> {
            SessionManager.markAbsent();
            show(stage);
        });

        root.getChildren().addAll(cameraPanel, infoCard);

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Sentinel - Live Verification");
    }

    private static HBox createDetailRow(String label, String value) {
        Label l = new Label(label + ": ");
        l.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold; -fx-font-size: 12px;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #334155; -fx-font-size: 14px;");
        return new HBox(l, v);
    }
}