package com.ems.dao;

import com.ems.config.DBConnection;
import com.ems.model.Exam;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {
    public void addExam(Exam exam) throws SQLException {
        String sql = "INSERT INTO exam(exam_id, exam_date, subject_code, session_slot, exam_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exam.getExamId());
            ps.setDate(2, Date.valueOf(exam.getExamDate()));
            ps.setString(3, exam.getSubjectCode());
            ps.setString(4, exam.getSessionSlot() != null ? exam.getSessionSlot() : "FN");
            ps.setString(5, exam.getExamType());
            ps.executeUpdate();
        }
    }

    public void updateExam(Exam exam) throws SQLException {
        String sql = "UPDATE exam SET exam_date = ?, subject_code = ?, session_slot = ?, exam_type = ? WHERE exam_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(exam.getExamDate()));
            ps.setString(2, exam.getSubjectCode());
            ps.setString(3, exam.getSessionSlot() != null ? exam.getSessionSlot() : "FN");
            ps.setString(4, exam.getExamType());
            ps.setInt(5, exam.getExamId());
            ps.executeUpdate();
        }
    }

    public void deleteExam(int examId) throws SQLException {
        String sql = "DELETE FROM exam WHERE exam_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.executeUpdate();
        }
    }

    public List<Exam> getAllExams() throws SQLException {
        String sql = "SELECT exam_id, exam_date, subject_code, COALESCE(session_slot, 'FN') AS session_slot, exam_type FROM exam ORDER BY exam_date, exam_id";
        List<Exam> exams = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                exams.add(new Exam(
                        rs.getInt("exam_id"),
                        rs.getDate("exam_date").toLocalDate(),
                        rs.getString("subject_code"),
                        rs.getString("session_slot"),
                        rs.getString("exam_type")
                ));
            }
        }
        return exams;
    }

    public List<com.ems.model.HallTicketEntry> getExamsForStudent(String usn) throws SQLException {
        String sql = "SELECT e.exam_id, e.exam_date, e.subject_code, s.subject_name, e.exam_type, sa.bench_no, sa.seat_position, b.room_no " +
                "FROM student st " +
                "JOIN subject s ON s.dept_id = st.dept_id AND s.semester = st.semester " +
                "JOIN exam e ON e.subject_code = s.subject_code " +
                "LEFT JOIN seating_allocation sa ON sa.exam_id = e.exam_id AND sa.usn = st.usn " +
                "LEFT JOIN bench b ON sa.bench_no = b.bench_no " +
                "WHERE st.usn = ? ORDER BY e.exam_date";

        List<com.ems.model.HallTicketEntry> list = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer seatPos = rs.getObject("seat_position") == null ? null : rs.getInt("seat_position");
                    list.add(new com.ems.model.HallTicketEntry(
                            rs.getInt("exam_id"),
                            rs.getDate("exam_date").toLocalDate(),
                            rs.getString("subject_code"),
                            rs.getString("subject_name"),
                            rs.getString("exam_type"),
                            rs.getString("room_no"),
                            rs.getString("bench_no"),
                            seatPos
                    ));
                }
            }
        }
        return list;
    }
}
