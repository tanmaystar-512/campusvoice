package campusvoice.util;

import campusvoice.model.ProblemMetadata;
import java.security.SecureRandom;

/**
 * Generates random unique Issue IDs with problem-specific category prefixes:
 * - Classroom: CV-CLS-XXXX
 * - Laboratory: CV-LAB-XXXX
 * - Hostel: CV-HST-XXXX
 * - Canteen: CV-CAN-XXXX
 * - Faculty: CV-ACAD-XXXX
 * - Administration: CV-ADM-XXXX
 * - Default: CV-XXXX
 */
public class IssueIdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a randomized issue ID with default 'CV-' prefix.
     */
    public static String generateId() {
        int number = 1000 + RANDOM.nextInt(9000);
        return "CV-" + number;
    }

    /**
     * Generates a problem-category specific issue ID.
     */
    public static String generateId(String category) {
        if (category == null || category.trim().isEmpty()) {
            return generateId();
        }
        ProblemMetadata meta = ProblemMetadata.forCategory(category);
        int number = 1000 + RANDOM.nextInt(9000);
        return meta.getIdPrefix() + number;
    }
}
