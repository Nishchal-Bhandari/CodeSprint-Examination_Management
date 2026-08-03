package com.ems.dao;

import com.ems.config.DBConnection;
import com.ems.model.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    public void log(String username, String action, String tableName,
                    String recordKey, String oldValue, String newValue) throws SQLException {
        String sql = "INSERT INTO audit_log(username, action, table_name, record_key, old_value, new_value) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, action);
            ps.setString(3, tableName);
            ps.setString(4, recordKey);
            ps.setString(5, oldValue);
            ps.setString(6, newValue);
            ps.executeUpdate();
        }
    }

    public List<AuditLog> fetchAll() throws SQLException {
        String sql = "SELECT audit_id, username, action, table_name, record_key, old_value, new_value, logged_at, ip_address " +
                     "FROM audit_log ORDER BY logged_at DESC LIMIT 500";
        List<AuditLog> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("logged_at");
                list.add(new AuditLog(
                        rs.getLong("audit_id"), rs.getString("username"),
                        rs.getString("action"), rs.getString("table_name"),
                        rs.getString("record_key"), rs.getString("old_value"),
                        rs.getString("new_value"),
                        ts != null ? ts.toLocalDateTime() : null,
                        rs.getString("ip_address")
                ));
            }
        }
        return list;
    }

    public List<AuditLog> fetchByUser(String username) throws SQLException {
        String sql = "SELECT audit_id, username, action, table_name, record_key, old_value, new_value, logged_at, ip_address " +
                     "FROM audit_log WHERE username=? ORDER BY logged_at DESC LIMIT 200";
        List<AuditLog> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("logged_at");
                    list.add(new AuditLog(
                            rs.getLong("audit_id"), rs.getString("username"),
                            rs.getString("action"), rs.getString("table_name"),
                            rs.getString("record_key"), rs.getString("old_value"),
                            rs.getString("new_value"),
                            ts != null ? ts.toLocalDateTime() : null,
                            rs.getString("ip_address")
                    ));
                }
            }
        }
        return list;
    }

    public void purgeOlderThan(int days) throws SQLException {
        String sql = "DELETE FROM audit_log WHERE logged_at < DATE_SUB(NOW(), INTERVAL ? DAY)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, days);
            ps.executeUpdate();
        }
    }
}
