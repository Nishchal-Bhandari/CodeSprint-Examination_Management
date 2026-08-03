# Demo Flow (Step-by-Step)

1. Run SQL setup
- Execute db/full_workbench_script.sql in MySQL Workbench.

2. Login
- Launch Java app.
- Use sample credentials:
  - admin / admin123
  - examcell / exam123

3. Student Management
- Open Student Management panel.
- Add a new student (USN, Name, Email, Dept ID, Semester).
- Update one record.
- Delete one record.
- Refresh and verify table state.

4. Exam Scheduling
- Open Exam Scheduling panel.
- Create an exam linked to an existing subject code.
- Update exam date/type.
- Refresh and verify.

5. Room and Bench Setup
- Open Room & Bench panel.
- Add a room.
- Add benches with capacities for that room.
- Refresh inventory and verify room-level seats.

6. Automated Seating Allocation
- Open Seat Allocation panel.
- Enter exam ID (example: 5001).
- Click Run Auto Allocation.
- Display generated seat mappings and verify deterministic ordering.

7. Faculty Duty Assignment
- Open Faculty Assignment panel.
- Enter exam ID, room number, role, required count.
- Click Auto Assign Faculty.
- Verify assigned duties and hard-cap behavior.

8. Query Demonstration
- Execute db/demo_queries.sql.
- Show joins, aggregates, filters, and ordered reports.

9. Trigger Proof
- Attempt manual insert exceeding bench capacity.
- Verify trigger error message.
