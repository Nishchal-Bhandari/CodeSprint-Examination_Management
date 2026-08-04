package com.ems.dao;

import com.ems.config.DBConnection;
import com.ems.model.Faculty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {
    public void addFaculty(Faculty faculty) throws SQLException {
        String sql = "INSERT INTO faculty(faculty_id, faculty_name, dept_id, workload, availability, email) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, faculty.getFacultyId());
            ps.setString(2, faculty.getFacultyName());
            ps.setInt(3, faculty.getDeptId());
            ps.setInt(4, faculty.getWorkload());
            ps.setString(5, faculty.getAvailability());
            if (faculty.getEmail() == null || faculty.getEmail().trim().isEmpty()) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, faculty.getEmail());
            }
            ps.executeUpdate();
        }
    }

    public void deleteFaculty(int facultyId) throws SQLException {
        String sql = "DELETE FROM faculty WHERE faculty_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            ps.executeUpdate();
        }
    }

    public void updateAvailability(int facultyId, String availability) throws SQLException {
        String sql = "UPDATE faculty SET availability = ? WHERE faculty_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, availability);
            ps.setInt(2, facultyId);
            ps.executeUpdate();
        }
    }

    public List<Faculty> getAllFaculty() throws SQLException {
        String sql = "SELECT faculty_id, faculty_name, dept_id, workload, availability, email FROM faculty ORDER BY faculty_id";
        List<Faculty> list = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Faculty(
                        rs.getInt("faculty_id"),
                        rs.getString("faculty_name"),
                        rs.getInt("dept_id"),
                        rs.getInt("workload"),
                        rs.getString("availability"),
                        rs.getString("email")
                ));
            }
        }
        return list;
    }
}
