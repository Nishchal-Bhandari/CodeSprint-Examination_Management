package com.ems.dao;

import com.ems.config.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacultyDutyDAO {
    public String autoAssignDuty(int examId, String roomNo, String role, int requiredCount) throws SQLException {
        String call = "{CALL sp_assign_faculty_duties(?, ?, ?, ?)}";
        try (Connection connection = DBConnection.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, examId);
            cs.setString(2, roomNo);
            cs.setString(3, role);
            cs.setInt(4, requiredCount);
            boolean hasResult = cs.execute();
            if (hasResult) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next()) {
                        return rs.getString("message");
                    }
                }
            }
            return "Faculty assignment procedure executed";
        }
    }

    public List<String> fetchRoomsForExam(int examId) throws SQLException {
        String sql = "SELECT DISTINCT b.room_no " +
                "FROM seating_allocation sa " +
                "JOIN bench b ON sa.bench_no = b.bench_no " +
                "WHERE sa.exam_id = ? " +
                "ORDER BY b.room_no";
        List<String> rooms = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rooms.add(rs.getString("room_no"));
                }
            }
        }
        if (rooms.isEmpty()) {
            String fallbackSql = "SELECT room_no FROM room ORDER BY room_no";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement ps = connection.prepareStatement(fallbackSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rooms.add(rs.getString("room_no"));
                }
            }
        }
        return rooms;
    }

    public boolean manualAssignDuty(int examId, String roomNo, int facultyId, String role) throws SQLException {
        String insertSql = "INSERT INTO faculty_duty (faculty_id, exam_id, room_no, role) VALUES (?, ?, ?, ?)";
        String updateWorkloadSql = "UPDATE faculty SET workload = workload + 1 WHERE faculty_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setInt(1, facultyId);
                    ps.setInt(2, examId);
                    ps.setString(3, roomNo);
                    ps.setString(4, role);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(updateWorkloadSql)) {
                    ps.setInt(1, facultyId);
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean swapFacultyDuty(long dutyId1, long dutyId2) throws SQLException {
        String fetchSql = "SELECT duty_id, faculty_id FROM faculty_duty WHERE duty_id IN (?, ?)";
        String updateSql = "UPDATE faculty_duty SET faculty_id = ? WHERE duty_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int faculty1 = -1, faculty2 = -1;
                try (PreparedStatement ps = conn.prepareStatement(fetchSql)) {
                    ps.setLong(1, dutyId1);
                    ps.setLong(2, dutyId2);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            long id = rs.getLong("duty_id");
                            if (id == dutyId1) faculty1 = rs.getInt("faculty_id");
                            if (id == dutyId2) faculty2 = rs.getInt("faculty_id");
                        }
                    }
                }

                if (faculty1 == -1 || faculty2 == -1) return false;

                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, faculty2);
                    ps.setLong(2, dutyId1);
                    ps.executeUpdate();

                    ps.setInt(1, faculty1);
                    ps.setLong(2, dutyId2);
                    ps.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<String[]> fetchAllDuties() throws SQLException {
        String sql = "SELECT fd.duty_id, fd.exam_id, fd.room_no, fd.role, f.faculty_id, f.faculty_name, d.dept_name " +
                "FROM faculty_duty fd " +
                "JOIN faculty f ON f.faculty_id = fd.faculty_id " +
                "LEFT JOIN department d ON d.dept_id = f.dept_id " +
                "ORDER BY fd.exam_id, fd.room_no";
        List<String[]> rows = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{
                        String.valueOf(rs.getLong("duty_id")),
                        String.valueOf(rs.getInt("exam_id")),
                        rs.getString("room_no"),
                        rs.getString("role"),
                        String.valueOf(rs.getInt("faculty_id")),
                        rs.getString("faculty_name"),
                        rs.getString("dept_name") != null ? rs.getString("dept_name") : "N/A"
                });
            }
        }
        return rows;
    }

    public int deleteDutiesForExam(int examId) throws SQLException {
        String selectSql = "SELECT faculty_id FROM faculty_duty WHERE exam_id = ?";
        String deleteSql = "DELETE FROM faculty_duty WHERE exam_id = ?";
        String updateSql = "UPDATE faculty SET workload = GREATEST(workload - 1, 0) WHERE faculty_id = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                List<Integer> facultyIds = new ArrayList<>();
                try (PreparedStatement selectPs = connection.prepareStatement(selectSql)) {
                    selectPs.setInt(1, examId);
                    try (ResultSet rs = selectPs.executeQuery()) {
                        while (rs.next()) {
                            facultyIds.add(rs.getInt("faculty_id"));
                        }
                    }
                }

                if (!facultyIds.isEmpty()) {
                    try (PreparedStatement updatePs = connection.prepareStatement(updateSql)) {
                        for (int fid : facultyIds) {
                            updatePs.setInt(1, fid);
                            updatePs.addBatch();
                        }
                        updatePs.executeBatch();
                    }
                }

                int deleted;
                try (PreparedStatement deletePs = connection.prepareStatement(deleteSql)) {
                    deletePs.setInt(1, examId);
                    deleted = deletePs.executeUpdate();
                }

                connection.commit();
                return deleted;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
