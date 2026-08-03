package com.ems.service;

import com.ems.dao.AuditLogDAO;
import com.ems.model.AuditLog;

import java.util.List;

public class AuditLogService {
    private final AuditLogDAO dao = new AuditLogDAO();

    public void log(String username, String action, String tableName,
                    String recordKey, String oldValue, String newValue) {
        try {
            dao.log(username, action, tableName, recordKey, oldValue, newValue);
        } catch (Exception e) {
            // audit must never block business operations
            com.ems.util.LoggerUtil.error("Audit log write failed: " + e.getMessage(), e);
        }
    }

    public List<AuditLog> all() throws Exception {
        return dao.fetchAll();
    }

    public List<AuditLog> byUser(String username) throws Exception {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username is required");
        return dao.fetchByUser(username.trim());
    }

    public void purgeOlderThan(int days) throws Exception {
        if (days <= 0) throw new IllegalArgumentException("Days must be positive");
        dao.purgeOlderThan(days);
    }
}
