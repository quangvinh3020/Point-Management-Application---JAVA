package model;
import java.util.*;

public class ClassRoom {
    private String classCode;
    private String subjectName;
    private String className;
    private List<Student> students;
    private double attendanceAssignmentWeight = 0.5;
    private double finalProjectWeight = 0.5;
    private double attendanceWeight = 0.5;
    private double assignmentWeight = 0.5;

    public ClassRoom(String classCode, String subjectName, String className) {
        this.classCode = classCode;
        this.subjectName = subjectName;
        this.className = className;
        this.students = new ArrayList<>();
        this.attendanceWeight = 0.5;
        this.assignmentWeight = 0.5;
    }

    // For backward compatibility (old data)
    public ClassRoom(String className) {
        this("", "", className);
    }

    public String getClassCode() {
        return classCode;
    }
    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }
    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public boolean removeStudentById(String id) {
        return students.removeIf(s -> s.getId().equals(id));
    }

    public Student findStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    public double getAttendanceAssignmentWeight() {
        return attendanceAssignmentWeight;
    }
    public void setAttendanceAssignmentWeight(double w) {
        this.attendanceAssignmentWeight = w;
    }
    public double getFinalProjectWeight() {
        return finalProjectWeight;
    }
    public void setFinalProjectWeight(double w) {
        this.finalProjectWeight = w;
    }

    public double getAttendanceWeight() { return attendanceWeight; }
    public void setAttendanceWeight(double w) { this.attendanceWeight = w; }
    public double getAssignmentWeight() { return assignmentWeight; }
    public void setAssignmentWeight(double w) { this.assignmentWeight = w; }
} 