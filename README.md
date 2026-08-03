# Examination Management System (EMS) — PS-6 Smart Exam Classroom Allocation & AI Copilot

> **Production-Grade DBMS, Desktop Automation System, & AI Copilot** for college examination operations, candidate seating allocation, smart invigilator scheduling, and interactive AI assistance.

---

## 🌟 Key Features (PS-6 & AI Enhanced)

### 1. 🤖 Conversational EMS AI Copilot (OpenRouter Free API Integration)
- **Zero-Dependency AI Client**: Standard Java 11+ `HttpClient` integrated directly into the Swing dashboard without heavyweight external libraries.
- **Powered by OpenRouter Free Models**: Connects to `openrouter/free` (Meta Llama 3.3 70B / DeepSeek R1 / Nvidia Nemotron) for zero-cost AI assistance.
- **Quick Action Chips**: One-click actions for drafting formal academic malpractice reports, reviewing invigilator duty constraints, washroom exit policies, and hall ticket eligibility.
- **Rich Chat UI**: Custom-styled speech bubbles, typing indicators, and markdown formatting.

### 2. ⚡ Industry-Grade Animated UI/UX Framework
- **Animation Engine (`AnimationEngine`)**: Built-in cubic easing, spring physics, component fade-in/out, directional slide-in, horizontal error shake, and pulse glow.
- **Animated Components**: Dynamic gradient buttons with hover shimmer, border glow effects, and custom rounded cards.
- **Non-Blocking Toast Notifications (`ToastManager`)**: Replaced intrusive popups with smooth slide-in toasts (Success, Error, Info, Warning) from the top-right corner.
- **Interactive Visual Seating Map**: 2D bench grid with capacity badges, seat hover glow, and live click-to-swap target selection.
- **Animated KPI Counters**: Real-time count-up number animations on data load for candidate and room metrics.
- **Animated Splash Screen**: Startup splash window with logo scale-in, typewriter header, progress bar, and fade-out transition.

### 3. 🛡️ Smart Invigilator Allocation (PS-6 Engine)
- **Department Constraint Protection**: Enforces that faculty members are **never assigned to invigilate exams of their own department** (eliminates bias & conflict of interest).
- **Workload Balancing**: Automatically tracks and balances duty counts across available faculty.
- **Session Schedule Clash Prevention**: Supports **Forenoon (`FN`)** and **Afternoon (`AN`)** slots, preventing double-booking of faculty or rooms across exam schedules.

### 4. 🎛️ Comprehensive Coordinator & Student Suite
- **Manual Seat & Duty Swaps**: Live override tools for coordinators to reassign seats or swap invigilators.
- **Student Portal**: Student login for viewing seat allocations, notifications, and generating printable Hall Tickets.
- **Cloud Database Connectivity**: Pre-configured with **Aiven Cloud MySQL** for instant multi-user synchronization.

---

## 🚀 Quick Start (How to Run)

### Method 1: Double-Click or Run Standalone Executable (Recommended)

Simply double-click `dist/ExaminationManagementSystem.jar` or run in terminal:

```powershell
java -jar dist/ExaminationManagementSystem.jar
```

---

### Method 2: Compile & Package from Source

#### **PowerShell:**
```powershell
# 1. Compile Java source files
javac -cp "lib/mysql-connector-j-9.7.0.jar;resources" -d out (Get-ChildItem -Recurse -Filter *.java src).FullName

# 2. Package Executable JAR
if (Test-Path resources) { Copy-Item -Path resources\* -Destination out -Recurse -Force }
Set-Content -Path 'out/MANIFEST.MF' -Value "Manifest-Version: 1.0`nMain-Class: com.ems.App`nClass-Path: lib/mysql-connector-j-9.7.0.jar ../lib/mysql-connector-j-9.7.0.jar`n"
& 'C:\Program Files\Java\jdk-23\bin\jar.exe' cfm dist/ExaminationManagementSystem.jar out/MANIFEST.MF -C out .

# 3. Run application
java -jar dist/ExaminationManagementSystem.jar
```

---

## 🔑 Default Login Credentials

| Role | Username / USN | Password | Access Level & Features |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Full Control, AI Copilot, Audit Trail & Security Logs |
| **Exam Cell** | `examcell` | `exam123` | Smart Invigilator Allocation, 2D Seating Map & Swaps |
| **Viewer** | `viewer` | `view123` | Read-only Reports & Room Schedules |
| **Faculty** | `f101` | `faculty123` | Assigned Duty Roster & Attendance Marking |
| **Student** (CS) | `1CS23CS001` | `1CS23CS001` | Student Portal, Seat Details & Printable Hall Ticket |
| **Student** (EC) | `1EC23EC001` | `1EC23EC001` | Student Portal, Seat Details & Printable Hall Ticket |
| **Student** (ME) | `1ME23ME001` | `1ME23ME001` | Student Portal, Seat Details & Printable Hall Ticket |

*(Note: For students, the initial password is the same as their USN).*

---

## 📁 Project Structure

```
Examination Management System/
├── db/                                  # Database SQL Scripts
│   ├── full_workbench_script.sql        # Core tables & schema
│   ├── migration_new_modules.sql        # Attendance, answer sheets & security modules
│   ├── migration_student_role.sql       # Student role migration
│   ├── migration_faculty_role.sql       # Faculty role migration
│   └── migration_ps6_smart_allocation.sql # PS-6 Department constraints & slots
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
│   │   └── ai/                          # OpenRouter AI Client (OpenRouterClient.java)
│   ├── ui/                              # Swing UI Frames & Visual Components
│   │   └── panels/                      # UI Panels (AiCopilotPanel, VisualSeatingPanel, etc.)
│   └── util/                            # AnimationEngine, ToastManager, AppTheme & UiUtil
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
