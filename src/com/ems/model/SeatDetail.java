package com.ems.model;

public class SeatDetail {
    private final int seatPosition;
    private final String usn;
    private final String studentName;

    public SeatDetail(int seatPosition, String usn, String studentName) {
        this.seatPosition = seatPosition;
        this.usn = usn;
        this.studentName = studentName;
    }

    public int getSeatPosition() {
        return seatPosition;
    }

    public String getUsn() {
        return usn;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getName() {
        return studentName != null ? studentName : "";
    }
}
