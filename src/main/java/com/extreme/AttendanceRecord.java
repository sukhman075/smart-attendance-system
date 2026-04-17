package com.extreme;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AttendanceRecord {

    private static final Set<String> presentStudents = Collections.synchronizedSet(new LinkedHashSet<>());
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void mark(String name) {
        if (name == null || name.trim().isEmpty() || name.equalsIgnoreCase("Unknown")) return;

        synchronized (presentStudents) {
            if (!isPresent(name.trim())) {
                String timestamp = LocalTime.now().format(timeFormatter);
                presentStudents.add(String.format("[%s] %s", timestamp, name.trim()));
            }
        }
    }

    public boolean isPresent(String name) {
        synchronized (presentStudents) {
            return presentStudents.stream()
                    .anyMatch(record -> record.contains("] " + name));
        }
    }

    public List<String> getAll() {
        synchronized (presentStudents) {
            return new ArrayList<>(presentStudents);
        }
    }

    /**
     * FIXED: Added this to resolve the "cannot find symbol clear()" error.
     */
    public void clear() {
        presentStudents.clear();
        System.out.println("🧹 Attendance Record cleared.");
    }

    public void resetSession() {
        clear();
    }
}