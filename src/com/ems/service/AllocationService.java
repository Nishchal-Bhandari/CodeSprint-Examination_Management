package com.ems.service;

import com.ems.dao.AllocationDAO;
import com.ems.model.BenchMap;
import java.util.List;

public class AllocationService {
    private final AllocationDAO dao = new AllocationDAO();

    public String autoAllocate(int examId) throws Exception {
        if (examId <= 0) {
            throw new IllegalArgumentException("Exam ID must be positive");
        }
        return dao.autoAllocateSeats(examId);
    }

    public boolean swapSeats(long seatId1, long seatId2) throws Exception {
        if (seatId1 <= 0 || seatId2 <= 0) {
            throw new IllegalArgumentException("Valid seat IDs required for swapping");
        }
        return dao.manualSwapSeats(seatId1, seatId2);
    }

    public List<String[]> forExam(int examId) throws Exception {
        if (examId <= 0) {
            throw new IllegalArgumentException("Exam ID must be positive");
        }
        return dao.fetchAllocationForExam(examId);
    }

    public int clearExam(int examId) throws Exception {
        if (examId <= 0) {
            throw new IllegalArgumentException("Exam ID must be positive");
        }
        return dao.deleteAllocationsForExam(examId);
    }

    public List<BenchMap> forExamRoom(int examId, String roomNo) throws Exception {
        if (examId <= 0) throw new IllegalArgumentException("Exam ID must be positive");
        if (roomNo == null || roomNo.trim().isEmpty()) throw new IllegalArgumentException("Room No required");
        return dao.fetchBenchMapForExamRoom(examId, roomNo.trim());
    }
}
