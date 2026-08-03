package com.ems.service;

import com.ems.dao.UserDAO;

/**
 * Handles authentication and session audit logging.
 * login() returns String[2]: {role, studentUsn} — studentUsn is null for non-student roles.
 */
public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final AuditLogService auditService = new AuditLogService();

    /**
     * @return String[2] {role, studentUsn}  — studentUsn null for admin/exam-cell/viewer
     * @throws Exception on bad credentials
     */
    public String[] login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password are required");
        }
        String[] result;
        try {
            result = userDAO.authenticate(username.trim(), password.trim());
        } catch (Exception e) {
            auditService.log(username.trim(), "LOGIN_FAILED", "app_user", username.trim(), null, "DB error");
            throw e;
        }
        if (result == null) {
            auditService.log(username.trim(), "LOGIN_FAILED", "app_user", username.trim(), null, "Bad credentials");
            throw new IllegalArgumentException("Invalid credentials or inactive user");
        }
        String role = result[0];
        auditService.log(username.trim(), "LOGIN_SUCCESS", "app_user", username.trim(), null, role);
        return new String[]{role, result[1]}; // {role, studentUsn}
    }

    /** Records logout in audit trail. */
    public void logout(String username) {
        auditService.log(username, "LOGOUT", "app_user", username, null, null);
    }
}
