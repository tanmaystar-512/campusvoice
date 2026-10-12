# Module 3: Database & Security Specialist
**Lead Engineer:** Twinkle  
**Enrollment No:** 24E1ARADF40P165 | **Roll No:** 24EARAD165  
**Email:** kashyaptwinkle206@gmail.com | **Mobile:** 8295950385  
**Assigned Role:** Database & Security Specialist  

---

## 1. Module Overview & Core Ownership
Module 3 is the persistent foundation and cryptographic fortress of **CampusVoice**. It is responsible for:
1. **Relational MySQL 8.0 Schema Design:** Normalized 3NF entity tables, relationship constraints, cascade rules, and indexing strategy.
2. **Entity-Relationship (ER) Modeling:** Decoupled data model with strict isolation between `users` and `issues`.
3. **High-Performance JDBC Connection Pool:** Thread-safe singleton connection manager (`DBConnection.java`) with connection reuse and resource release.
4. **Data Access Object (DAO) Layer:** Strict parameter binding via `PreparedStatement` across all database operations, eliminating SQL Injection vulnerabilities.
5. **System Audit Trail & Security Assurance:** Immutable audit logging tables (`status_history`, `audit_trail`), salted SHA-256 password hashing (`PasswordUtil.java`), and automated security test suite (`DatabaseSecurityTestSuite.java`).

---

## 2. Directory Structure & Key Artifacts
```
Module-3_Database_Security/
├── sql/
│   └── schema.sql                        # Complete MySQL DDL & Seed Dataset script
├── schema.sql                            # Root mirror for 1-click import
├── db.properties                         # Connection pool configuration
├── DatabaseSecurityTestSuite.java        # Security assertion test suite
├── src/
│   ├── dao/                              # Data Access Object Layer
│   │   ├── UserDAO.java
│   │   ├── ClassDAO.java
│   │   ├── IssueDAO.java
│   │   ├── VoteDAO.java
│   │   ├── EvidenceDAO.java
│   │   └── StatusHistoryDAO.java
│   └── util/
│       ├── DBConnection.java             # Thread-safe JDBC Connection Manager
│       └── PasswordUtil.java             # Salted SHA-256 hashing utility
└── README.md                             # Schema dictionary and security proofs
```

---

## 3. Database Schema & Tables Dictionary

| Table Name | Purpose | Identity Decoupling Mechanism |
|---|---|---|
| `users` | Student & Admin authentication profiles | Holds credentials; strictly **isolated** from complaints |
| `classes` | Class cohorts, semesters, batch strength | Used to calculate quorum participation ($N_{\text{class}}$) |
| `issues` | Anonymous grievance registry | Contains `class_id` only; **NO** `student_id` or `user_id` foreign key |
| `issue_votes` | Class peer verification ballots | Stores `voter_token_hash` (`UNIQUE(issue_id, voter_token_hash)`) |
| `issue_evidence` | Image/document attachments | Indexed by `issue_id` |
| `status_history` | Timestamped lifecycle state transitions | Logs transition dates, changed statuses, and admin remarks |

---

## 4. Database Setup & Initialization
```bash
# 1. Start MySQL Server
# 2. Execute the initialization script:
mysql -u root -p < schema.sql
```
Credentials and connection pools can be adjusted in `db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/campusvoice?useSSL=false&serverTimezone=UTC
db.user=root
db.password=root
db.pool.size=20
```

---

## 5. Security Validation
Run the database security verification test:
```powershell
javac -cp "lib/*" DatabaseSecurityTestSuite.java
java -cp ".;lib/*" campusvoice.DatabaseSecurityTestSuite
```
Validates SQL injection resistance, cryptographic hash consistency, and zero identity leakage.
