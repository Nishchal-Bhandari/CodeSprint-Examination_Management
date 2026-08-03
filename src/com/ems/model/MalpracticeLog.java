package com.ems.model;

import java.time.LocalDateTime;

public class MalpracticeLog {
    private long mpId;
    private String usn;
    private int examId;
    private String roomNo;
    private String incidentType;
    private String description;
    private Integer reportedBy;
    private LocalDateTime reportedAt;

    public MalpracticeLog(long mpId, String usn, int examId, String roomNo,
                          String incidentType, String description,
                          Integer reportedBy, LocalDateTime reportedAt) {
        this.mpId = mpId;
        this.usn = usn;
        this.examId = examId;
        this.roomNo = roomNo;
        this.incidentType = incidentType;
        this.description = description;
        this.reportedBy = reportedBy;
        this.reportedAt = reportedAt;
    }

    public long getMpId() { return mpId; }
    public String getUsn() { return usn; }
    public int getExamId() { return examId; }
    public String getRoomNo() { return roomNo; }
    public String getIncidentType() { return incidentType; }
    public String getDescription() { return description; }
    public Integer getReportedBy() { return reportedBy; }
    public LocalDateTime getReportedAt() { return reportedAt; }
}
