package com.ems.service;

import com.ems.dao.NotificationDAO;
import com.ems.dao.ExamDAO;
import com.ems.dao.StudentDAO;
import com.ems.model.HallTicketEntry;
import com.ems.model.Notification;
import com.ems.model.Student;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregates data for the Student Portal — notifications, hall ticket, exam schedule.
 */
public class StudentPortalService {
    private final NotificationDAO notifDAO = new NotificationDAO();
    private final ExamDAO examDAO = new ExamDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    /** Get student profile. */
    public Student getStudent(String usn) throws Exception {
        return studentDAO.getStudentByUsn(usn);
    }

    /**
     * Notifications relevant to this student:
     *   - target_usn = student's USN  (personal)
     *   - target_role = 'ALL' or 'STUDENT' (broadcast)
     */
    public List<Notification> getNotificationsForStudent(String usn) throws Exception {
        return notifDAO.fetchAll().stream()
                .filter(n -> {
                    boolean personal = usn.equals(n.getTargetUsn());
                    boolean broadcast = n.getTargetUsn() == null
                            && ("ALL".equals(n.getTargetRole()) || "STUDENT".equals(n.getTargetRole()));
                    return personal || broadcast;
                })
                .collect(Collectors.toList());
    }

    /** Hall ticket entries for this student (exam + seat allocation). */
    public List<HallTicketEntry> getHallTicket(String usn) throws Exception {
        return examDAO.getExamsForStudent(usn);
    }
}
