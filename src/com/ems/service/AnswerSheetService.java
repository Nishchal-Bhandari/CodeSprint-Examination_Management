package com.ems.service;

import com.ems.dao.AnswerSheetDAO;

import java.util.List;

public class AnswerSheetService {
    private final AnswerSheetDAO dao = new AnswerSheetDAO();

    public void addSheet(String usn, int examId, String barcode, boolean spare,
                         int bundleNo, String roomNo, Integer invigilatorId) throws Exception {
        if (usn == null || usn.isBlank()) throw new IllegalArgumentException("USN is required");
        if (roomNo == null || roomNo.isBlank()) throw new IllegalArgumentException("Room No is required");
        if (bundleNo <= 0) throw new IllegalArgumentException("Bundle No must be positive");
        dao.addSheet(usn.trim(), examId, barcode, spare, bundleNo, roomNo.trim(), invigilatorId);
    }

    public void sealBundle(int examId, String roomNo, int bundleNo) throws Exception {
        if (examId <= 0) throw new IllegalArgumentException("Exam ID must be positive");
        if (roomNo == null || roomNo.isBlank()) throw new IllegalArgumentException("Room No is required");
        dao.sealBundle(examId, roomNo.trim(), bundleNo);
    }

    public List<String[]> sheetsForExam(int examId) throws Exception {
        if (examId <= 0) throw new IllegalArgumentException("Exam ID must be positive");
        return dao.fetchSheetsForExam(examId);
    }

    public List<String[]> bundleSummary(int examId) throws Exception {
        if (examId <= 0) throw new IllegalArgumentException("Exam ID must be positive");
        return dao.bundleSummaryForExam(examId);
    }

    public void deleteSheet(long sheetId) throws Exception {
        if (sheetId <= 0) throw new IllegalArgumentException("Sheet ID must be positive");
        dao.deleteSheet(sheetId);
    }
}
