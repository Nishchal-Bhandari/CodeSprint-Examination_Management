package com.ems.dao;

import com.ems.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomBenchDAO {
    public void addRoom(String roomNo, String block, int totalBenches) throws SQLException {
        String sql = "INSERT INTO room(room_no, block, total_benches) VALUES(?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roomNo);
            ps.setString(2, block);
            ps.setInt(3, totalBenches);
            ps.executeUpdate();
        }
    }

    public void addBench(String benchNo, String roomNo, int capacity) throws SQLException {
        String sql = "INSERT INTO bench(bench_no, room_no, capacity) VALUES(?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, benchNo);
            ps.setString(2, roomNo);
            ps.setInt(3, capacity);
            ps.executeUpdate();
        }
    }

    public void deleteBench(String benchNo) throws SQLException {
        String deleteSeats = "DELETE FROM seating_allocation WHERE bench_no = ?";
        String deleteBench = "DELETE FROM bench WHERE bench_no = ?";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement ps = connection.prepareStatement(deleteSeats)) {
                    ps.setString(1, benchNo);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement(deleteBench)) {
                    ps.setString(1, benchNo);
                    ps.executeUpdate();
                }
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void deleteRoom(String roomNo) throws SQLException {
        String selectDutyFaculty = "SELECT faculty_id FROM faculty_duty WHERE room_no = ?";
        String deleteDuties = "DELETE FROM faculty_duty WHERE room_no = ?";
        String deleteSeats = "DELETE sa FROM seating_allocation sa JOIN bench b ON b.bench_no = sa.bench_no WHERE b.room_no = ?";
        String deleteBenches = "DELETE FROM bench WHERE room_no = ?";
        String deleteRoom = "DELETE FROM room WHERE room_no = ?";
        String updateWorkload = "UPDATE faculty SET workload = GREATEST(workload - 1, 0) WHERE faculty_id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement ps = connection.prepareStatement(selectDutyFaculty)) {
                    ps.setString(1, roomNo);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            try (PreparedStatement updatePs = connection.prepareStatement(updateWorkload)) {
                                updatePs.setInt(1, rs.getInt("faculty_id"));
                                updatePs.executeUpdate();
                            }
                        }
                    }
                }
                try (PreparedStatement ps = connection.prepareStatement(deleteDuties)) {
                    ps.setString(1, roomNo);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement(deleteSeats)) {
                    ps.setString(1, roomNo);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement(deleteBenches)) {
                    ps.setString(1, roomNo);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement(deleteRoom)) {
                    ps.setString(1, roomNo);
                    ps.executeUpdate();
                }
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<String[]> getRoomBenchInventory() throws SQLException {
        String sql = "SELECT r.room_no, r.block, r.total_benches, COUNT(b.bench_no) benches, COALESCE(SUM(b.capacity), 0) seats " +
                "FROM room r LEFT JOIN bench b ON r.room_no = b.room_no " +
                "GROUP BY r.room_no, r.block, r.total_benches ORDER BY r.room_no";
        List<String[]> rows = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{
                        rs.getString("room_no"),
                        rs.getString("block"),
                        String.valueOf(rs.getInt("total_benches")),
                        String.valueOf(rs.getInt("benches")),
                        String.valueOf(rs.getInt("seats"))
                });
            }
        }
        return rows;
    }
}
