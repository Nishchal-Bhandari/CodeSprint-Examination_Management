package com.ems.model;

import java.time.LocalDateTime;

public class ExamAttendance {
    private long attendanceId;
    private String usn;
    private int examId;
    private boolean present;
    private LocalDateTime markedAt;

    public ExamAttendance(long attendanceId, String usn, int examId, boolean present, LocalDateTime markedAt) {
        this.attendanceId = attendanceId;
        this.usn = usn;
        this.examId = examId;
        this.present = present;
        this.markedAt = markedAt;
    }

    public long getAttendanceId() { return attendanceId; }
    public String getUsn() { return usn; }
    public int getExamId() { return examId; }
    public boolean isPresent() { return present; }
    public LocalDateTime getMarkedAt() { return markedAt; }
}
