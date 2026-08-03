package com.ems.service;

import com.ems.dao.AttendanceDAO;
import com.ems.dao.FacultyDutyDAO;

import java.util.List;

/**
 * Aggregates report data across modules for the Reports & Audit panel.
 */
public class ReportService {
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final FacultyDutyDAO dutyDAO = new FacultyDutyDAO();

    /** Absentee report: students who are marked absent for an exam */
    public List<String[]> absenteeReport(int examId) throws Exception {
        if (examId <= 0) throw new IllegalArgumentException("Exam ID must be positive");
        return attendanceDAO.fetchAttendanceReport(examId);
    }

    /** Malpractice summary: all malpractice incidents */
    public List<String[]> malpracticeSummary(int examId) throws Exception {
        return attendanceDAO.fetchMalpracticeForExam(examId);
    }

    /** Room-wise attendance: attendance grouped by room */
    public List<String[]> roomWiseAttendance(int examId) throws Exception {
        return attendanceDAO.fetchAttendanceReport(examId);
    }

    /** Faculty duty log for an exam */
    public List<String[]> facultyDutyLog() throws Exception {
        return dutyDAO.fetchAllDuties();
    }
}
