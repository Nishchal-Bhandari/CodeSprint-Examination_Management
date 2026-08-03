package com.ems.dao;

import com.ems.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerSheetDAO {

    public void addSheet(String usn, int examId, String barcode, boolean spare,
                         int bundleNo, String roomNo, Integer invigilatorId) throws SQLException {
        String sql = "INSERT INTO answer_sheet(usn, exam_id, barcode, is_spare, bundle_no, room_no, invigilator_id) " +
                     "VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usn);
            ps.setInt(2, examId);
            ps.setString(3, barcode);
            ps.setInt(4, spare ? 1 : 0);
            ps.setInt(5, bundleNo);
            ps.setString(6, roomNo);
            if (invigilatorId != null) ps.setInt(7, invigilatorId);
            else ps.setNull(7, Types.INTEGER);
            ps.executeUpdate();
        }
    }

    public void sealBundle(int examId, String roomNo, int bundleNo) throws SQLException {
        String sql = "UPDATE answer_sheet SET sealed=1 WHERE exam_id=? AND room_no=? AND bundle_no=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setString(2, roomNo);
            ps.setInt(3, bundleNo);
            ps.executeUpdate();
        }
    }

    public List<String[]> fetchSheetsForExam(int examId) throws SQLException {
        String sql = "SELECT ash.sheet_id, ash.usn, s.name, ash.barcode, " +
                     "ash.is_spare, ash.sealed, ash.bundle_no, ash.room_no, ash.invigilator_id, ash.created_at " +
                     "FROM answer_sheet ash JOIN student s ON s.usn = ash.usn " +
                     "WHERE ash.exam_id=? ORDER BY ash.room_no, ash.bundle_no, ash.usn";
        List<String[]> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new String[]{
                            String.valueOf(rs.getLong("sheet_id")),
                            rs.getString("usn"),
                            rs.getString("name"),
                            rs.getString("barcode"),
                            rs.getInt("is_spare") == 1 ? "Spare" : "Main",
                            rs.getInt("sealed") == 1 ? "Sealed" : "Open",
                            rs.getObject("bundle_no") != null ? String.valueOf(rs.getInt("bundle_no")) : "-",
                            rs.getString("room_no"),
                            rs.getObject("invigilator_id") != null ? String.valueOf(rs.getInt("invigilator_id")) : "-",
                            String.valueOf(rs.getTimestamp("created_at"))
                    });
                }
            }
        }
        return rows;
    }

    /** Count bundles per room for summary */
    public List<String[]> bundleSummaryForExam(int examId) throws SQLException {
        String sql = "SELECT room_no, bundle_no, COUNT(*) AS sheet_count, " +
                     "SUM(sealed) AS sealed_count FROM answer_sheet WHERE exam_id=? GROUP BY room_no, bundle_no ORDER BY room_no, bundle_no";
        List<String[]> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new String[]{
                            rs.getString("room_no"),
                            rs.getObject("bundle_no") != null ? String.valueOf(rs.getInt("bundle_no")) : "-",
                            String.valueOf(rs.getInt("sheet_count")),
                            String.valueOf(rs.getInt("sealed_count"))
                    });
                }
            }
        }
        return rows;
    }

    public void deleteSheet(long sheetId) throws SQLException {
        String sql = "DELETE FROM answer_sheet WHERE sheet_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, sheetId);
            ps.executeUpdate();
        }
    }
}
