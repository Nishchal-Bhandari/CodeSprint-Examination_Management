package com.ems.dao;

import com.ems.config.DBConnection;
import com.ems.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {
    public void addDepartment(Department dept) throws SQLException {
        String sql = "INSERT INTO department(dept_id, dept_name) VALUES(?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dept.getDeptId());
            ps.setString(2, dept.getDeptName());
            ps.executeUpdate();
        }
    }

    public void deleteDepartment(int deptId) throws SQLException {
        String sql = "DELETE FROM department WHERE dept_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ps.executeUpdate();
        }
    }

    public List<Department> getAllDepartments() throws SQLException {
        String sql = "SELECT dept_id, dept_name FROM department ORDER BY dept_id";
        List<Department> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Department(rs.getInt("dept_id"), rs.getString("dept_name")));
            }
        }
        return list;
    }
}
