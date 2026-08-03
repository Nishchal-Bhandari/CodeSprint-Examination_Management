# ER Diagram Explanation - Examination Management System

## Entities and Relationships

1. Department -> Student
- Relationship: One-to-Many
- Meaning: One department has many students, each student belongs to one department.
- Keys: department.dept_id (PK) -> student.dept_id (FK)

2. Department -> Subject
- Relationship: One-to-Many
- Meaning: One department offers multiple subjects.
- Keys: department.dept_id (PK) -> subject.dept_id (FK)

3. Department -> Faculty
- Relationship: One-to-Many
- Meaning: A department has many faculty members.
- Keys: department.dept_id (PK) -> faculty.dept_id (FK)

4. Subject -> Exam
- Relationship: One-to-Many (logical), represented by exam.subject_code
- Meaning: A subject can have multiple exam events over time.
- Keys: subject.subject_code (PK) -> exam.subject_code (FK)

5. Exam -> Seating Allocation
- Relationship: One-to-Many
- Meaning: One exam has many allocated seats.
- Keys: exam.exam_id (PK) -> seating_allocation.exam_id (FK)

6. Student -> Seating Allocation
- Relationship: One-to-Many over all exams; One-to-One per exam via unique constraint
- Meaning: A student can appear in multiple exams, but only once per exam.
- Keys: student.usn (PK) -> seating_allocation.usn (FK), unique (exam_id, usn)

7. Room -> Bench
- Relationship: One-to-Many
- Meaning: One room contains multiple benches.
- Keys: room.room_no (PK) -> bench.room_no (FK)

8. Bench -> Seating Allocation
- Relationship: One-to-Many over all exams; constrained by capacity per exam
- Meaning: Bench hosts seat positions for exam candidates.
- Keys: bench.bench_no (PK) -> seating_allocation.bench_no (FK), unique (exam_id, bench_no, seat_position)

9. Faculty -> Faculty Duty
- Relationship: One-to-Many over all exams; One duty per faculty per exam via unique constraint
- Meaning: Faculty can be assigned duties across exams with workload cap.
- Keys: faculty.faculty_id (PK) -> faculty_duty.faculty_id (FK), unique (faculty_id, exam_id)

10. Exam -> Faculty Duty
- Relationship: One-to-Many
- Meaning: Multiple faculty can be assigned to the same exam.
- Keys: exam.exam_id (PK) -> faculty_duty.exam_id (FK)

11. Room -> Faculty Duty
- Relationship: One-to-Many
- Meaning: Faculty duty is assigned per exam room.
- Keys: room.room_no (PK) -> faculty_duty.room_no (FK)

12. App User -> Faculty (optional)
- Relationship: Optional Many-to-One
- Meaning: Login users may be linked to a faculty member.
- Keys: faculty.faculty_id (PK) -> app_user.faculty_id (FK)

## ER Structure (Textual)
Department -> {Student, Subject, Faculty}
Subject -> Exam
Room -> {Bench, FacultyDuty}
Exam -> {SeatingAllocation, FacultyDuty}
Student -> SeatingAllocation
Bench -> SeatingAllocation
Faculty -> FacultyDuty
AppUser -> Faculty (optional)
