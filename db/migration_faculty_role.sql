USE examination_management_system;

ALTER TABLE app_user
    MODIFY COLUMN role ENUM('ADMIN','EXAM_CELL','VIEWER','STUDENT','FACULTY') NOT NULL;

INSERT IGNORE INTO app_user (username, password_hash, role, faculty_id, is_active)
SELECT CONCAT('f', faculty_id), 'faculty123', 'FACULTY', faculty_id, 1
FROM faculty;
