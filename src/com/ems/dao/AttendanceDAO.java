package com.ems.dao;

import com.ems.config.DBConnection;
import com.ems.model.ExamAttendance;
import com.ems.model.MalpracticeLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // ---- Attendance ----

    public void markAttendance(String usn, int examId, boolean present) throws SQLException {
        String sql = "INSERT INTO exam_attendance(usn, exam_id, is_present) VALUES(?,?,?) " +
                     "ON DUPLICATE KEY UPDATE is_present=VALUES(is_present), marked_at=NOW()";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usn);
            ps.setInt(2, examId);
            ps.setInt(3, present ? 1 : 0);
            ps.executeUpdate();
        }
    }

    public List<ExamAttendance> fetchAttendanceForExam(int examId) throws SQLException {
        String sql = "SELECT attendance_id, usn, exam_id, is_present, marked_at FROM exam_attendance WHERE exam_id=? ORDER BY usn";
        List<ExamAttendance> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("marked_at");
                    list.add(new ExamAttendance(
                            rs.getLong("attendance_id"),
                            rs.getString("usn"),
                            rs.getInt("exam_id"),
                            rs.getInt("is_present") == 1,
                            ts != null ? ts.toLocalDateTime() : null
                    ));
                }
            }
        }
        return list;
    }

    /** Returns rows: [usn, name, status] */
    public List<String[]> fetchAttendanceReport(int examId) throws SQLException {
        String sql = "SELECT s.usn, s.name, COALESCE(ea.is_present, -1) AS is_present " +
                     "FROM seating_allocation sa " +
                     "JOIN student s ON s.usn = sa.usn " +
                     "LEFT JOIN exam_attendance ea ON ea.usn = s.usn AND ea.exam_id = sa.exam_id " +
                     "WHERE sa.exam_id = ? ORDER BY sa.bench_no, sa.seat_position";
        List<String[]> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int ip = rs.getInt("is_present");
                    String status = ip == 1 ? "Present" : ip == 0 ? "Absent" : "Not Marked";
                    rows.add(new String[]{rs.getString("usn"), rs.getString("name"), status});
                }
            }
        }
        return rows;
    }

    // ---- Washroom Log ----

    public void logWashroomExit(String usn, int examId) throws SQLException {
        String sql = "INSERT INTO washroom_log(usn, exam_id) VALUES(?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usn);
            ps.setInt(2, examId);
            ps.executeUpdate();
        }
    }

    public void markWashroomReturn(long wlId) throws SQLException {
        String sql = "UPDATE washroom_log SET return_time=NOW() WHERE wl_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, wlId);
            ps.executeUpdate();
        }
    }

    public List<String[]> fetchWashroomLog(int examId) throws SQLException {
        String sql = "SELECT wl.wl_id, wl.usn, s.name, wl.exit_time, wl.return_time " +
                     "FROM washroom_log wl JOIN student s ON s.usn = wl.usn " +
                     "WHERE wl.exam_id=? ORDER BY wl.exit_time DESC";
        List<String[]> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new String[]{
                            String.valueOf(rs.getLong("wl_id")),
                            rs.getString("usn"),
                            rs.getString("name"),
                            String.valueOf(rs.getTimestamp("exit_time")),
                            rs.getTimestamp("return_time") != null ? String.valueOf(rs.getTimestamp("return_time")) : "Still Out"
                    });
                }
            }
        }
        return rows;
    }

    // ---- Malpractice ----

    public void logMalpractice(String usn, int examId, String roomNo, String incidentType, String description, Integer reportedBy) throws SQLException {
        String sql = "INSERT INTO malpractice_log(usn, exam_id, room_no, incident_type, description, reported_by) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usn);
            ps.setInt(2, examId);
            ps.setString(3, roomNo);
            ps.setString(4, incidentType);
            ps.setString(5, description);
            if (reportedBy != null) ps.setInt(6, reportedBy);
            else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
        }
    }

    public List<String[]> fetchMalpracticeForExam(int examId) throws SQLException {
        String sql = "SELECT ml.mp_id, ml.usn, s.name, ml.room_no, ml.incident_type, ml.description, ml.reported_at " +
                     "FROM malpractice_log ml JOIN student s ON s.usn = ml.usn " +
                     "WHERE ml.exam_id=? ORDER BY ml.reported_at DESC";
        List<String[]> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new String[]{
                            String.valueOf(rs.getLong("mp_id")),
                            rs.getString("usn"),
                            rs.getString("name"),
                            rs.getString("room_no"),
                            rs.getString("incident_type"),
                            rs.getString("description"),
                            String.valueOf(rs.getTimestamp("reported_at"))
                    });
                }
            }
        }
        return rows;
    }

    public List<String[]> fetchAllMalpractice() throws SQLException {
        String sql = "SELECT ml.mp_id, ml.usn, s.name, ml.exam_id, ml.room_no, ml.incident_type, ml.description, ml.reported_at " +
                     "FROM malpractice_log ml JOIN student s ON s.usn = ml.usn ORDER BY ml.reported_at DESC";
        List<String[]> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{
                        String.valueOf(rs.getLong("mp_id")),
                        rs.getString("usn"),
                        rs.getString("name"),
                        String.valueOf(rs.getInt("exam_id")),
                        rs.getString("room_no"),
                        rs.getString("incident_type"),
                        rs.getString("description"),
                        String.valueOf(rs.getTimestamp("reported_at"))
                });
            }
        }
        return rows;
    }
}
