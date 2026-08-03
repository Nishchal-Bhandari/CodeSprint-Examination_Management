package com.ems.dao;

import com.ems.config.DBConnection;
import com.ems.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public long createNotification(String notifType, String title, String body,
                                   String targetRole, String targetUsn) throws SQLException {
        String sql = "INSERT INTO notification(notif_type, title, body, target_role, target_usn) VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, notifType);
            ps.setString(2, title);
            ps.setString(3, body);
            ps.setString(4, targetRole);
            if (targetUsn != null && !targetUsn.trim().isEmpty()) ps.setString(5, targetUsn);
            else ps.setNull(5, Types.VARCHAR);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getLong(1) : -1;
            }
        }
    }

    public void markAsSent(long notifId) throws SQLException {
        String sql = "UPDATE notification SET is_sent=1, sent_at=NOW() WHERE notif_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, notifId);
            ps.executeUpdate();
        }
    }

    public List<Notification> fetchAll() throws SQLException {
        String sql = "SELECT notif_id, notif_type, title, body, target_role, target_usn, is_sent, created_at, sent_at " +
                     "FROM notification ORDER BY created_at DESC";
        List<Notification> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp createdTs = rs.getTimestamp("created_at");
                Timestamp sentTs = rs.getTimestamp("sent_at");
                list.add(new Notification(
                        rs.getLong("notif_id"), rs.getString("notif_type"),
                        rs.getString("title"), rs.getString("body"),
                        rs.getString("target_role"), rs.getString("target_usn"),
                        rs.getInt("is_sent") == 1,
                        createdTs != null ? createdTs.toLocalDateTime() : null,
                        sentTs != null ? sentTs.toLocalDateTime() : null
                ));
            }
        }
        return list;
    }

    public void deleteNotification(long notifId) throws SQLException {
        String sql = "DELETE FROM notification WHERE notif_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, notifId);
            ps.executeUpdate();
        }
    }
}
