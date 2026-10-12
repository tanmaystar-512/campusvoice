package campusvoice.service;

import campusvoice.dao.ClassDAO;
import campusvoice.dao.IssueDAO;
import campusvoice.dao.VoteDAO;
import campusvoice.model.ClassModel;
import campusvoice.model.Issue;
import campusvoice.model.VerificationResult;
import java.util.Map;

/**
 * TriggerService - Automated Verification Post-Vote Sequence
 * Member 1 Responsibility (Week 8):
 * Automatically triggered after every peer vote to recalculate issue validity
 * and transition state from 'UNDER_VERIFICATION' to 'CLASS_VERIFIED'.
 */
public class TriggerService {

    private final IssueDAO issueDAO;
    private final ClassDAO classDAO;
    private final VoteDAO voteDAO;
    private final VerificationEngine verificationEngine;

    public TriggerService() {
        this.issueDAO = new IssueDAO();
        this.classDAO = new ClassDAO();
        this.voteDAO = new VoteDAO();
        this.verificationEngine = new VerificationEngine();
    }

    public TriggerService(IssueDAO issueDAO, ClassDAO classDAO, VoteDAO voteDAO, VerificationEngine verificationEngine) {
        this.issueDAO = issueDAO;
        this.classDAO = classDAO;
        this.voteDAO = voteDAO;
        this.verificationEngine = verificationEngine;
    }

    /**
     * Evaluates votes and applies automated state transition if thresholds are satisfied.
     */
    public VerificationResult evaluateAndTrigger(String issueId) {
        Issue issue = issueDAO.findById(issueId);
        if (issue == null) {
            return null;
        }

        // Retrieve classroom total strength
        int classStrength = 60;
        ClassModel cm = classDAO.findById(issue.getClassId());
        if (cm != null && cm.getTotalStrength() > 0) {
            classStrength = cm.getTotalStrength();
        }

        // Fetch aggregated vote breakdown
        Map<String, Integer> counts = voteDAO.getVoteCounts(issueId);

        // Run Verification Engine logic
        VerificationResult result = verificationEngine.evaluate(
                issueId,
                classStrength,
                counts,
                issue.getStatus()
        );

        // State Transition: If threshold met and issue was under verification, promote to CLASS_VERIFIED
        String currentStatus = issue.getStatus();
        if (result.isThresholdMet() && ("UNDER_VERIFICATION".equalsIgnoreCase(currentStatus) || "REPORTED".equalsIgnoreCase(currentStatus))) {
            boolean updated = issueDAO.updateStatus(
                    issueId,
                    "CLASS_VERIFIED",
                    String.format("Promoted by Verification Engine: Participation=%.1f%% (>=%.0f%%), Confirmation=%.1f%% (>=%.0f%%)",
                            result.getParticipationRate(),
                            verificationEngine.getMinParticipation(),
                            result.getConfirmationScore(),
                            verificationEngine.getMinConfirmation()),
                    "SYSTEM"
            );
            if (updated) {
                result.setCurrentStatus("CLASS_VERIFIED");
            }
        }

        return result;
    }
}
