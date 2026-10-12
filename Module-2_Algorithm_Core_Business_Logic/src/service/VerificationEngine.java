package campusvoice.service;

import campusvoice.model.VerificationResult;
import campusvoice.util.DBConnection;
import java.util.Map;

/**
 * VerificationEngine - Algorithmic Verification Core
 * Programmed according to Member 2 specifications (Weeks 1, 8):
 * 
 * 1. Participation Rate (P):
 *    P = (Total Valid Votes Received / Total Eligible Class Strength) * 100
 *    Condition: P >= 30.0% (Configurable threshold)
 * 
 * 2. Weighted Confirmation Score (S):
 *    Weights: Frequently = 1.0, Sometimes = 0.5, Never = 0.0
 *    S = [ (Count(Frequently) * 1.0 + Count(Sometimes) * 0.5) / Total Valid Votes ] * 100
 *    Condition: S >= 60.0% (Configurable threshold)
 * 
 * 3. State Transition Rule:
 *    IF (P >= 30% AND S >= 60%) -> 'CLASS_VERIFIED'
 *    ELSE -> 'UNDER_VERIFICATION'
 */
public class VerificationEngine {

    public static final double DEFAULT_MIN_PARTICIPATION = 30.0;
    public static final double DEFAULT_MIN_CONFIRMATION = 60.0;

    private final double minParticipation;
    private final double minConfirmation;

    public VerificationEngine() {
        double p = DEFAULT_MIN_PARTICIPATION;
        double c = DEFAULT_MIN_CONFIRMATION;
        try {
            String pStr = DBConnection.getProperties().getProperty("algorithm.participation.min");
            if (pStr != null) p = Double.parseDouble(pStr.trim());

            String cStr = DBConnection.getProperties().getProperty("algorithm.confirmation.min");
            if (cStr != null) c = Double.parseDouble(cStr.trim());
        } catch (Exception ignored) {}

        this.minParticipation = p;
        this.minConfirmation = c;
    }

    public VerificationEngine(double minParticipation, double minConfirmation) {
        this.minParticipation = minParticipation;
        this.minConfirmation = minConfirmation;
    }

    /**
     * Evaluates votes against class strength and returns detailed VerificationResult.
     */
    public VerificationResult evaluate(String issueId, int totalClassStrength, Map<String, Integer> voteCounts, String currentStatus) {
        int freq = voteCounts.getOrDefault("FREQUENTLY", 0);
        int some = voteCounts.getOrDefault("SOMETIMES", 0);
        int never = voteCounts.getOrDefault("NEVER", 0);
        int totalVotes = voteCounts.getOrDefault("TOTAL", freq + some + never);

        // Guard against division by zero
        if (totalClassStrength <= 0) {
            totalClassStrength = 60; // default baseline strength
        }

        // 1. Calculate Participation Rate P
        double participationRate = ((double) totalVotes / totalClassStrength) * 100.0;
        // Round to 2 decimal places
        participationRate = Math.round(participationRate * 100.0) / 100.0;

        // 2. Calculate Weighted Confirmation Score S
        double confirmationScore = 0.0;
        if (totalVotes > 0) {
            double weightedSum = (freq * 1.0) + (some * 0.5) + (never * 0.0);
            confirmationScore = (weightedSum / totalVotes) * 100.0;
            confirmationScore = Math.round(confirmationScore * 100.0) / 100.0;
        }

        // 3. State Transition Rule
        boolean thresholdMet = (participationRate >= minParticipation) && (confirmationScore >= minConfirmation);

        String calculatedStatus = currentStatus;
        if (thresholdMet) {
            calculatedStatus = "CLASS_VERIFIED";
        }

        return new VerificationResult(
                issueId,
                totalClassStrength,
                totalVotes,
                freq,
                some,
                never,
                participationRate,
                confirmationScore,
                thresholdMet,
                calculatedStatus
        );
    }

    public double getMinParticipation() {
        return minParticipation;
    }

    public double getMinConfirmation() {
        return minConfirmation;
    }
}
