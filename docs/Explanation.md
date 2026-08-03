# Examination Management System - Project Explanation

## Problem Solved
Colleges face operational issues in exam planning, room readiness, seating, and invigilation scheduling. This project automates these workflows using a relational database-driven architecture.

## Key Features
- Student CRUD management
- Exam scheduling and maintenance
- Room and bench infrastructure management
- Automated seating allocation through stored procedure
- Faculty duty assignment with workload and availability constraints
- Trigger-based capacity safety at insertion time

## DBMS Concepts Used
- Relational schema modeling with 10 interrelated tables
- Primary keys, foreign keys, unique and check constraints
- Transactions and rollback in stored procedures
- Trigger for integrity guardrail
- Indexed query paths for reporting and dashboard reads
- Normalization up to 3NF

## Architecture
- Frontend: Java Swing
- Backend: Java JDBC, DAO + Service layers
- Database: MySQL 8 (Workbench-ready script)

## Why This Is Realistic
- Uses deterministic and auditable seat assignment
- Handles workload balancing for faculty duties
- Supports real constraints and failure handling
- Includes practical query reports for exam cell operations
