package com.ems.model;

import java.time.LocalDateTime;

public class AuditLog {
    private long auditId;
    private String username;
    private String action;
    private String tableName;
    private String recordKey;
    private String oldValue;
    private String newValue;
    private LocalDateTime loggedAt;
    private String ipAddress;

    public AuditLog(long auditId, String username, String action, String tableName,
                    String recordKey, String oldValue, String newValue,
                    LocalDateTime loggedAt, String ipAddress) {
        this.auditId = auditId;
        this.username = username;
        this.action = action;
        this.tableName = tableName;
        this.recordKey = recordKey;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.loggedAt = loggedAt;
        this.ipAddress = ipAddress;
    }

    public long getAuditId() { return auditId; }
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getTableName() { return tableName; }
    public String getRecordKey() { return recordKey; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public String getIpAddress() { return ipAddress; }
}
