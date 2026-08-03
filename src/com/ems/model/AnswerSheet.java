package com.ems.model;

import java.time.LocalDateTime;

public class AnswerSheet {
    private long sheetId;
    private String usn;
    private int examId;
    private String barcode;
    private boolean spare;
    private boolean sealed;
    private Integer bundleNo;
    private String roomNo;
    private Integer invigilatorId;
    private LocalDateTime createdAt;

    public AnswerSheet(long sheetId, String usn, int examId, String barcode,
                       boolean spare, boolean sealed, Integer bundleNo, String roomNo,
                       Integer invigilatorId, LocalDateTime createdAt) {
        this.sheetId = sheetId;
        this.usn = usn;
        this.examId = examId;
        this.barcode = barcode;
        this.spare = spare;
        this.sealed = sealed;
        this.bundleNo = bundleNo;
        this.roomNo = roomNo;
        this.invigilatorId = invigilatorId;
        this.createdAt = createdAt;
    }

    public long getSheetId() { return sheetId; }
    public String getUsn() { return usn; }
    public int getExamId() { return examId; }
    public String getBarcode() { return barcode; }
    public boolean isSpare() { return spare; }
    public boolean isSealed() { return sealed; }
    public Integer getBundleNo() { return bundleNo; }
    public String getRoomNo() { return roomNo; }
    public Integer getInvigilatorId() { return invigilatorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
