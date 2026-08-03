-- ==========================================================
-- Examination Management System - Full Workbench SQL Script
-- MySQL 8.0+
-- ==========================================================

DROP DATABASE IF EXISTS examination_management_system;
CREATE DATABASE examination_management_system;
USE examination_management_system;

-- -------------------------
-- 1) Core Master Tables
-- -------------------------

CREATE TABLE department (
    dept_id INT PRIMARY KEY,
    dept_name VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE room (
    room_no VARCHAR(20) PRIMARY KEY,
    block VARCHAR(50) NOT NULL,
    total_benches INT NOT NULL,
    CONSTRAINT chk_room_total_benches CHECK (total_benches > 0)
);

CREATE TABLE faculty (
    faculty_id INT PRIMARY KEY,
    faculty_name VARCHAR(120) NOT NULL,
    dept_id INT NOT NULL,
    workload INT NOT NULL DEFAULT 0,
    availability ENUM('AVAILABLE', 'UNAVAILABLE') NOT NULL DEFAULT 'AVAILABLE',
    email VARCHAR(150) UNIQUE,
    CONSTRAINT chk_faculty_workload CHECK (workload >= 0),
    CONSTRAINT fk_faculty_department FOREIGN KEY (dept_id) REFERENCES department(dept_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE app_user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'EXAM_CELL', 'VIEWER') NOT NULL,
    faculty_id INT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_app_user_faculty FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id)
        ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE student (
    usn VARCHAR(20) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    dept_id INT NOT NULL,
    semester INT NOT NULL,
    CONSTRAINT chk_student_semester CHECK (semester BETWEEN 1 AND 8),
    CONSTRAINT fk_student_department FOREIGN KEY (dept_id) REFERENCES department(dept_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE exam (
    exam_id INT PRIMARY KEY,
    exam_date DATE NOT NULL,
    subject_code VARCHAR(20) NULL,
    exam_type ENUM('INTERNAL', 'MIDTERM', 'END_SEM', 'SUPPLEMENTARY') NOT NULL
);

CREATE TABLE subject (
    subject_code VARCHAR(20) PRIMARY KEY,
    dept_id INT NOT NULL,
    exam_id INT NULL,
    subject_name VARCHAR(150) NOT NULL,
    semester INT NOT NULL,
    CONSTRAINT chk_subject_semester CHECK (semester BETWEEN 1 AND 8),
    CONSTRAINT fk_subject_department FOREIGN KEY (dept_id) REFERENCES department(dept_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_subject_exam FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
        ON UPDATE CASCADE ON DELETE SET NULL
);

ALTER TABLE exam
    ADD CONSTRAINT fk_exam_subject FOREIGN KEY (subject_code) REFERENCES subject(subject_code)
        ON UPDATE CASCADE ON DELETE RESTRICT;

CREATE TABLE bench (
    bench_no VARCHAR(20) PRIMARY KEY,
    room_no VARCHAR(20) NOT NULL,
    capacity INT NOT NULL,
    CONSTRAINT chk_bench_capacity CHECK (capacity > 0),
    CONSTRAINT fk_bench_room FOREIGN KEY (room_no) REFERENCES room(room_no)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE seating_allocation (
    seat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bench_no VARCHAR(20) NOT NULL,
    usn VARCHAR(20) NOT NULL,
    exam_id INT NOT NULL,
    seat_position INT NOT NULL,
    allocated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seat_bench FOREIGN KEY (bench_no) REFERENCES bench(bench_no)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_seat_student FOREIGN KEY (usn) REFERENCES student(usn)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_seat_exam FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uq_exam_student UNIQUE (exam_id, usn),
    CONSTRAINT uq_exam_bench_position UNIQUE (exam_id, bench_no, seat_position),
    CONSTRAINT chk_seat_position CHECK (seat_position > 0)
);

CREATE TABLE faculty_duty (
    duty_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    faculty_id INT NOT NULL,
    exam_id INT NOT NULL,
    room_no VARCHAR(20) NOT NULL,
    role ENUM('CHIEF_SUPERINTENDENT', 'INVIGILATOR', 'RELIEVER', 'SQUAD') NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_duty_faculty FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_duty_exam FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_duty_room FOREIGN KEY (room_no) REFERENCES room(room_no)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_faculty_exam UNIQUE (faculty_id, exam_id)
);

-- -------------------------
-- 2) Supporting Indexes
-- -------------------------

CREATE INDEX idx_student_dept ON student(dept_id);
CREATE INDEX idx_subject_dept_sem ON subject(dept_id, semester);
CREATE INDEX idx_exam_date ON exam(exam_date);
CREATE INDEX idx_bench_room ON bench(room_no);
CREATE INDEX idx_seat_exam ON seating_allocation(exam_id);
CREATE INDEX idx_duty_exam_role ON faculty_duty(exam_id, role);
CREATE INDEX idx_faculty_dept_avail ON faculty(dept_id, availability, workload);

-- -------------------------
-- 3) Trigger: Bench Capacity Guard
-- -------------------------

DELIMITER $$

CREATE TRIGGER trg_prevent_bench_overflow
BEFORE INSERT ON seating_allocation
FOR EACH ROW
BEGIN
    DECLARE v_capacity INT;
    DECLARE v_used INT;

    SELECT capacity INTO v_capacity
    FROM bench
    WHERE bench_no = NEW.bench_no;

    IF v_capacity IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid bench number for seating allocation';
    END IF;

    SELECT COUNT(*) INTO v_used
    FROM seating_allocation
    WHERE exam_id = NEW.exam_id
      AND bench_no = NEW.bench_no;

    IF v_used >= v_capacity THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Bench capacity exceeded for this exam';
    END IF;

    IF NEW.seat_position > v_capacity THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seat position exceeds bench capacity';
    END IF;
END$$

CREATE TRIGGER trg_prevent_bench_overflow_update
BEFORE UPDATE ON seating_allocation
FOR EACH ROW
BEGIN
    DECLARE v_capacity INT;
    DECLARE v_used INT;

    SELECT capacity INTO v_capacity
    FROM bench
    WHERE bench_no = NEW.bench_no;

    IF v_capacity IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid bench number for seating allocation';
    END IF;

    SELECT COUNT(*) INTO v_used
    FROM seating_allocation
    WHERE exam_id = NEW.exam_id
      AND bench_no = NEW.bench_no
      AND seat_id <> OLD.seat_id;

    IF v_used >= v_capacity THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Bench capacity exceeded for this exam';
    END IF;

    IF NEW.seat_position > v_capacity THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seat position exceeds bench capacity';
    END IF;
END$$

DELIMITER ;

-- -------------------------
-- 4) Stored Procedure: Auto Seating Allocation
-- -------------------------

DELIMITER $$

CREATE PROCEDURE sp_auto_allocate_seats(IN p_exam_id INT)
BEGIN
    DECLARE v_subject_code VARCHAR(20);
    DECLARE v_dept_id INT;
    DECLARE v_student_count INT;
    DECLARE v_capacity INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    IF EXISTS (SELECT 1 FROM seating_allocation WHERE exam_id = p_exam_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seats already allocated for this exam. Clear old allocation first.';
    END IF;

    SELECT subject_code INTO v_subject_code
    FROM exam
    WHERE exam_id = p_exam_id;

    IF v_subject_code IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Exam not found or subject is not mapped';
    END IF;

    SELECT dept_id INTO v_dept_id
    FROM subject
    WHERE subject_code = v_subject_code;

    IF v_dept_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Subject department mapping missing';
    END IF;

    SELECT COUNT(*) INTO v_student_count
    FROM student
    WHERE dept_id = v_dept_id;

    SELECT COALESCE(SUM(b.capacity), 0) INTO v_capacity
    FROM bench b
    JOIN room r ON r.room_no = b.room_no;

    IF v_student_count = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No students found for exam subject department';
    END IF;

    IF v_capacity < v_student_count THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient bench capacity for allocation';
    END IF;

    DROP TEMPORARY TABLE IF EXISTS tmp_students;
    CREATE TEMPORARY TABLE tmp_students AS
    SELECT usn,
           ROW_NUMBER() OVER (ORDER BY usn) AS rn
    FROM student
    WHERE dept_id = v_dept_id;

    DROP TEMPORARY TABLE IF EXISTS tmp_slots;
    CREATE TEMPORARY TABLE tmp_slots (
        bench_no VARCHAR(20) NOT NULL,
        seat_position INT NOT NULL,
        rn INT NOT NULL,
        PRIMARY KEY (rn)
    );

    SET @slot_rn = 0;

    INSERT INTO tmp_slots (bench_no, seat_position, rn)
    SELECT x.bench_no, x.seat_position, (@slot_rn := @slot_rn + 1) AS rn
    FROM (
        WITH RECURSIVE numbers(n) AS (
            SELECT 1
            UNION ALL
            SELECT n + 1 FROM numbers WHERE n < 20
        )
        SELECT b.bench_no,
               numbers.n AS seat_position
        FROM bench b
        JOIN numbers ON numbers.n <= b.capacity
        ORDER BY b.room_no, b.bench_no, numbers.n
    ) x;

    INSERT INTO seating_allocation (bench_no, usn, exam_id, seat_position)
    SELECT sl.bench_no,
           st.usn,
           p_exam_id,
           sl.seat_position
    FROM tmp_students st
    JOIN tmp_slots sl ON sl.rn = st.rn;

    COMMIT;

    SELECT CONCAT('Seat allocation completed for exam ', p_exam_id,
                  '. Students allocated: ', v_student_count) AS message;
END$$

DELIMITER ;

-- -------------------------
-- 5) Stored Procedure: Faculty Duty Auto-Assign (Hard Cap)
-- -------------------------

DELIMITER $$

CREATE PROCEDURE sp_assign_faculty_duties(
    IN p_exam_id INT,
    IN p_room_no VARCHAR(20),
    IN p_role VARCHAR(40),
    IN p_required_count INT
)
BEGIN
    DECLARE v_exam_date DATE;
    DECLARE v_assigned INT DEFAULT 0;
    DECLARE v_faculty_id INT;
    DECLARE done INT DEFAULT 0;

    DECLARE faculty_cursor CURSOR FOR
        SELECT f.faculty_id
        FROM faculty f
        WHERE f.availability = 'AVAILABLE'
          AND f.workload < 2
          AND NOT EXISTS (
              SELECT 1
              FROM faculty_duty fd
              JOIN exam e2 ON e2.exam_id = fd.exam_id
              WHERE fd.faculty_id = f.faculty_id
                AND e2.exam_date = v_exam_date
          )
        ORDER BY f.workload ASC, f.faculty_id ASC;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_required_count <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Required faculty count must be positive';
    END IF;

    SELECT exam_date INTO v_exam_date
    FROM exam
    WHERE exam_id = p_exam_id;

    IF v_exam_date IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Exam not found';
    END IF;

    START TRANSACTION;

    OPEN faculty_cursor;

    duty_loop: LOOP
        FETCH faculty_cursor INTO v_faculty_id;

        IF done = 1 OR v_assigned >= p_required_count THEN
            LEAVE duty_loop;
        END IF;

        INSERT INTO faculty_duty (faculty_id, exam_id, room_no, role)
        VALUES (v_faculty_id, p_exam_id, p_room_no, p_role);

        UPDATE faculty
        SET workload = workload + 1
        WHERE faculty_id = v_faculty_id;

        SET v_assigned = v_assigned + 1;
    END LOOP;

    CLOSE faculty_cursor;

    IF v_assigned < p_required_count THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Not enough available faculty under workload cap';
    END IF;

    COMMIT;

    SELECT CONCAT('Faculty duty assigned for exam ', p_exam_id,
                  '. Assigned count: ', v_assigned) AS message;
END$$

DELIMITER ;

-- -------------------------
-- 6) Sample Data
-- -------------------------

INSERT INTO department (dept_id, dept_name) VALUES
(1, 'Computer Science and Engineering'),
(2, 'Electronics and Communication Engineering'),
(3, 'Mechanical Engineering');

INSERT INTO room (room_no, block, total_benches) VALUES
('A101', 'Academic Block A', 10),
('A102', 'Academic Block A', 10),
('B201', 'Academic Block B', 8);

INSERT INTO bench (bench_no, room_no, capacity) VALUES
('A101-B1', 'A101', 2), ('A101-B2', 'A101', 2), ('A101-B3', 'A101', 2), ('A101-B4', 'A101', 2), ('A101-B5', 'A101', 2),
('A101-B6', 'A101', 2), ('A101-B7', 'A101', 2), ('A101-B8', 'A101', 2), ('A101-B9', 'A101', 2), ('A101-B10', 'A101', 2),
('A102-B1', 'A102', 2), ('A102-B2', 'A102', 2), ('A102-B3', 'A102', 2), ('A102-B4', 'A102', 2), ('A102-B5', 'A102', 2),
('A102-B6', 'A102', 2), ('A102-B7', 'A102', 2), ('A102-B8', 'A102', 2), ('A102-B9', 'A102', 2), ('A102-B10', 'A102', 2),
('B201-B1', 'B201', 3), ('B201-B2', 'B201', 3), ('B201-B3', 'B201', 3), ('B201-B4', 'B201', 3),
('B201-B5', 'B201', 3), ('B201-B6', 'B201', 3), ('B201-B7', 'B201', 3), ('B201-B8', 'B201', 3);

INSERT INTO faculty (faculty_id, faculty_name, dept_id, workload, availability, email) VALUES
(101, 'Dr. Arjun Rao', 1, 0, 'AVAILABLE', 'arjun.rao@college.edu'),
(102, 'Prof. Kavya Nair', 1, 1, 'AVAILABLE', 'kavya.nair@college.edu'),
(103, 'Dr. Meera Iyer', 2, 0, 'AVAILABLE', 'meera.iyer@college.edu'),
(104, 'Prof. Rohan Das', 2, 1, 'AVAILABLE', 'rohan.das@college.edu'),
(105, 'Dr. Nisha Verma', 3, 0, 'AVAILABLE', 'nisha.verma@college.edu'),
(106, 'Prof. Amit Kulkarni', 3, 0, 'UNAVAILABLE', 'amit.kulkarni@college.edu');

INSERT INTO app_user (username, password_hash, role, faculty_id, is_active) VALUES
('admin', 'admin123', 'ADMIN', NULL, 1),
('examcell', 'exam123', 'EXAM_CELL', 101, 1),
('viewer', 'view123', 'VIEWER', 103, 1);

INSERT INTO student (usn, name, email, dept_id, semester) VALUES
('1CS23CS001', 'Aarav Sharma', 'aarav.sharma@college.edu', 1, 6),
('1CS23CS002', 'Diya Menon', 'diya.menon@college.edu', 1, 6),
('1CS23CS003', 'Ishaan Reddy', 'ishaan.reddy@college.edu', 1, 6),
('1CS23CS004', 'Ananya Patel', 'ananya.patel@college.edu', 1, 6),
('1CS23CS005', 'Vivaan Gupta', 'vivaan.gupta@college.edu', 1, 6),
('1CS23CS006', 'Saanvi Rao', 'saanvi.rao@college.edu', 1, 6),
('1CS23CS007', 'Reyansh Jain', 'reyansh.jain@college.edu', 1, 6),
('1CS23CS008', 'Aadhya Singh', 'aadhya.singh@college.edu', 1, 6),
('1CS23CS009', 'Advik Nair', 'advik.nair@college.edu', 1, 6),
('1CS23CS010', 'Myra Das', 'myra.das@college.edu', 1, 6),
('1EC23EC001', 'Ritvik Anand', 'ritvik.anand@college.edu', 2, 4),
('1EC23EC002', 'Kiara Joseph', 'kiara.joseph@college.edu', 2, 4),
('1ME23ME001', 'Arnav Kulkarni', 'arnav.kulkarni@college.edu', 3, 4),
('1ME23ME002', 'Sara Fernandes', 'sara.fernandes@college.edu', 3, 4);

INSERT INTO exam (exam_id, exam_date, subject_code, exam_type) VALUES
(5001, '2026-06-10', NULL, 'END_SEM'),
(5002, '2026-06-11', NULL, 'END_SEM'),
(5003, '2026-06-12', NULL, 'INTERNAL');

INSERT INTO subject (subject_code, dept_id, exam_id, subject_name, semester) VALUES
('CS601', 1, 5001, 'Database Management Systems', 6),
('CS602', 1, 5002, 'Operating Systems', 6),
('EC401', 2, 5003, 'Signals and Systems', 4);

UPDATE exam SET subject_code = 'CS601' WHERE exam_id = 5001;
UPDATE exam SET subject_code = 'CS602' WHERE exam_id = 5002;
UPDATE exam SET subject_code = 'EC401' WHERE exam_id = 5003;

-- Example run commands (execute when required):
-- CALL sp_auto_allocate_seats(5001);
-- CALL sp_assign_faculty_duties(5001, 'A101', 'INVIGILATOR', 2);
