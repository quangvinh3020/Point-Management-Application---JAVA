package util;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import model.ClassRoom;
import model.Student;
import util.UserDAO;

public class FileHandler {
    // Read class list from file (Base64 decode)
    public static List<ClassRoom> readClassesFromFile(String filename) {
        List<ClassRoom> classes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            StringBuilder encoded = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                encoded.append(line);
            }
            if (encoded.length() == 0) return classes;
            byte[] decodedBytes = Base64.getDecoder().decode(encoded.toString());
            String decoded = new String(decodedBytes, StandardCharsets.UTF_8);
            String[] lines = decoded.split("\r?\n");
            ClassRoom currentClass = null;
            for (String l : lines) {
                if (l.startsWith("CLASS:")) {
                    // New format: CLASS:classCode|subjectName|className|attendanceWeight|assignmentWeight
                    String classInfo = l.substring(6).trim();
                    String[] parts = classInfo.split("\\|");
                    if (parts.length == 5) {
                        currentClass = new ClassRoom(parts[0], parts[1], parts[2]);
                        try {
                            currentClass.setAttendanceWeight(Double.parseDouble(parts[3]));
                            currentClass.setAssignmentWeight(Double.parseDouble(parts[4]));
                        } catch (Exception e) {
                            currentClass.setAttendanceWeight(0.5);
                            currentClass.setAssignmentWeight(0.5);
                        }
                    } else if (parts.length == 3) {
                        currentClass = new ClassRoom(parts[0], parts[1], parts[2]);
                    } else {
                        currentClass = new ClassRoom(classInfo);
                    }
                    classes.add(currentClass);
                } else if (currentClass != null && !l.isEmpty()) {
                    String[] parts = l.split(",");
                    if (parts.length >= 5) {
                        String id = parts[0];
                        String name = parts[1];
                        List<Double> attendance = parseDoubleList(parts[2]);
                        List<Double> assignments = parseDoubleList(parts[3]);
                        double finalProject = Double.parseDouble(parts[4]);
                        Student s = new Student(id, name);
                        s.setAttendanceScores(attendance);
                        s.setAssignmentScores(assignments);
                        s.setFinalProjectScore(finalProject);
                        currentClass.addStudent(s);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot read file: " + e.getMessage());
        }
        return classes;
    }

    // Write class list to file (Base64 encode)
    public static void writeClassesToFile(String filename, List<ClassRoom> classes) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            StringBuilder sb = new StringBuilder();
            for (ClassRoom c : classes) {
                // New format: CLASS:classCode|subjectName|className|attendanceWeight|assignmentWeight
                sb.append("CLASS:" + c.getClassCode() + "|" + c.getSubjectName() + "|" + c.getClassName() + "|" + c.getAttendanceWeight() + "|" + c.getAssignmentWeight()).append("\n");
                for (Student s : c.getStudents()) {
                    String attendance = joinDoubleList(s.getAttendanceScores());
                    String assignments = joinDoubleList(s.getAssignmentScores());
                    String line = s.getId() + "," + s.getName() + "," + attendance + "," + assignments + "," + s.getFinalProjectScore();
                    sb.append(line).append("\n");
                }
            }
            String encoded = Base64.getEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
            bw.write(encoded);
        } catch (IOException e) {
            System.out.println("Cannot write file: " + e.getMessage());
        }
    }

   // Save class list to database (in base64 format)
    public static void saveClassesToDatabase(int userId, List<ClassRoom> classes) {
        StringBuilder sb = new StringBuilder();
        for (ClassRoom c : classes) {
            sb.append("CLASS:" + c.getClassCode() + "|" + c.getSubjectName() + "|" + c.getClassName() + "|" + c.getAttendanceWeight() + "|" + c.getAssignmentWeight()).append("\n");
            for (Student s : c.getStudents()) {
                String attendance = joinDoubleList(s.getAttendanceScores());
                String assignments = joinDoubleList(s.getAssignmentScores());
                String line = s.getId() + "," + s.getName() + "," + attendance + "," + assignments + "," + s.getFinalProjectScore();
                sb.append(line).append("\n");
            }
        }
        String encoded = java.util.Base64.getEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
        UserDAO.saveUserData(userId, encoded);
    }

    // Read class list from database (base64 decode)
    public static List<ClassRoom> readClassesFromDatabase(int userId) {
        List<ClassRoom> classes = new ArrayList<>();
        String encoded = UserDAO.loadUserData(userId);
        if (encoded == null || encoded.isEmpty()) return classes;
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(encoded);
        String decoded = new String(decodedBytes, StandardCharsets.UTF_8);
        String[] lines = decoded.split("\r?\n");
        ClassRoom currentClass = null;
        for (String l : lines) {
            if (l.startsWith("CLASS:")) {
                String classInfo = l.substring(6).trim();
                String[] parts = classInfo.split("\\|");
                if (parts.length == 5) {
                    currentClass = new ClassRoom(parts[0], parts[1], parts[2]);
                    try {
                        currentClass.setAttendanceWeight(Double.parseDouble(parts[3]));
                        currentClass.setAssignmentWeight(Double.parseDouble(parts[4]));
                    } catch (Exception e) {
                        currentClass.setAttendanceWeight(0.5);
                        currentClass.setAssignmentWeight(0.5);
                    }
                } else if (parts.length == 3) {
                    currentClass = new ClassRoom(parts[0], parts[1], parts[2]);
                } else {
                    currentClass = new ClassRoom(classInfo);
                }
                classes.add(currentClass);
            } else if (currentClass != null && !l.isEmpty()) {
                String[] parts = l.split(",");
                if (parts.length >= 5) {
                    String id = parts[0];
                    String name = parts[1];
                    List<Double> attendance = parseDoubleList(parts[2]);
                    List<Double> assignments = parseDoubleList(parts[3]);
                    double finalProject = Double.parseDouble(parts[4]);
                    Student s = new Student(id, name);
                    s.setAttendanceScores(attendance);
                    s.setAssignmentScores(assignments);
                    s.setFinalProjectScore(finalProject);
                    currentClass.addStudent(s);
                }
            }
        }
        return classes;
    }

    // Read the class list from the text string (base64 decoded)
    public static List<ClassRoom> readClassesFromDatabaseString(String decoded) {
        List<ClassRoom> classes = new ArrayList<>();
        String[] lines = decoded.split("\r?\n");
        ClassRoom currentClass = null;
        for (String l : lines) {
            if (l.startsWith("CLASS:")) {
                String classInfo = l.substring(6).trim();
                String[] parts = classInfo.split("\\|");
                if (parts.length == 5) {
                    currentClass = new ClassRoom(parts[0], parts[1], parts[2]);
                    try {
                        currentClass.setAttendanceWeight(Double.parseDouble(parts[3]));
                        currentClass.setAssignmentWeight(Double.parseDouble(parts[4]));
                    } catch (Exception e) {
                        currentClass.setAttendanceWeight(0.5);
                        currentClass.setAssignmentWeight(0.5);
                    }
                } else if (parts.length == 3) {
                    currentClass = new ClassRoom(parts[0], parts[1], parts[2]);
                } else {
                    currentClass = new ClassRoom(classInfo);
                }
                classes.add(currentClass);
            } else if (currentClass != null && !l.isEmpty()) {
                String[] parts = l.split(",");
                if (parts.length >= 5) {
                    String id = parts[0];
                    String name = parts[1];
                    List<Double> attendance = parseDoubleList(parts[2]);
                    List<Double> assignments = parseDoubleList(parts[3]);
                    double finalProject = Double.parseDouble(parts[4]);
                    Student s = new Student(id, name);
                    s.setAttendanceScores(attendance);
                    s.setAssignmentScores(assignments);
                    s.setFinalProjectScore(finalProject);
                    currentClass.addStudent(s);
                }
            }
        }
        return classes;
    }

    private static List<Double> parseDoubleList(String str) {
        List<Double> list = new ArrayList<>();
        if (str == null || str.isEmpty()) return list;
        String[] arr = str.split("\\|");
        for (String s : arr) {
            try {
                list.add(Double.parseDouble(s));
            } catch (NumberFormatException ignored) {}
        }
        return list;
    }

    // Change joinDoubleList to public
    public static String joinDoubleList(List<Double> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append("|");
        }
        return sb.toString();
    }
} 