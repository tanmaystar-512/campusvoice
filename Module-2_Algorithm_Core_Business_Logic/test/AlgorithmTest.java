package campusvoice;

import campusvoice.model.VerificationResult;
import campusvoice.service.VerificationEngine;
import campusvoice.util.IssueIdGenerator;
import campusvoice.util.PasswordUtil;
import campusvoice.util.TokenUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * CampusVoice Algorithm & Core Logic Test Suite
 * Validates Week 1-8 mathematical specifications and security boundaries.
 */
public class AlgorithmTest {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  RUNNING CAMPUSVOICE BACKEND BASE UNIT TESTS    ");
        System.out.println("=================================================\n");

        int passed = 0;
        int failed = 0;

        // Test 1: Verification Algorithm - Boundary condition (P < 30%)
        try {
            VerificationEngine engine = new VerificationEngine(30.0, 60.0);
            Map<String, Integer> votes = new HashMap<>();
            votes.put("FREQUENTLY", 17);
            votes.put("SOMETIMES", 0);
            votes.put("NEVER", 0);
            votes.put("TOTAL", 17);
            // Class strength = 60, 17/60 = 28.33% (< 30%)
            VerificationResult res = engine.evaluate("CV-TEST", 60, votes, "UNDER_VERIFICATION");
            assertTest(!res.isThresholdMet(), "Participation < 30% must NOT verify (got P=" + res.getParticipationRate() + "%)");
            assertTest("UNDER_VERIFICATION".equals(res.getCurrentStatus()), "Status should remain UNDER_VERIFICATION");
            passed++;
        } catch (Throwable t) {
            System.err.println("Test 1 Failed: " + t.getMessage());
            failed++;
        }

        // Test 2: Verification Algorithm - Exact qualification (P >= 30%, S >= 60%)
        try {
            VerificationEngine engine = new VerificationEngine(30.0, 60.0);
            Map<String, Integer> votes = new HashMap<>();
            votes.put("FREQUENTLY", 12); // 12 * 1.0 = 12
            votes.put("SOMETIMES", 6);   // 6 * 0.5 = 3 -> Total weighted = 15
            votes.put("NEVER", 2);       // 2 * 0.0 = 0
            votes.put("TOTAL", 20);      // 20/60 = 33.33% (>= 30%), Score = 15/20 = 75% (>= 60%)
            VerificationResult res = engine.evaluate("CV-TEST", 60, votes, "UNDER_VERIFICATION");
            assertTest(res.isThresholdMet(), "P=33.33% & S=75.0% MUST verify");
            assertTest("CLASS_VERIFIED".equals(res.getCurrentStatus()), "Status must become CLASS_VERIFIED");
            passed++;
        } catch (Throwable t) {
            System.err.println("Test 2 Failed: " + t.getMessage());
            failed++;
        }

        // Test 3: Verification Algorithm - High participation but Low confirmation (S < 60%)
        try {
            VerificationEngine engine = new VerificationEngine(30.0, 60.0);
            Map<String, Integer> votes = new HashMap<>();
            votes.put("FREQUENTLY", 2);
            votes.put("SOMETIMES", 2);   // Weighted = 2 + 1 = 3
            votes.put("NEVER", 26);
            votes.put("TOTAL", 30);      // P = 30/60 = 50% (>= 30%), Score = 3/30 = 10% (< 60%)
            VerificationResult res = engine.evaluate("CV-TEST", 60, votes, "UNDER_VERIFICATION");
            assertTest(!res.isThresholdMet(), "Low confirmation (10%) must NOT verify even if P=50%");
            assertTest("UNDER_VERIFICATION".equals(res.getCurrentStatus()), "Status must remain UNDER_VERIFICATION");
            passed++;
        } catch (Throwable t) {
            System.err.println("Test 3 Failed: " + t.getMessage());
            failed++;
        }

        // Test 4: One-Way Voter Token Hashing (Secrecy & Determinism)
        try {
            String hash1 = TokenUtil.generateVoterTokenHash("24EARAD163", "CV-1847");
            String hash2 = TokenUtil.generateVoterTokenHash("24EARAD163", "CV-1847");
            String hash3 = TokenUtil.generateVoterTokenHash("24EARAD164", "CV-1847");

            assertTest(hash1.equals(hash2), "Same student on same issue must yield identical token hash for duplicate prevention");
            assertTest(!hash1.equals(hash3), "Different students must produce unique hashes");
            assertTest(hash1.length() == 64, "SHA-256 hash must be 64 hexadecimal characters");
            passed++;
        } catch (Throwable t) {
            System.err.println("Test 4 Failed: " + t.getMessage());
            failed++;
        }

        // Test 5: Password Hashing with SHA-256
        try {
            String hash = PasswordUtil.hashPassword("password123");
            assertTest(PasswordUtil.verifyPassword("password123", hash), "Password verification failed");
            assertTest(!PasswordUtil.verifyPassword("wrongpass", hash), "Wrong password must be rejected");
            passed++;
        } catch (Throwable t) {
            System.err.println("Test 5 Failed: " + t.getMessage());
            failed++;
        }

        // Test 6: Issue ID Generator
        try {
            String id = IssueIdGenerator.generateId();
            assertTest(id.startsWith("CV-"), "Issue ID must start with 'CV-'");
            assertTest(id.length() == 7, "Issue ID must be 'CV-' followed by 4 digits (e.g. CV-1847)");
            passed++;
        } catch (Throwable t) {
            System.err.println("Test 6 Failed: " + t.getMessage());
            failed++;
        }

        System.out.println(String.format("%n================================================="));
        System.out.println(String.format("  TEST SUMMARY: %d PASSED, %d FAILED             ", passed, failed));
        System.out.println("=================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertTest(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion Failed: " + message);
        }
        System.out.println("[PASS] " + message);
    }
}
