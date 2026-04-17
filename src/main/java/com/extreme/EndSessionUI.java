package com.extreme;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EndSessionUI {
    public static void show(Stage stage, String sessionID) {
        VBox endRoot = new VBox(30);
        endRoot.setAlignment(Pos.CENTER);
        endRoot.setStyle("-fx-background-color: #f1f5f9;");

        Label finishMsg = new Label("SESSION WRAPPED UP");
        finishMsg.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0369a1;");

        Button homeBtn = new Button("BACK TO MAIN HUB");
        homeBtn.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-padding: 15 40; -fx-font-weight: bold; -fx-cursor: hand;");

        homeBtn.setOnAction(e -> {
            SessionManager.resetAll();
            // Call the dashboard but don't just "new" it—tell it to show itself on the stage
            ModernDashboard dashboard = new ModernDashboard();
            dashboard.showTeacherDashboard(stage);
        });

        endRoot.getChildren().addAll(finishMsg, homeBtn);

        // This line is what makes the screen actually change
        stage.getScene().setRoot(endRoot);
    }
}