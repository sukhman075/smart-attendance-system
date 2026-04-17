package com.extreme;

import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class FaceAttendancePro extends Application {

    @Override
    public void init() throws Exception {
        // --- PRE-LOAD CORE LOGIC ---
        try {
            // Load the OpenCV Native Library before the UI starts
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("✅ OpenCV Libraries Loaded Successfully.");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("❌ Critical Error: OpenCV Library not found in Path!");
            // Optional: Show a popup or exit
        }
    }

    @Override
    public void start(Stage stage) {
        // Setting up the initial window properties
        stage.setTitle("SENTINEL AI - Biometric Verification");

        // Ensure the app looks clean on launch
        stage.setResizable(true);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        // Start with the Teacher Setup UI
        TeacherSetupUI.show(stage);

        stage.show();
    }

    @Override
    public void stop() {
        // --- CLEAN UP ---
        System.out.println("Stopping Sentinel AI...");
        // Ensure any running camera feeds or executors are shut down
        SessionManager.cleanup();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}