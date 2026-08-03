package com.ems.dao;

import com.ems.config.DBConnection;
import com.ems.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO student(usn, name, email, dept_id, semester) VALUES(?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, student.getUsn());
            ps.setString(2, student.getName());
            ps.setString(3, student.getEmail());
            ps.setInt(4, student.getDeptId());
            ps.setInt(5, student.getSemester());
            ps.executeUpdate();
        }
    }

    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE student SET name = ?, email = ?, dept_id = ?, semester = ? WHERE usn = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setInt(3, student.getDeptId());
            ps.setInt(4, student.getSemester());
            ps.setString(5, student.getUsn());
            ps.executeUpdate();
        }
    }

    public void deleteStudent(String usn) throws SQLException {
        String sql = "DELETE FROM student WHERE usn = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usn);
            ps.executeUpdate();
        }
    }

    public List<Student> getAllStudents() throws SQLException {
        String sql = "SELECT usn, name, email, dept_id, semester FROM student ORDER BY usn";
        List<Student> students = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(new Student(
                        rs.getString("usn"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("dept_id"),
                        rs.getInt("semester")
                ));
            }
        }
        return students;
    }

    public Student getStudentByUsn(String usn) throws SQLException {
        String sql = "SELECT usn, name, email, dept_id, semester FROM student WHERE usn = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getString("usn"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getInt("dept_id"),
                            rs.getInt("semester")
                    );
                }
            }
        }
        return null;
    }
}
