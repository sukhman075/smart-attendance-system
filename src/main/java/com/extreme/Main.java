package com.extreme;

import nu.pattern.OpenCV;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Initializing Sentinel Academic Portal...");
        try {
            OpenCV.loadShared();
            System.out.println("✅ OpenCV Loaded.");
        } catch (Exception e) {
            System.out.println("⚠️ Falling back to local load.");
        }

        // Launch the ModernDashboard
        ModernDashboard.launch(ModernDashboard.class, args);
    }
}