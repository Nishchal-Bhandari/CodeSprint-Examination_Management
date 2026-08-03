USE examination_management_system;

-- 1) Students from CSE department (SELECT with condition)
SELECT s.usn, s.name, s.email
FROM student s
JOIN department d ON d.dept_id = s.dept_id
WHERE d.dept_name = 'Computer Science and Engineering'
ORDER BY s.usn;

-- 2) Upcoming exams by date (ORDER BY)
SELECT e.exam_id, e.exam_date, e.exam_type, e.subject_code
FROM exam e
WHERE e.exam_date >= CURDATE()
ORDER BY e.exam_date, e.exam_id;

-- 3) Exam and subject details (JOIN)
SELECT e.exam_id, e.exam_date, e.exam_type, su.subject_code, su.subject_name, d.dept_name
FROM exam e
JOIN subject su ON su.subject_code = e.subject_code
JOIN department d ON d.dept_id = su.dept_id
ORDER BY e.exam_date;

-- 4) Room bench inventory (JOIN + aggregate)
SELECT r.room_no, r.block, r.total_benches, COUNT(b.bench_no) AS benches_created, SUM(b.capacity) AS seats_available
FROM room r
LEFT JOIN bench b ON b.room_no = r.room_no
GROUP BY r.room_no, r.block, r.total_benches
ORDER BY r.room_no;

-- 5) Allocated students per exam (GROUP BY)
SELECT e.exam_id, su.subject_name, COUNT(sa.seat_id) AS allocated_students
FROM exam e
JOIN subject su ON su.subject_code = e.subject_code
LEFT JOIN seating_allocation sa ON sa.exam_id = e.exam_id
GROUP BY e.exam_id, su.subject_name
ORDER BY e.exam_id;

-- 6) Seat allocation listing with full details (multi JOIN)
SELECT sa.exam_id, su.subject_name, sa.usn, st.name AS student_name,
       sa.bench_no, sa.seat_position, b.room_no
FROM seating_allocation sa
JOIN student st ON st.usn = sa.usn
JOIN exam e ON e.exam_id = sa.exam_id
JOIN subject su ON su.subject_code = e.subject_code
JOIN bench b ON b.bench_no = sa.bench_no
ORDER BY sa.exam_id, b.room_no, sa.bench_no, sa.seat_position;

-- 7) Bench utilization by exam (GROUP BY + condition)
SELECT sa.exam_id, sa.bench_no, COUNT(*) AS occupied,
       MAX(b.capacity) AS capacity,
       ROUND((COUNT(*) / MAX(b.capacity)) * 100, 2) AS utilization_percent
FROM seating_allocation sa
JOIN bench b ON b.bench_no = sa.bench_no
GROUP BY sa.exam_id, sa.bench_no
HAVING occupied > 0
ORDER BY sa.exam_id, sa.bench_no;

-- 8) Faculty duty report (JOIN + ORDER BY)
SELECT fd.duty_id, fd.exam_id, su.subject_name, f.faculty_name,
       fd.room_no, fd.role, e.exam_date
FROM faculty_duty fd
JOIN faculty f ON f.faculty_id = fd.faculty_id
JOIN exam e ON e.exam_id = fd.exam_id
JOIN subject su ON su.subject_code = e.subject_code
ORDER BY e.exam_date, fd.room_no, fd.role;

-- 9) Faculty workload summary (GROUP BY)
SELECT f.faculty_id, f.faculty_name, f.workload, f.availability,
       COUNT(fd.duty_id) AS assigned_duties
FROM faculty f
LEFT JOIN faculty_duty fd ON fd.faculty_id = f.faculty_id
GROUP BY f.faculty_id, f.faculty_name, f.workload, f.availability
ORDER BY assigned_duties DESC, f.workload DESC;

-- 10) Unallocated students for an exam
SELECT st.usn, st.name
FROM student st
JOIN exam e ON e.exam_id = 5001
JOIN subject su ON su.subject_code = e.subject_code AND su.dept_id = st.dept_id
LEFT JOIN seating_allocation sa ON sa.exam_id = e.exam_id AND sa.usn = st.usn
WHERE sa.seat_id IS NULL
ORDER BY st.usn;

-- 11) Department-wise student count (GROUP BY)
SELECT d.dept_name, COUNT(s.usn) AS student_count
FROM department d
LEFT JOIN student s ON s.dept_id = d.dept_id
GROUP BY d.dept_id, d.dept_name
ORDER BY student_count DESC;

-- 12) Exams with no faculty assigned yet
SELECT e.exam_id, e.exam_date, su.subject_name
FROM exam e
JOIN subject su ON su.subject_code = e.subject_code
LEFT JOIN faculty_duty fd ON fd.exam_id = e.exam_id
WHERE fd.duty_id IS NULL
ORDER BY e.exam_date;
