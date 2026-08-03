# System Logic

## 1) Automated Seating Allocation Algorithm
Procedure: sp_auto_allocate_seats(p_exam_id)

Inputs:
- Exam ID

Steps:
1. Validate exam and subject mapping.
2. Resolve department from subject.
3. Load all students of that department (deterministic order by USN).
4. Build ordered seat slots from all benches using seat positions 1..capacity.
5. Validate capacity: total slots >= total students.
6. Map nth student to nth slot.
7. Insert allocations transactionally.

Rules Enforced:
- Duplicate student allocation per exam is blocked by unique (exam_id, usn).
- Duplicate seat position per bench per exam is blocked by unique (exam_id, bench_no, seat_position).
- Bench overflow is blocked by trigger.

Complexity:
- O(S + P), where S is students and P is generated positions.
- Deterministic mapping ensures predictable demo output.

## 2) Faculty Assignment Algorithm (Hard Cap)
Procedure: sp_assign_faculty_duties(exam, room, role, required_count)

Inputs:
- Exam ID, room number, role, required faculty count

Steps:
1. Validate exam existence and required_count > 0.
2. Build candidate faculty list where:
   - availability = AVAILABLE
   - workload < 2 (hard cap)
   - no duty on the same exam date
3. Sort by workload ascending (fair load distribution).
4. Assign in cursor loop until required_count is reached.
5. Increment workload after each assignment.
6. Roll back if candidates are insufficient.

Rules Enforced:
- Hard cap prevents overload.
- Unique (faculty_id, exam_id) prevents duplicate assignment in one exam.
- Transaction ensures all-or-nothing consistency.

## 3) Trigger Logic
Trigger: trg_prevent_bench_overflow (+ update variant)

Checks:
- Bench exists.
- Existing allocations count for exam+bench is less than bench capacity.
- seat_position does not exceed bench capacity.

On violation:
- SIGNAL SQLSTATE 45000 with explicit message.
