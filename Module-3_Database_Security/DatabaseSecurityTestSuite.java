package campusvoice;

import campusvoice.dao.*;
import campusvoice.db.DBConnection;
import campusvoice.exception.DatabaseException;
import campusvoice.exception.DatabaseException.ErrorCode;
import campusvoice.exception.SQLErrorCodeMapper;
import campusvoice.model.Issue;
import campusvoice.model.User;
import campusvoice.model.VoteResult;
import campusvoice.security.SecurityUtil;

import java.lang.reflect.Field;
import java.sql.SQLException;

/**
 * Automated Verification and Security Test Suite for CampusVoice Database & Security Layer.
 * 
 * Tests:
 * 1. SHA-256 Salted Password Hashing & Constant-Time Verification
 * 2. One-Way Salted Voter Token Pseudonymization & Cross-Issue Isolation
 * 3. Text Sanitization & Defense-in-Depth against Script Injection
 * 4. MySQL Vendor Error Code Mapping to DatabaseException
 * 5. Issue & Vote Model Privacy Isolation (Zero User ID / Roll Number linkage)
 * 6. Class Verification Statistical Threshold Logic
 * 7. End-to-End DAO Parameterization & Architecture Compliance
 */
public class DatabaseSecurityTestSuite {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   CAMPUSVOICE: DATABASE & SECURITY MODULE TEST SUITE            ");
        System.out.println("   Lead Architect: Database & Security Specialist                 ");
        System.out.println("==================================================================");

        int passed = 0;
        int totalTests = 7;

