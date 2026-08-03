# Normalization Report (1NF, 2NF, 3NF)

## 1NF Compliance
All tables satisfy 1NF because:
- Each table has a primary key.
- Every field contains atomic values only.
- No repeating groups exist in a row.

Examples:
- student stores one email per row, not comma-separated lists.
- seating_allocation stores one bench position per row.

## 2NF Compliance
2NF requires full dependency on whole candidate keys.

- Tables with single-column PK (department, student, subject, exam, room, bench, faculty, app_user) are automatically in 2NF.
- For relationship tables with additional unique constraints:
  - seating_allocation uses seat_id as PK, and attributes depend on allocation row identity.
  - faculty_duty uses duty_id as PK, and attributes depend on duty row identity.
- Composite uniqueness (exam_id, usn), (exam_id, bench_no, seat_position), and (faculty_id, exam_id) are integrity constraints, not partial dependency sources.

## 3NF Compliance
3NF requires no transitive dependency among non-key attributes.

- student: usn -> {name, email, dept_id, semester}; department name is not stored here, avoiding transitive dependency.
- subject: subject_code -> {dept_id, exam_id, subject_name, semester}; no department descriptive attributes duplicated.
- exam: exam_id -> {exam_date, subject_code, exam_type}; subject properties kept in subject.
- bench: bench_no -> {room_no, capacity}; room attributes (block, total_benches) remain in room.
- seating_allocation: seat_id -> {bench_no, usn, exam_id, seat_position}; student and exam details are not denormalized.
- faculty_duty: duty_id -> {faculty_id, exam_id, room_no, role}; faculty metadata remains in faculty.

## Conclusion
The schema is normalized up to 3NF with controlled denormalization avoided. Performance needs are handled using indexes and constraints instead of duplicated attributes.
