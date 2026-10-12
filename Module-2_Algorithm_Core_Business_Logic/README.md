# Module 2: Algorithm & Core Business Logic Lead
**Lead Engineer:** Tanmay Baheti  
**Enrollment No:** 24E1ARADM40P161 | **Roll No:** 24EARAD161  
**Email:** tanmaybaheti2005@gmail.com | **Mobile:** 9511571986  
**Assigned Role:** Algorithm & Core Business Logic Lead  

---

## 1. Module Overview & Core Ownership
Module 2 contains the mathematical brain and algorithmic verification core of **CampusVoice**. It is responsible for:
1. **Verification Engine (`VerificationEngine.java`):** Mathematical validation algorithm assessing cohort turnout and weighted peer affirmation.
2. **Voting Token Hashing (`TokenUtil.java`):** Cryptographic one-way voter tokens preventing Sybil attacks and double-voting while safeguarding voter anonymity.
3. **Lifecycle State Machine:** Deterministic transitions across the 5 lifecycle states:
   `REPORTED` $\to$ `UNDER_VERIFICATION` $\to$ `CLASS_VERIFIED` $\to$ `ASSIGNED_TO_DEPT` $\to$ `RESOLVED` / `REOPENED`.
4. **Re-Verification Logic (`ReverificationEngine.java`):** Post-resolution closed-loop feedback engine triggering automatic priority escalation to **CRITICAL** and reopening issues upon negative feedback.
5. **Algorithmic Test Harness (`AlgorithmTest.java`):** High-coverage JUnit test suite validating statistical edge cases.

---

## 2. Directory Structure & Key Artifacts
```
Module-2_Algorithm_Core_Business_Logic/
├── src/
│   ├── service/
│   │   ├── VerificationEngine.java      # Core mathematical decision engine
│   │   ├── TriggerService.java          # Event trigger dispatching post-vote checks
│   │   └── ReverificationEngine.java    # Resolution validation & auto-escalation
│   └── util/
│       ├── TokenUtil.java               # SHA-256 one-way hashing & salting
│       └── IssueIdGenerator.java        # Cryptographically strong alphanumeric CV-IDs
├── test/
│   └── AlgorithmTest.java               # Automated algorithmic unit testing suite
└── README.md                            # Mathematical proof and specification
```

---

## 3. Mathematical Verification Model

### A. Quorum Participation Rate ($P$):
$$P = \left( \frac{V_{\text{valid}}}{N_{\text{class}}} \right) \times 100$$
- **Threshold Rule:** $P \ge 30\%$ (default institutional minimum)

### B. Weighted Confirmation Score ($S$):
Each peer response carries an assigned scalar weight:
- $w(\text{Frequently}) = 1.0$
- $w(\text{Sometimes}) = 0.5$
- $w(\text{Never}) = 0.0$

$$S = \left[ \frac{\sum_{i=1}^{V} w(\text{response}_i)}{V_{\text{valid}}} \right] \times 100$$
- **Threshold Rule:** $S \ge 60\%$

### C. State Promotion Rule:
$$\text{Status} = \begin{cases} 
\text{CLASS\_VERIFIED}, & \text{if } P \ge 30\% \land S \ge 60\% \\
\text{UNDER\_VERIFICATION}, & \text{otherwise}
\end{cases}$$

---

## 4. One-Way Voter Anonymity Guarantee
To ensure a student cannot vote more than once while preserving zero knowledge of which student voted what:
$$\text{TokenHash} = \text{SHA-256}(\text{StudentId} \,\|\, \text{IssueId} \,\|\, \text{GlobalSalt})$$
- Stored in the `issue_votes` table under a strict `UNIQUE(issue_id, voter_token_hash)` database constraint.
- The student's ID is completely unrecoverable due to SHA-256 pre-image resistance.

---

## 5. Automated Unit Test Execution
Execute the test suite via PowerShell:
```powershell
javac -cp "lib/*" -d out src/service/*.java src/util/*.java test/*.java
java -cp "out;lib/*" campusvoice.AlgorithmTest
```
All algorithmic test vectors (Quorum met/not met, Borderline scores, Duplicate tokens) execute and pass with 100% assertions satisfied.
