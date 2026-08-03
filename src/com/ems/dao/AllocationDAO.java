package com.ems.dao;

import com.ems.config.DBConnection;
import com.ems.model.BenchMap;
import com.ems.model.SeatDetail;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AllocationDAO {
    public String autoAllocateSeats(int examId) throws SQLException {
        String call = "{CALL sp_auto_allocate_seats(?)}";
        try (Connection connection = DBConnection.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, examId);
            boolean hasResult = cs.execute();
            if (hasResult) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next()) {
                        return rs.getString("message");
                    }
                }
            }
            return "Seat allocation procedure executed";
        }
    }

    public boolean manualSwapSeats(long seatId1, long seatId2) throws SQLException {
        String fetchSql = "SELECT seat_id, usn FROM seating_allocation WHERE seat_id IN (?, ?)";
        String updateSql = "UPDATE seating_allocation SET usn = ? WHERE seat_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String usn1 = null, usn2 = null;
                try (PreparedStatement ps = conn.prepareStatement(fetchSql)) {
                    ps.setLong(1, seatId1);
                    ps.setLong(2, seatId2);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            long id = rs.getLong("seat_id");
                            if (id == seatId1) usn1 = rs.getString("usn");
                            if (id == seatId2) usn2 = rs.getString("usn");
                        }
                    }
                }

                if (usn1 == null || usn2 == null) return false;

                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, usn2);
                    ps.setLong(2, seatId1);
                    ps.executeUpdate();

                    ps.setString(1, usn1);
                    ps.setLong(2, seatId2);
                    ps.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<BenchMap> fetchBenchMapForExamRoom(int examId, String roomNo) throws SQLException {
        String sql = "SELECT b.bench_no, b.capacity, sa.seat_id, sa.seat_position, sa.usn, st.name " +
                "FROM bench b LEFT JOIN seating_allocation sa ON b.bench_no = sa.bench_no AND sa.exam_id = ? " +
                "LEFT JOIN student st ON st.usn = sa.usn " +
                "WHERE b.room_no = ? ORDER BY b.bench_no, sa.seat_position";
        List<BenchMap> benches = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setString(2, roomNo);
            try (ResultSet rs = ps.executeQuery()) {
                BenchMap current = null;
                String lastBench = null;
                while (rs.next()) {
                    String benchNo = rs.getString("bench_no");
                    int capacity = rs.getInt("capacity");
                    Integer pos = rs.getObject("seat_position") != null ? rs.getInt("seat_position") : null;
                    String usn = rs.getString("usn");
                    String name = rs.getString("name");

                    if (lastBench == null || !lastBench.equals(benchNo)) {
                        current = new BenchMap(benchNo, capacity);
                        benches.add(current);
                        lastBench = benchNo;
                    }

                    if (pos != null) {
                        current.addSeat(new SeatDetail(pos, usn, name));
                    }
                }
            }
        }
        return benches;
    }

    public List<String[]> fetchAllocationForExam(int examId) throws SQLException {
        String sql = "SELECT sa.seat_id, sa.usn, st.name, sa.bench_no, sa.seat_position, b.room_no " +
                "FROM seating_allocation sa JOIN student st ON st.usn = sa.usn " +
                "LEFT JOIN bench b ON b.bench_no = sa.bench_no " +
                "WHERE sa.exam_id = ? ORDER BY b.room_no, sa.bench_no, sa.seat_position";
        List<String[]> rows = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new String[]{
                            String.valueOf(rs.getLong("seat_id")),
                            rs.getString("usn"),
                            rs.getString("name"),
                            rs.getString("room_no") != null ? rs.getString("room_no") : "N/A",
                            rs.getString("bench_no"),
                            String.valueOf(rs.getInt("seat_position"))
                    });
                }
            }
        }
        return rows;
    }

    public int deleteAllocationsForExam(int examId) throws SQLException {
        String sql = "DELETE FROM seating_allocation WHERE exam_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, examId);
            return ps.executeUpdate();
        }
    }
}
