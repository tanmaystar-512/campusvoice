package campusvoice.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Token Utility for Anonymous Voting Privacy
 * Member 2 & 1 Responsibility (Weeks 3 & 7):
 * Generates a one-way cryptographic SHA-256 hash representing a student's eligibility to vote
 * on a specific issue.
 * 
 * Formula:
 * VoterTokenHash = SHA-256( studentId + ":" + issueId + ":" + secretSalt )
 * 
 * Guarantees:
 * 1. Double-Voting Prevention: Database enforces UNIQUE(issue_id, voter_token_hash).
 * 2. Perfect Anonymity: The hash is computationally irreversible, ensuring zero trace of student identity.
 */
public class TokenUtil {

    private static final String DEFAULT_SALT = "CAMPUS_VOICE_SECRET_SALT_2026_X92";

    public static String generateVoterTokenHash(String studentId, String issueId) {
        String salt = DBConnection.getProperties().getProperty("security.voting.salt", DEFAULT_SALT);
        String rawInput = studentId.trim().toUpperCase() + ":" + issueId.trim() + ":" + salt;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawInput.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
