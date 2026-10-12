package campusvoice.model;

/**
 * VerificationResult Model
 * Contains the outcome of the Class-Level Verification Algorithm:
 * - Participation Rate P = (Total Valid Votes / Class Strength) * 100  (Threshold >= 30%)
 * - Weighted Confirmation Score S = [(Freq * 1.0 + Some * 0.5) / Total Votes] * 100  (Threshold >= 60%)
 */
public class VerificationResult {
    private String issueId;
    private int totalStrength;
    private int totalVotes;
    private int frequentlyCount;
    private int sometimesCount;
    private int neverCount;
    private double participationRate;     // P in percentage
    private double confirmationScore;      // S in percentage
    private boolean thresholdMet;
    private String currentStatus;

    public VerificationResult() {}

    public VerificationResult(String issueId, int totalStrength, int totalVotes, int frequentlyCount, int sometimesCount, int neverCount, double participationRate, double confirmationScore, boolean thresholdMet, String currentStatus) {
        this.issueId = issueId;
        this.totalStrength = totalStrength;
        this.totalVotes = totalVotes;
        this.frequentlyCount = frequentlyCount;
        this.sometimesCount = sometimesCount;
        this.neverCount = neverCount;
        this.participationRate = participationRate;
        this.confirmationScore = confirmationScore;
        this.thresholdMet = thresholdMet;
        this.currentStatus = currentStatus;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public int getTotalStrength() {
        return totalStrength;
    }

    public void setTotalStrength(int totalStrength) {
        this.totalStrength = totalStrength;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public int getFrequentlyCount() {
        return frequentlyCount;
    }

    public void setFrequentlyCount(int frequentlyCount) {
        this.frequentlyCount = frequentlyCount;
    }

    public int getSometimesCount() {
        return sometimesCount;
    }

    public void setSometimesCount(int sometimesCount) {
        this.sometimesCount = sometimesCount;
    }

    public int getNeverCount() {
        return neverCount;
    }

    public void setNeverCount(int neverCount) {
        this.neverCount = neverCount;
    }

    public double getParticipationRate() {
        return participationRate;
    }

    public void setParticipationRate(double participationRate) {
        this.participationRate = participationRate;
    }

    public double getConfirmationScore() {
        return confirmationScore;
    }

    public void setConfirmationScore(double confirmationScore) {
        this.confirmationScore = confirmationScore;
    }

    public boolean isThresholdMet() {
        return thresholdMet;
    }

    public void setThresholdMet(boolean thresholdMet) {
        this.thresholdMet = thresholdMet;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}
