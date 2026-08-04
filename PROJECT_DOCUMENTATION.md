# Examination Management System (EMS) — Technical & User Documentation

This document explains the Examination Management System (EMS) implemented in this workspace, its architecture, database design, key components, and step-by-step instructions to run and demonstrate the application.

---

## 1) Quick Summary
- **Purpose**: A comprehensive college examination administration system supporting student & subject management, exam scheduling, room & bench layout setup, automated seating allocation (with department clash prevention), smart faculty invigilator duty scheduling, bulk attendance marking, visual workload analytics, and an integrated **Examination Assistant** AI Copilot.
- **Technology Stack**: Java 11+ (Swing UI), JDBC, MySQL (Aiven Cloud / Local Workbench script with triggers & stored procedures).
- **Architecture**: UI Layer (Swing Panels) → Service Layer → DAO Layer → MySQL Database.

---

## 2) Repository Layout & Key Source Files

```
Examination Management System/
├── db/                                  # Database SQL Scripts
│   ├── full_workbench_script.sql        # Core tables & schema
│   ├── migration_new_modules.sql        # Attendance & security modules
│   └── migration_ps6_smart_allocation.sql # PS-6 Department constraints & slots
├── dist/                                # Executable Distribution
│   └── ExaminationManagementSystem.jar  # Standalone Fat JAR (bundled MySQL driver)
├── lib/                                 # Third-party Libraries
│   └── mysql-connector-j-9.7.0.jar      # MySQL JDBC Driver
├── resources/                           # Configuration Files
│   └── database.properties              # Cloud JDBC Connection Settings
├── src/com/ems/                         # Java Source Code
│   ├── config/                          # DBConfig & DBConnection (ClassLoader + Lazy Load)
│   ├── dao/                             # AllocationDAO, FacultyDAO, FacultyDutyDAO, StudentDAO, etc.
│   ├── model/                           # Entity Models (Faculty, Student, ExamAttendance, etc.)
│   ├── service/                         # Business Services (FacultyDutyService, AttendanceService, etc.)
│   │   └── ai/                          # OpenRouterClient.java (Examination Assistant AI)
│   ├── ui/                              # DashboardFrame.java & LoginFrame.java
│   │   └── panels/                      # UI Panels (FacultyDutyPanel, FacultyPanel, AttendanceConductPanel, AiCopilotPanel, etc.)
│   └── util/                            # AnimationEngine, UiUtil, AppTheme, ToastManager
├── PROJECT_DOCUMENTATION.md             # Complete Technical Documentation
└── README.md                            # Quick Start Guide
```

---

## 3) Key Modules & Features

### A. 👤 Faculty Duty Assignment (Auto-Assign by Exam ID)
- **Exam ID Assignment**: Allows assigning faculty by specifying an `Exam ID` (e.g. `5001`). The system queries all rooms allotted to that exam and automatically assigns invigilators for every room in one action.
- **Department Conflict Prevention**: Ensures faculty are **never assigned to invigilate exams of their own department**.
- **Download Assignments CSV**: Export faculty duty schedules to a local `.csv` file for distribution or printing.

### B. ✅ Bulk Attendance & Conduct Marking
- **Exam ID Bulk Load**: Entering an `Exam ID` loads all allocated students into an interactive checkbox table.
- **One-Click Actions**:
  - **✅ Mark All Present**: Sets all student checkboxes to Present with one click.
  - **❌ Mark All Absent**: Sets all student checkboxes to Absent with one click.
  - **💾 Save Attendance for All**: Persists attendance for all loaded students into `exam_attendance` in a single click.
- **High-Contrast Dark Theme Readability**: Explicit row renderers ensure high-contrast text visibility:
  - *Not Marked*: White text on dark slate surface (`#1E293B`).
  - *Present*: Mint green text (`#A7F3D0`) on dark emerald background (`#064E3B`).
  - *Absent*: Light red text (`#FECACA`) on dark rose background (`#450A0A`).

### C. 📋 Visual Faculty Workload & Status Management
- **Visual Progress Bar Renderer**: Renders color-coded workload progress bars for each faculty row:
  - 🟢 `0 / 2 (Light Workload)`
  - 🟡 `1 / 2 (Moderate Workload)`
  - 🔴 `2 / 2 (Max Cap)`
