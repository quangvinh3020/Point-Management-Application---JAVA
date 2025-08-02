package model;

import java.util.*;

public class Student {
    private String id;
    private String name;
    private double grade; // old summary score, keep for compatibility
    private List<Double> attendanceScores; // points by school day
    private List<Double> assignmentScores; // individual homework score
    private double finalProjectScore; // final score
    public Student(String id, String name, double grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.attendanceScores = new ArrayList<>();
        this.assignmentScores = new ArrayList<>();
        this.finalProjectScore = 0.0;
    }

    // New constructor for new data
    public Student(String id, String name) {
        this(id, name, 0.0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public List<Double> getAttendanceScores() {
        return attendanceScores;
    }

    public void setAttendanceScores(List<Double> attendanceScores) {
        this.attendanceScores = attendanceScores;
    }

    public List<Double> getAssignmentScores() {
        return assignmentScores;
    }

    public void setAssignmentScores(List<Double> assignmentScores) {
        this.assignmentScores = assignmentScores;
    }

    public double getFinalProjectScore() {
        return finalProjectScore;
    }

    public void setFinalProjectScore(double finalProjectScore) {
        this.finalProjectScore = finalProjectScore;
    }

    public double getPersonalScore() {
        double att = 0;
        int attCount = 0;
        for (double d : attendanceScores) { att += d; attCount++; }
        double avgAttendance = attCount > 0 ? att / attCount : 0.0;

        double assign = 0;
        int assignCount = 0;
        for (double d : assignmentScores) { assign += d; assignCount++; }
        double avgAssignment = assignCount > 0 ? assign / assignCount : 0.0;

        return (avgAttendance + avgAssignment) / 2.0;
    }

    public double getPersonalScore(ClassRoom c) {
        double att = 0;
        int attCount = 0;
        for (double d : attendanceScores) { att += d; attCount++; }
        double avgAttendance = attCount > 0 ? att / attCount : 0.0;

        double assign = 0;
        int assignCount = 0;
        for (double d : assignmentScores) { assign += d; assignCount++; }
        double avgAssignment = assignCount > 0 ? assign / assignCount : 0.0;

        return avgAttendance * c.getAttendanceWeight() + avgAssignment * c.getAssignmentWeight();
    }

    public double getFinalGrade() {
        double personalScore = getPersonalScore();
        return (personalScore + finalProjectScore) / 2.0;
    }

    public double getFinalGrade(double attendanceAssignmentWeight, double finalProjectWeight) {
        double personalScore = getPersonalScore();
        return personalScore * attendanceAssignmentWeight + finalProjectScore * finalProjectWeight;
    }
    public double getFinalGrade(ClassRoom c) {
        return getFinalGrade(c.getAttendanceAssignmentWeight(), c.getFinalProjectWeight(), c);
    }

    public double getFinalGrade(double attendanceAssignmentWeight, double finalProjectWeight, ClassRoom c) {
        double personalScore = getPersonalScore(c);
        return personalScore * attendanceAssignmentWeight + finalProjectScore * finalProjectWeight;
    }

   // Convert decimal points to quadratic (only A+, A, B+, B, C+, C, F)
    public String getFinalGradeLetter() {
        double score = getFinalGrade();
        if (score >= 9.0) return "A+";
        if (score >= 8.0) return "A";
        if (score >= 7.0) return "B+";
        if (score >= 6.5) return "B";
        if (score >= 5.5) return "C+";
        if (score >= 5.0) return "C";
        return "F";
    }

    public String getFinalGradeLetter(ClassRoom c) {
        double score = getFinalGrade(c);
        if (score >= 9.0) return "A+";
        if (score >= 8.0) return "A";
        if (score >= 7.0) return "B+";
        if (score >= 6.5) return "B";
        if (score >= 5.5) return "C+";
        if (score >= 5.0) return "C";
        return "F";
    }

    @Override
    public String toString() {
        return id + "," + name + "," + getFinalGrade();
    }
} 