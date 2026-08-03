package com.ems.service;

import com.ems.dao.NotificationDAO;
import com.ems.model.Notification;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationService {
    private static final Set<String> VALID_TYPES = new HashSet<>(
            Arrays.asList("HALL_TICKET", "TIMETABLE", "ROOM_CHANGE", "DUTY_REMINDER", "MALPRACTICE"));
    private static final Set<String> VALID_ROLES = new HashSet<>(
            Arrays.asList("ALL", "ADMIN", "EXAM_CELL", "FACULTY", "STUDENT"));

    private final NotificationDAO dao = new NotificationDAO();

    public long create(String type, String title, String body, String targetRole, String targetUsn) throws Exception {
        if (type == null || !VALID_TYPES.contains(type.trim().toUpperCase()))
            throw new IllegalArgumentException("Invalid notification type");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        if (body == null || body.isBlank()) throw new IllegalArgumentException("Body is required");
        String role = targetRole == null ? "ALL" : targetRole.trim().toUpperCase();
        if (!VALID_ROLES.contains(role)) throw new IllegalArgumentException("Invalid target role");
        return dao.createNotification(type.trim().toUpperCase(), title.trim(), body.trim(),
                role, targetUsn == null || targetUsn.isBlank() ? null : targetUsn.trim());
    }

    public void markSent(long notifId) throws Exception {
        if (notifId <= 0) throw new IllegalArgumentException("Notification ID must be positive");
        dao.markAsSent(notifId);
    }

    public List<Notification> all() throws Exception {
        return dao.fetchAll();
    }

    public void delete(long notifId) throws Exception {
        if (notifId <= 0) throw new IllegalArgumentException("Notification ID must be positive");
        dao.deleteNotification(notifId);
    }
}