- **Live KPI Header**: Displays real-time counts for Total Faculty, Light Workload (0), Moderate (1), and Max Cap (2+).
- **⚡ Update Availability**: One-click update button to change availability status (`AVAILABLE` / `UNAVAILABLE`) by Faculty ID or table row selection.

### D. 🤖 Examination Assistant AI Copilot
- **OpenRouter Free API**: Built using standard Java `HttpClient` connecting to free AI models (Meta Llama 3.3 70B, DeepSeek R1, Nemotron).
- **Context-Aware Assistance**: Pre-configured quick action chips for drafting malpractice reports, invigilator policy guidance, washroom rules, and hall ticket checks.

---

## 4) Running the Application

### Option 1: Run Pre-Built Executable Standalone Fat JAR (Recommended)

Run directly from terminal or double-click `dist/ExaminationManagementSystem.jar`:

```powershell
java -jar dist/ExaminationManagementSystem.jar
```

*(The JAR contains all class files, properties, and the bundled MySQL driver, requiring zero external classpath configuration).*

---

### Option 2: Recompile & Package from Source

```powershell
# 1. Compile Java sources
Get-ChildItem -Recurse -Filter *.java src | Resolve-Path -Relative | Set-Content sources.txt
cmd /c 'javac -d bin -cp "lib/*" @sources.txt'

# 2. Package Fat JAR with MySQL Driver
if (Test-Path build_fat) { Remove-Item -Recurse -Force build_fat }
New-Item -ItemType Directory -Path build_fat | Out-Null
Set-Location build_fat
& 'C:\Program Files\Java\jdk-23\bin\jar.exe' xf '../lib/mysql-connector-j-9.7.0.jar'
Set-Location '..'
Copy-Item -Recurse -Force bin\* build_fat\
Copy-Item -Recurse -Force resources\* build_fat\
& 'C:\Program Files\Java\jdk-23\bin\jar.exe' --create --file dist/ExaminationManagementSystem.jar --main-class com.ems.App -C build_fat .

# 3. Launch application
java -jar dist/ExaminationManagementSystem.jar
```

---

## 5) Default Credentials

| Role | Username / USN | Password | Description |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Full access to all modules and Examination Assistant AI |
| **Exam Cell** | `examcell` | `exam123` | Seating allocation, faculty duty assignment & attendance |
| **Viewer** | `viewer` | `view123` | Read-only view |
| **Faculty** | `f101` | `faculty123` | Faculty duty roster & attendance |
| **Student** | `1CS23CS001` | `1CS23CS001` | Student Portal, Seat Details & Printable Hall Ticket |

---

## 6) Step-by-Step Instructor Demo Script

1. **Launch Application**: Execute `java -jar dist/ExaminationManagementSystem.jar`. Notice the animated splash screen and smooth login frame.
2. **Login as Admin**: Enter `admin` / `admin123`. The Dashboard opens in full-screen window mode.
3. **Faculty Duty Allocation**:
   - Navigate to **👤 Faculty assignment**.
   - Enter `Exam ID` `5001`. Click **⚡ Auto Assign Faculty**. Notice all rooms allotted to that exam are assigned invigilators automatically.
   - Click **📥 Download Assignments CSV** to save the schedule.
4. **Faculty Management & Visual Workload**:
   - Navigate to **📋 Faculty management**.
   - Observe the top KPI summary cards showing Light, Moderate, and Max Cap faculty workload.
   - Look at the **Workload Visual** column showing progress bars (`0/2`, `1/2`, `2/2`).
   - Select a faculty row (e.g. `106 - Prof. Amit Kulkarni`), change Availability dropdown to `AVAILABLE`/`UNAVAILABLE`, and click **⚡ Update Availability**.
5. **Bulk Attendance Marking**:
   - Navigate to **✅ Attendance & conduct**.
   - Enter Exam ID `5001` and click **📋 Load Students**.
   - Click **✅ Mark All Present** or **❌ Mark All Absent**, toggle individual checkboxes, then click **💾 Save Attendance for All**.
6. **Examination Assistant AI Copilot**:
   - Navigate to **🤖 Examination Assistant**.
   - Click any quick action chip (e.g., *"Draft Malpractice Report"*) to ask the AI assistant for instant guidance.