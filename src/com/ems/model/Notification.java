package com.ems.model;

import java.time.LocalDateTime;

public class Notification {
    private long notifId;
    private String notifType;
    private String title;
    private String body;
    private String targetRole;
    private String targetUsn;
    private boolean sent;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    public Notification(long notifId, String notifType, String title, String body,
                        String targetRole, String targetUsn, boolean sent,
                        LocalDateTime createdAt, LocalDateTime sentAt) {
        this.notifId = notifId;
        this.notifType = notifType;
        this.title = title;
        this.body = body;
        this.targetRole = targetRole;
        this.targetUsn = targetUsn;
        this.sent = sent;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public long getNotifId() { return notifId; }
    public String getNotifType() { return notifType; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getTargetRole() { return targetRole; }
    public String getTargetUsn() { return targetUsn; }
    public boolean isSent() { return sent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getSentAt() { return sentAt; }
}
