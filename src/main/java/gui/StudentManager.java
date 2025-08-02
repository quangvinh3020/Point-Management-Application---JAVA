package gui;

import java.util.*;
import model.Student;

public class StudentManager {
    private List<Student> students;

    public StudentManager() {
        students = new ArrayList<>();
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

    public List<Student> findStudentsByName(String name) {
        List<Student> result = new ArrayList<>();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }

    public List<Student> getAllStudents() {
        return students;
    }

    public void updateStudent(String id, String newName, double newGrade) {
        Student s = findStudentById(id);
        if (s != null) {
            s.setName(newName);
            s.setGrade(newGrade);
        }
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
} 