package com.ems.dao;

import com.ems.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Returns a String[3]: {role, student_usn, username} if credentials are valid, else null.
     * student_usn is null for non-student roles.
     */
    public String[] authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, password_hash, role, student_usn FROM app_user WHERE username = ? AND is_active = 1";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                int userId = rs.getInt("user_id");
                String stored = rs.getString("password_hash");
                String role = rs.getString("role");
                String studentUsn = rs.getString("student_usn");

                boolean authenticated;
                if (stored != null && stored.startsWith("pbkdf2$")) {
                    authenticated = com.ems.util.PasswordUtil.verify(password.toCharArray(), stored);
                } else {
                    // legacy plaintext — compare then migrate to hash
                    authenticated = stored != null && stored.equals(password);
                    if (authenticated) {
                        String newHash = com.ems.util.PasswordUtil.hash(password.toCharArray());
                        updatePasswordHash(connection, userId, newHash);
                    }
                }
                return authenticated ? new String[]{role, studentUsn, username} : null;
            }
        }
    }

    private void updatePasswordHash(Connection conn, int userId, String newHash) throws SQLException {
        String update = "UPDATE app_user SET password_hash = ? WHERE user_id = ?";
        try (PreparedStatement ups = conn.prepareStatement(update)) {
            ups.setString(1, newHash);
            ups.setInt(2, userId);
            ups.executeUpdate();
        }
    }
}

