-- ============================================================
-- Student Role Migration
-- Run after migration_new_modules.sql
-- ============================================================
USE examination_management_system;

-- 1. Add STUDENT role to app_user
ALTER TABLE app_user
    MODIFY COLUMN role ENUM('ADMIN','EXAM_CELL','VIEWER','STUDENT') NOT NULL;

-- 2. Add student_usn link column
ALTER TABLE app_user
    ADD COLUMN student_usn VARCHAR(20) NULL AFTER faculty_id,
    ADD CONSTRAINT fk_user_student FOREIGN KEY (student_usn)
        REFERENCES student(usn) ON UPDATE CASCADE ON DELETE SET NULL;

-- 3. Auto-create app_user accounts for every existing student
--    Default password = USN (plaintext, will be hashed on first login)
INSERT IGNORE INTO app_user (username, password_hash, role, student_usn, is_active)
SELECT usn, usn, 'STUDENT', usn, 1 FROM student;
