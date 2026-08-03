package com.ems.model;

import java.util.ArrayList;
import java.util.List;

public class BenchMap {
    private final String benchNo;
    private final int capacity;
    private final List<SeatDetail> seats = new ArrayList<>();

    public BenchMap(String benchNo, int capacity) {
        this.benchNo = benchNo;
        this.capacity = capacity;
    }

    public String getBenchNo() {
        return benchNo;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<SeatDetail> getSeats() {
        return seats;
    }

    public void addSeat(SeatDetail s) {
        seats.add(s);
    }

    public SeatDetail getSeatAt(int position) {
        for (SeatDetail s : seats) {
            if (s.getSeatPosition() == position) {
                return s;
            }
        }
        return null;
    }
}