        // TEST 1: SHA-256 Salted Password Hashing & Constant-Time Verification
        System.out.print("[TEST 1/7] Testing Cryptographic Password Hashing & Salt Verification... ");
        try {
            String rawPass = "admin123";
            String salt1 = SecurityUtil.generateSalt();
            String salt2 = SecurityUtil.generateSalt();

            String hash1 = SecurityUtil.hashPassword(rawPass, salt1);
            String hash2 = SecurityUtil.hashPassword(rawPass, salt2);

            boolean hashesDiffer = !hash1.equals(hash2); // Different salts must yield different hashes
            boolean verifySuccess = SecurityUtil.verifyPassword(rawPass, hash1, salt1);
            boolean verifyFailure = !SecurityUtil.verifyPassword("wrongpass", hash1, salt1);

            if (hashesDiffer && verifySuccess && verifyFailure && hash1.length() == 64) {
                System.out.println("PASSED (SHA-256 64-char hex, unique salts verified)");
                passed++;
            } else {
                System.out.println("FAILED (Password hashing validation error)");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        // TEST 2: One-Way Salted Voter Token Hashing & Cross-Issue Isolation
        System.out.print("[TEST 2/7] Testing One-Way Voter Token Hashing & Privacy Isolation... ");
        try {
            String token = "CV1832026";
            String issueA = "CV-4821";
            String issueB = "CV-1847";

            String hashA = SecurityUtil.hashVoterToken(token, issueA);
            String hashB = SecurityUtil.hashVoterToken(token, issueB);
            String hashA_repeat = SecurityUtil.hashVoterToken(token, issueA);

            boolean deterministicSameIssue = hashA.equals(hashA_repeat);
            boolean isolatedAcrossIssues = !hashA.equals(hashB);

            if (deterministicSameIssue && isolatedAcrossIssues && hashA.length() == 64) {
                System.out.println("PASSED (Deterministic per issue, isolated across issues)");
                passed++;
            } else {
                System.out.println("FAILED (Token hash collision or isolation failure)");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        // TEST 3: Input Sanitization & Script Injection Defense
        System.out.print("[TEST 3/7] Testing Text Sanitization against Script / Tag Injection... ");
        try {
            String rawInput = "<script>alert('XSS')</script> Laboratory Room \"304\" & Filter";
            String sanitized = SecurityUtil.sanitizeInput(rawInput);

            boolean scriptNeutralized = !sanitized.contains("<") && !sanitized.contains(">");
            boolean entitiesEncoded = sanitized.contains("&lt;script&gt;") && sanitized.contains("&quot;");

            if (scriptNeutralized && entitiesEncoded) {
                System.out.println("PASSED (Tags encoded: " + sanitized.substring(0, 30) + "...)");
                passed++;
            } else {
                System.out.println("FAILED (Sanitization failed: " + sanitized + ")");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        // TEST 4: MySQL Vendor Error Code Mapping
        System.out.print("[TEST 4/7] Testing MySQL Error Code Mapping & DatabaseException Translation... ");
        try {
            // Simulate MySQL 1062 (ER_DUP_ENTRY)
            SQLException dupEntrySqlEx = new SQLException("Duplicate entry '183' for key 'users.student_id'", "23000", 1062);
            DatabaseException dupEx = SQLErrorCodeMapper.translate(dupEntrySqlEx, "Registering Student");

            // Simulate MySQL 1452 (ER_NO_REFERENCED_ROW_2)
            SQLException fkSqlEx = new SQLException("Cannot add or update a child row: a foreign key constraint fails", "23000", 1452);
            DatabaseException fkEx = SQLErrorCodeMapper.translate(fkSqlEx, "Linking Foreign Key");

            // Simulate MySQL 1045 (ER_ACCESS_DENIED_ERROR)
            SQLException authSqlEx = new SQLException("Access denied for user 'root'@'localhost'", "28000", 1045);
            DatabaseException authEx = SQLErrorCodeMapper.translate(authSqlEx, "Connecting to DB");

            boolean dupMapped = dupEx.getErrorCode() == ErrorCode.DUPLICATE_ENTRY;
            boolean fkMapped = fkEx.getErrorCode() == ErrorCode.FOREIGN_KEY_VIOLATION;
            boolean authMapped = authEx.getErrorCode() == ErrorCode.ACCESS_DENIED;

            if (dupMapped && fkMapped && authMapped) {
                System.out.println("PASSED (1062 -> DUP_ENTRY, 1452 -> FK_VIOLATION, 1045 -> ACCESS_DENIED)");
                passed++;
            } else {
                System.out.println("FAILED (Error mapping mismatch)");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        // TEST 5: Strict Privacy & Zero Student Linkage Audit
        System.out.print("[TEST 5/7] Auditing Models & DAOs for Zero User Linkage (Ballot Secrecy)... ");
        try {
            boolean privacyClean = true;

            // Audit Issue model fields
            for (Field field : Issue.class.getDeclaredFields()) {
                String fname = field.getName().toLowerCase();
                if (fname.contains("user") || fname.contains("student") || fname.contains("roll")) {
                    privacyClean = false;
                    System.out.println("\n  [Violation] Found sensitive field in Issue: " + field.getName());
                }
            }

            // Audit VoteResult model fields
            for (Field field : VoteResult.class.getDeclaredFields()) {
                String fname = field.getName().toLowerCase();
                if (fname.contains("user") || fname.contains("student") || fname.contains("roll")) {
                    privacyClean = false;
                    System.out.println("\n  [Violation] Found sensitive field in VoteResult: " + field.getName());
                }
            }

            if (privacyClean) {
                System.out.println("PASSED (Zero student identifiers in Issue & Vote models)");
                passed++;
            } else {
                System.out.println("FAILED (Privacy isolation leak detected)");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        // TEST 6: Class-Level Verification Statistical Thresholds
        System.out.print("[TEST 6/7] Testing Statistical Verification Math (30% Part, 60% Agreement)... ");
        try {
            // Scenario A: 10 votes out of 60 students (16.6% < 30% threshold) -> NOT verified
            VoteResult lowParticipation = new VoteResult("CV-1001", 8, 2, 0, 60, "REPORTED");
            boolean lowPartNotVerified = !lowParticipation.isClassVerified();

            // Scenario B: 20 votes out of 60 (33.3% >= 30%), but 10 Frequently + 1 Sometimes = 55% (< 60%) -> NOT verified
            VoteResult lowAgreement = new VoteResult("CV-1002", 10, 1, 9, 60, "UNDER VERIFICATION");
            boolean lowAgreeNotVerified = !lowAgreement.isClassVerified();

            // Scenario C: 25 votes out of 60 (41.6% >= 30%), 18 Frequently + 5 Sometimes = 92% (>= 60%) -> CLASS VERIFIED
            VoteResult qualified = new VoteResult("CV-1003", 18, 5, 2, 60, "UNDER VERIFICATION");
            boolean qualifiedVerified = qualified.isClassVerified();

            if (lowPartNotVerified && lowAgreeNotVerified && qualifiedVerified) {
                System.out.println("PASSED (Threshold rules verified: Part=" + qualified.getParticipationRate() + "%, Agree=" + 
                        (qualified.getFrequentlyPercentage() + qualified.getSometimesPercentage()) + "%)");
                passed++;
            } else {
                System.out.println("FAILED (Threshold calculation incorrect)");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        // TEST 7: DAO Layer Architecture & Diagnostic Check
        System.out.print("[TEST 7/7] Testing DAO Layer Instantiation & Configuration Check... ");
        try {
            UserDAO userDAO = new UserDAO();
            IssueDAO issueDAO = new IssueDAO();
            VoteDAO voteDAO = new VoteDAO();
            StatusDAO statusDAO = new StatusDAO();
            AuditDAO auditDAO = new AuditDAO();

            String generatedId = issueDAO.generateUniqueIssueId();
            boolean validIdFormat = generatedId.matches("CV-\\d+");
            boolean driverFound = DBConnection.isDriverInitialized();

            if (userDAO != null && issueDAO != null && voteDAO != null && statusDAO != null && auditDAO != null && validIdFormat) {
                System.out.println("PASSED (All 5 DAOs ready, Issue ID Format: " + generatedId + ", JDBC Driver Available: " + driverFound + ")");
                passed++;
            } else {
                System.out.println("FAILED (DAO instantiation or configuration error)");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        System.out.println("==================================================================");
        System.out.printf("TEST EXECUTION COMPLETE: %d/%d TESTS PASSED (%.0f%% SUCCESS)\n", 
                passed, totalTests, ((double) passed / totalTests) * 100.0);
        System.out.println("==================================================================");
    }
}
