package com.ems.service;

import com.ems.dao.StudentDAO;
import com.ems.model.Student;

import java.util.List;

public class StudentService {
    private final StudentDAO dao = new StudentDAO();

    public void add(Student student) throws Exception {
        validate(student);
        dao.addStudent(student);
    }

    public void update(Student student) throws Exception {
        validate(student);
        dao.updateStudent(student);
    }

    public void delete(String usn) throws Exception {
        if (usn == null || usn.trim().isEmpty()) {
            throw new IllegalArgumentException("USN is required");
        }
        dao.deleteStudent(usn.trim());
    }

    public List<Student> all() throws Exception {
        return dao.getAllStudents();
    }

    public Student findByUsn(String usn) throws Exception {
        if (usn == null || usn.trim().isEmpty()) return null;
        return dao.getStudentByUsn(usn.trim());
    }

    private void validate(Student student) {
        if (student.getUsn() == null || student.getUsn().trim().isEmpty()) {
            throw new IllegalArgumentException("USN is required");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (student.getDeptId() <= 0) {
            throw new IllegalArgumentException("Department ID must be positive");
        }
        if (student.getSemester() < 1 || student.getSemester() > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8");
        }
    }
}
