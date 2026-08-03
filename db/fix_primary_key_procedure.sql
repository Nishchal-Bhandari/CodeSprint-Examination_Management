-- ============================================================
-- Fix: sp_auto_allocate_seats to support Primary Keys on Temp Tables
-- Fixes 'sql_require_primary_key' error on Aiven Cloud MySQL
-- ============================================================
USE examination_management_system;

DROP PROCEDURE IF EXISTS sp_auto_allocate_seats;

DELIMITER $$

CREATE PROCEDURE sp_auto_allocate_seats(
    IN p_exam_id INT
)
BEGIN
    DECLARE v_exam_date DATE;
    DECLARE v_subject_code VARCHAR(20);
    DECLARE v_dept_id INT;
    DECLARE v_student_count INT DEFAULT 0;
    DECLARE v_capacity INT DEFAULT 0;
    DECLARE done INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_exam_id <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Exam ID';
    END IF;

    SELECT exam_date, subject_code
    INTO v_exam_date, v_subject_code
    FROM exam
    WHERE exam_id = p_exam_id;

    IF v_exam_date IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Exam ID not found';
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

    START TRANSACTION;

    DELETE FROM seating_allocation WHERE exam_id = p_exam_id;

    -- Temp Table 1: tmp_students with PRIMARY KEY
    DROP TEMPORARY TABLE IF EXISTS tmp_students;
    CREATE TEMPORARY TABLE tmp_students (
        usn VARCHAR(20) NOT NULL,
        rn INT NOT NULL,
        PRIMARY KEY (rn)
    );

    INSERT INTO tmp_students (usn, rn)
    SELECT usn, ROW_NUMBER() OVER (ORDER BY usn) AS rn
    FROM student
    WHERE dept_id = v_dept_id;

    -- Temp Table 2: tmp_slots with PRIMARY KEY
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
