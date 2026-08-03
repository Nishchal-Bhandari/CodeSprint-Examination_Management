# Examination Management System (EMS) — PS-6 Smart Exam Classroom Allocation

> **Production-Quality DBMS & Desktop Automation System** for college examination operations, candidate seating allocation, and smart invigilator scheduling.

---

## 🌟 Key Features (PS-6 Compliant)

### 1. Smart Invigilator Allocation (PS-6 Engine)
- **Department Constraint Protection**: Enforces that faculty members are **never assigned to invigilate exams of their own department** (eliminates bias & conflict of interest).
- **Workload Balancing**: Automatically tracks and balances duty counts across available faculty.
- **Session Schedule Clash Prevention**: Supports **Forenoon (`FN`)** and **Afternoon (`AN`)** slots, preventing double-booking of faculty or rooms across exam schedules.

### 2. Interactive 2D Classroom Seating Map
- **Visual Room Layout**: Renders interactive bench cards with capacity badges and seat boxes.
- **Color-Coded Status**:
  - 🟩 **Occupied Seat** (Displays Student USN and Name tooltip)
  - ⬜ **Vacant Seat**
  - 🟨 **Selected Swap Target**
- **Click-to-Swap**: Examination Coordinators can click any seat on the visual map to swap candidate seating live.

### 3. Coordinator Manual Modification Suite
- **Manual Seat Swap & Reassign**: Override auto-allocation and swap seat positions between any two candidates.
- **Manual Invigilator Swap & Assignment**: Override auto-assignment and assign standby faculty or swap room invigilators with 1 click.

### 4. Cloud Database Connectivity (Aiven Cloud MySQL)
- Pre-configured out-of-the-box with **Aiven Cloud MySQL**.
- Multi-user live synchronization across laptops and network devices without local MySQL installation.

---

## 🚀 Quick Start (How to Run)

### Method 1: Double-Click or Run Standalone Executable (Recommended)

Simply double-click `dist/ExaminationManagementSystem.jar` or run in terminal:

```powershell
java -jar dist/ExaminationManagementSystem.jar
```

---

### Method 2: Compile & Build from Source

#### **PowerShell:**
```powershell
# 1. Compile Java source code
javac -cp "lib/mysql-connector-j-9.7.0.jar;resources" -d out (Get-ChildItem -Recurse -Filter *.java src).FullName

# 2. Package Executable JAR
powershell -Command "if (Test-Path dist) { Remove-Item -Recurse -Force dist }; New-Item -ItemType Directory -Path dist/build | Out-Null; Push-Location dist/build; & 'C:\Program Files\Java\jdk-23\bin\jar.exe' xf '../../lib/mysql-connector-j-9.7.0.jar'; Pop-Location; Copy-Item -Recurse -Force 'out/*' 'dist/build/'; Copy-Item -Recurse -Force 'resources/*' 'dist/build/'; Set-Content -Path 'dist/MANIFEST.MF' -Value 'Main-Class: com.ems.App'; & 'C:\Program Files\Java\jdk-23\bin\jar.exe' cfm 'dist/ExaminationManagementSystem.jar' 'dist/MANIFEST.MF' -C 'dist/build' ."

# 3. Run application
java -jar dist/ExaminationManagementSystem.jar
```

---

## 🔑 Default Login Credentials

| Role | Username | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Full System Control & Audit Security Logs |
| **Exam Cell** | `examcell` | `exam123` | Smart Invigilator Allocation, 2D Seating Map & Manual Swaps |
| **Viewer** | `viewer` | `view123` | Read-only Reports & Room Schedules |
| **Faculty** | `f101` | `faculty123` | Assigned Duty Roster & Attendance Marking |
| **Student** | `1CS23CS001` | `1CS23CS001` | Student Portal, Seat Details & Hall Ticket Download |

---

## 📁 Project Structure

```
Examination Management System/
├── db/                                  # Database SQL Scripts
│   ├── full_workbench_script.sql        # Core tables & schema
│   ├── migration_new_modules.sql        # Attendance, answer sheets & security modules
│   ├── migration_student_role.sql       # Student role migration
│   ├── migration_faculty_role.sql       # Faculty role migration
│   ├── migration_ps6_smart_allocation.sql # PS-6 Department constraints & slots
│   └── fix_primary_key_procedure.sql    # Primary key temp table procedure patch
├── dist/                                # Executable Distribution
│   └── ExaminationManagementSystem.jar  # Pre-built standalone executable
├── lib/                                 # Third-party Libraries
│   └── mysql-connector-j-9.7.0.jar      # MySQL JDBC Driver
├── resources/                           # Configuration Files
│   └── database.properties              # Cloud JDBC Connection Settings
├── src/com/ems/                         # Java Source Code
│   ├── config/                          # Database connection handling
│   ├── dao/                             # Data Access Objects
│   ├── model/                           # Entity Models
│   ├── service/                         # Business Logic Services
│   ├── ui/                              # Swing UI Frames & Visual Components
│   └── util/                            # Theme, Logger & UI Utilities
├── .gitignore                           # Git ignore rules
├── PROJECT_DOCUMENTATION.md             # Complete Technical Documentation
└── README.md                            # Project Overview & Setup Guide
```

---

## 🛠 Database Configuration (`resources/database.properties`)

The project is pre-configured to connect to Aiven Cloud MySQL:

```properties
db.url=jdbc:mysql://examination-examination.c.aivencloud.com:18314/examination_management_system?useSSL=true&trustServerCertificate=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=avnadmin
db.password=<YOUR_AIVEN_PASSWORD>
db.driver=com.mysql.cj.jdbc.Driver
```
