package com.extreme;

import java.util.Objects;

public class Student {

    private final String id;
    private final String name;
    private final String course;
    private final String father;
    private String status; // States: PENDING, VERIFIED, ABSENT

    public Student(String id, String name, String course, String father, String status) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.father = father;
        this.status = (status == null) ? "PENDING" : status.toUpperCase();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCourse() { return course; }
    public String getFather() { return father; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status.toUpperCase();
    }

    // Standardized for consistent UI display
    @Override
    public String toString() {
        return String.format("[%s] %s | %s", id, name, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}