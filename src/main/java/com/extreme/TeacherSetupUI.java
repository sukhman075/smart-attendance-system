package com.extreme;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.awt.Desktop;

public class TeacherSetupUI {

    public static void show(Stage stage) {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f1f5f9;");

        // --- HEADER ---
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label title = new Label("SENTINEL ACADEMIC PORTAL");
        title.setStyle("-fx-text-fill: #0369a1; -fx-font-size: 28px; -fx-font-weight: bold;");
        header.getChildren().add(title);

        // --- INSTRUCTION BOX ---
        VBox instr = new VBox(8);
        instr.setPadding(new Insets(15));
        instr.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8;");
        instr.setMaxWidth(400);

        Label instrTitle = new Label("SESSION GUIDELINES:");
        instrTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");

        String[] steps = {
                "• Select the Subject and Class Section.",
                "• Ensure the camera has a clear view of the student.",
                "• Verify identities one by one in the session.",
                "• Export the .xlsx report before concluding."
        };
        instr.getChildren().add(instrTitle);
        for (String s : steps) {
            Label step = new Label(s);
            step.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");
            instr.getChildren().add(step);
        }

        // --- SETUP CARD ---
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);");
        card.setMaxWidth(400);

        ComboBox<String> subject = new ComboBox<>();
        subject.setPromptText("Select Subject");
        subject.getItems().addAll("Java Programming", "Database Systems", "Web Technology");
        subject.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> section = new ComboBox<>();
        section.setPromptText("Select Section");
        section.getItems().addAll("BCA-6A", "BCA-6B", "CSE-4C");
        section.setMaxWidth(Double.MAX_VALUE);

        Button startBtn = new Button("INITIALIZE SESSION");
        startBtn.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-cursor: hand;");
        startBtn.setMaxWidth(Double.MAX_VALUE);

        Button viewLogsBtn = new Button("📂 BROWSE SAVED ATTENDANCE");
        viewLogsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #0369a1; -fx-font-weight: bold; -fx-cursor: hand;");
        viewLogsBtn.setMaxWidth(Double.MAX_VALUE);

        startBtn.setOnAction(e -> {
            if (subject.getValue() != null && section.getValue() != null) {
                startBtn.setText("PREPARING SESSION...");
                startBtn.setDisable(true);
                new Thread(() -> {
                    try { Thread.sleep(1500); } catch (Exception ex) {}
                    Platform.runLater(() -> {
                        SessionManager.loadStudents();
                        StudentSessionUI.show(stage);
                    });
                }).start();
            }
        });

        viewLogsBtn.setOnAction(e -> openExcelArchive(stage));

        card.getChildren().addAll(new Label("CONFIGURATION"), subject, section, startBtn, new Separator(), viewLogsBtn);
        root.getChildren().addAll(header, instr, card);

        stage.setScene(new Scene(root, 700, 680));
        stage.show();
    }

    private static void openExcelArchive(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("View Records");
        File dir = new File("attendance-records");
        if (!dir.exists()) dir.mkdir();
        fileChooser.setInitialDirectory(dir);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try { Desktop.getDesktop().open(file); } catch (Exception ex) {}
        }
    }
}