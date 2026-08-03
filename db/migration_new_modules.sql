-- ==========================================================
-- Examination Management System - New Modules Migration
-- MySQL 8.0+  |  Run AFTER full_workbench_script.sql
-- ==========================================================

USE examination_management_system;

-- -------------------------
-- A) Entry Control
-- -------------------------

CREATE TABLE IF NOT EXISTS entry_log (
    log_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    usn           VARCHAR(20) NOT NULL,
    exam_id       INT NOT NULL,
    scanned_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    entry_status  ENUM('ALLOWED','LATE','DENIED','IMPERSONATION') NOT NULL DEFAULT 'ALLOWED',
    remarks       VARCHAR(300),
    CONSTRAINT fk_el_student FOREIGN KEY (usn) REFERENCES student(usn) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_el_exam    FOREIGN KEY (exam_id) REFERENCES exam(exam_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uq_entry_usn_exam UNIQUE (usn, exam_id)
);

CREATE TABLE IF NOT EXISTS prohibited_items_log (
    item_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    usn           VARCHAR(20) NOT NULL,
    exam_id       INT NOT NULL,
    item_desc     VARCHAR(300) NOT NULL,
    confiscated   TINYINT(1) NOT NULL DEFAULT 0,
    logged_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pil_student FOREIGN KEY (usn) REFERENCES student(usn) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_pil_exam    FOREIGN KEY (exam_id) REFERENCES exam(exam_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- -------------------------
-- B) Attendance & Conduct
-- -------------------------

CREATE TABLE IF NOT EXISTS exam_attendance (
    attendance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usn           VARCHAR(20) NOT NULL,
    exam_id       INT NOT NULL,
    is_present    TINYINT(1) NOT NULL DEFAULT 1,
    marked_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_att_student FOREIGN KEY (usn) REFERENCES student(usn) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_att_exam    FOREIGN KEY (exam_id) REFERENCES exam(exam_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uq_att_usn_exam UNIQUE (usn, exam_id)
);

CREATE TABLE IF NOT EXISTS malpractice_log (
    mp_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    usn           VARCHAR(20) NOT NULL,
    exam_id       INT NOT NULL,
    room_no       VARCHAR(20) NOT NULL,
    incident_type VARCHAR(120) NOT NULL,
    description   VARCHAR(500),
    reported_by   INT NULL COMMENT 'faculty_id',
    reported_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mp_student  FOREIGN KEY (usn) REFERENCES student(usn) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_mp_exam     FOREIGN KEY (exam_id) REFERENCES exam(exam_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_mp_room     FOREIGN KEY (room_no) REFERENCES room(room_no) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_mp_faculty  FOREIGN KEY (reported_by) REFERENCES faculty(faculty_id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS washroom_log (
    wl_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    usn           VARCHAR(20) NOT NULL,
    exam_id       INT NOT NULL,
    exit_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    return_time   TIMESTAMP NULL,
    CONSTRAINT fk_wl_student  FOREIGN KEY (usn) REFERENCES student(usn) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_wl_exam     FOREIGN KEY (exam_id) REFERENCES exam(exam_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- -------------------------
-- C) Answer Sheet Handling
-- -------------------------

CREATE TABLE IF NOT EXISTS answer_sheet (
    sheet_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    usn           VARCHAR(20) NOT NULL,
    exam_id       INT NOT NULL,
    barcode       VARCHAR(60) UNIQUE,
    is_spare      TINYINT(1) NOT NULL DEFAULT 0,
    sealed        TINYINT(1) NOT NULL DEFAULT 0,
    bundle_no     INT NULL,
    room_no       VARCHAR(20) NOT NULL,
    invigilator_id INT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_as_student  FOREIGN KEY (usn) REFERENCES student(usn) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_as_exam     FOREIGN KEY (exam_id) REFERENCES exam(exam_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_as_room     FOREIGN KEY (room_no) REFERENCES room(room_no) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_as_invig    FOREIGN KEY (invigilator_id) REFERENCES faculty(faculty_id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT uq_as_usn_exam UNIQUE (usn, exam_id, is_spare)
);

-- -------------------------
-- D) Notifications
-- -------------------------

CREATE TABLE IF NOT EXISTS notification (
    notif_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    notif_type    ENUM('HALL_TICKET','TIMETABLE','ROOM_CHANGE','DUTY_REMINDER','MALPRACTICE') NOT NULL,
    title         VARCHAR(200) NOT NULL,
    body          TEXT NOT NULL,
    target_role   ENUM('ALL','ADMIN','EXAM_CELL','FACULTY','STUDENT') NOT NULL DEFAULT 'ALL',
    target_usn    VARCHAR(20) NULL COMMENT 'null = broadcast',
    is_sent       TINYINT(1) NOT NULL DEFAULT 0,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at       TIMESTAMP NULL,
    CONSTRAINT fk_notif_student FOREIGN KEY (target_usn) REFERENCES student(usn) ON UPDATE CASCADE ON DELETE SET NULL
);

-- -------------------------
-- E) Access & Security – Audit Log
-- -------------------------

CREATE TABLE IF NOT EXISTS audit_log (
    audit_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(60) NOT NULL,
    action        VARCHAR(200) NOT NULL,
    table_name    VARCHAR(60),
    record_key    VARCHAR(60),
    old_value     TEXT,
    new_value     TEXT,
    logged_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address    VARCHAR(60)
);

-- -------------------------
-- F) Indexes for new tables
-- -------------------------

CREATE INDEX idx_entry_exam   ON entry_log(exam_id);
CREATE INDEX idx_att_exam     ON exam_attendance(exam_id);
CREATE INDEX idx_mp_exam      ON malpractice_log(exam_id);
CREATE INDEX idx_notif_type   ON notification(notif_type, is_sent);
CREATE INDEX idx_audit_user   ON audit_log(username, logged_at);
