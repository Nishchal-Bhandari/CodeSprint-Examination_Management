-- ============================================================
-- Migration: PS-6 Smart Allocation Enhancements
-- Adds session slots & Department Constraints to Faculty Assignment
-- ============================================================
USE examination_management_system;

-- 1. Add session_slot to exam table if it doesn't exist
SET @dbname = DATABASE();
SET @tablename = 'exam';
SET @columnname = 'session_slot';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      TABLE_SCHEMA = @dbname
      AND TABLE_NAME = @tablename
      AND COLUMN_NAME = @columnname
  ) > 0,
  'SELECT 1',
  'ALTER TABLE exam ADD COLUMN session_slot ENUM(''FN'', ''AN'') NOT NULL DEFAULT ''FN'' AFTER subject_code'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 2. Update sp_assign_faculty_duties procedure with Department Constraints & Slot Clash check
DROP PROCEDURE IF EXISTS sp_assign_faculty_duties;

DELIMITER $$

CREATE PROCEDURE sp_assign_faculty_duties(
    IN p_exam_id INT,
    IN p_room_no VARCHAR(20),
    IN p_role VARCHAR(40),
    IN p_required_count INT
)
BEGIN
    DECLARE v_exam_date DATE;
    DECLARE v_session_slot VARCHAR(10);
    DECLARE v_exam_dept_id INT;
    DECLARE v_assigned INT DEFAULT 0;
    DECLARE v_faculty_id INT;
    DECLARE done INT DEFAULT 0;

    -- Fetch exam details: date, session slot, and subject department
    SELECT e.exam_date, COALESCE(e.session_slot, 'FN'), s.dept_id
    INTO v_exam_date, v_session_slot, v_exam_dept_id
    FROM exam e
    LEFT JOIN subject s ON s.subject_code = e.subject_code
    WHERE e.exam_id = p_exam_id;

    IF v_exam_date IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Exam not found';
    END IF;

    -- Cursor to select available faculty:
    -- 1. Must be AVAILABLE
    -- 2. Workload < 2
    -- 3. DEPARTMENT CONSTRAINT: Faculty department MUST NOT match exam subject department (avoid conflict of interest)
    -- 4. SCHEDULE CLASH: Faculty MUST NOT have another duty on same exam_date and session_slot
    BEGIN
        DECLARE faculty_cursor CURSOR FOR
            SELECT f.faculty_id
            FROM faculty f
            WHERE f.availability = 'AVAILABLE'
              AND f.workload < 2
              AND (v_exam_dept_id IS NULL OR f.dept_id <> v_exam_dept_id)
              AND NOT EXISTS (
                  SELECT 1
                  FROM faculty_duty fd
                  JOIN exam e2 ON e2.exam_id = fd.exam_id
                  WHERE fd.faculty_id = f.faculty_id
                    AND e2.exam_date = v_exam_date
                    AND COALESCE(e2.session_slot, 'FN') = v_session_slot
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
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Not enough available cross-department faculty under workload cap';
        END IF;

        COMMIT;

        SELECT CONCAT('Smart Invigilator assignment completed for exam ', p_exam_id,
                      '. Assigned count: ', v_assigned, ' (Department constraint enforced)') AS message;
    END;
END$$

DELIMITER ;
