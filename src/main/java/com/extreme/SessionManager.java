package com.extreme;

import java.util.*;

public class SessionManager {

    private static final List<Student> students = new ArrayList<>();
    private static int currentIndex = 0;

    /**
     * Resets the entire session state and wipes the attendance record.
     */
    public static void loadStudents() {
        students.clear();
        currentIndex = 0;

        // Clear the actual attendance logs for the Excel exporter
        new AttendanceRecord().clear();

        // MOCK DATA: Replace with your actual database/list loading logic
        students.add(new Student("23BCA10104", "Sukhjeet Chouhan", "BCA", "Surinderpal singh", "Pending"));
        students.add(new Student("23BCA10189", "Manveer Chouhan ", "BCA", "Leela Singh", "Pending"));
        students.add(new Student("23BCA10404", "Manjot Singh", "BCA", "Surjit Singh", "Pending"));

        System.out.println("📂 New Session Prepared with " + students.size() + " students.");
    }

    public static Student getCurrentStudent() {
        if (currentIndex < students.size()) {
            return students.get(currentIndex);
        }
        return null;
    }

    public static void markPresent() {
        Student s = getCurrentStudent();
        if (s != null) {
            s.setStatus("Verified");
            new AttendanceRecord().mark(s.getName());
            currentIndex++;
        }
    }

    /**
     * FIXED: This method was missing, causing the StudentSessionUI error.
     */
    public static void markAbsent() {
        Student s = getCurrentStudent();
        if (s != null) {
            s.setStatus("Absent");
            // We don't call AttendanceRecord.mark() because they aren't present
            currentIndex++;
            System.out.println("📌 Student marked absent. Moving to next.");
        }
    }

    public static boolean isFinished() {
        return currentIndex >= students.size();
    }

    public static void resetAll() {
        students.clear();
        currentIndex = 0;
        new AttendanceRecord().clear();
    }

    public static void cleanup() {
        resetAll();
    }
}