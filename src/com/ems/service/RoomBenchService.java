package com.ems.service;

import com.ems.dao.RoomBenchDAO;

import java.util.List;

public class RoomBenchService {
    private final RoomBenchDAO dao = new RoomBenchDAO();

    public void addRoom(String roomNo, String block, int totalBenches) throws Exception {
        if (roomNo == null || roomNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number is required");
        }
        if (block == null || block.trim().isEmpty()) {
            throw new IllegalArgumentException("Block is required");
        }
        if (totalBenches <= 0) {
            throw new IllegalArgumentException("Total benches must be positive");
        }
        dao.addRoom(roomNo.trim(), block.trim(), totalBenches);
    }

    public void addBench(String benchNo, String roomNo, int capacity) throws Exception {
        if (benchNo == null || benchNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Bench number is required");
        }
        if (roomNo == null || roomNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number is required");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Bench capacity must be positive");
        }
        dao.addBench(benchNo.trim(), roomNo.trim(), capacity);
    }

    public List<String[]> inventory() throws Exception {
        return dao.getRoomBenchInventory();
    }

    public void deleteBench(String benchNo) throws Exception {
        if (benchNo == null || benchNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Bench number is required");
        }
        dao.deleteBench(benchNo.trim());
    }

    public void deleteRoom(String roomNo) throws Exception {
        if (roomNo == null || roomNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number is required");
        }
        dao.deleteRoom(roomNo.trim());
    }
}
