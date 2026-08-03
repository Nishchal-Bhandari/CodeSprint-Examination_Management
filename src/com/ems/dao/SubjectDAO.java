package com.ems.dao;

import com.ems.config.DBConnection;
import com.ems.model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    public void addSubject(Subject subject) throws SQLException {
        String sql = "INSERT INTO subject(subject_code, dept_id, subject_name, semester) VALUES(?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, subject.getSubjectCode());
            ps.setInt(2, subject.getDeptId());
            ps.setString(3, subject.getSubjectName());
            ps.setInt(4, subject.getSemester());
            ps.executeUpdate();
        }
    }

    public void updateSubject(Subject subject) throws SQLException {
        String sql = "UPDATE subject SET dept_id = ?, subject_name = ?, semester = ? WHERE subject_code = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subject.getDeptId());
            ps.setString(2, subject.getSubjectName());
            ps.setInt(3, subject.getSemester());
            ps.setString(4, subject.getSubjectCode());
            ps.executeUpdate();
        }
    }

    public void deleteSubject(String subjectCode) throws SQLException {
        String sql = "DELETE FROM subject WHERE subject_code = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, subjectCode);
            ps.executeUpdate();
        }
    }

    public List<Subject> getAllSubjects() throws SQLException {
        String sql = "SELECT subject_code, dept_id, subject_name, semester FROM subject ORDER BY subject_code";
        List<Subject> subjects = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                subjects.add(new Subject(
                        rs.getString("subject_code"),
                        rs.getInt("dept_id"),
                        rs.getString("subject_name"),
                        rs.getInt("semester")
                ));
            }
        }
        return subjects;
    }

    public List<String> getSubjectCodes() throws SQLException {
        String sql = "SELECT subject_code FROM subject ORDER BY subject_code";
        List<String> codes = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                codes.add(rs.getString("subject_code"));
            }
        }
        return codes;
    }
}
