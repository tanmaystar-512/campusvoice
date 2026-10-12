package campusvoice.service;

import campusvoice.dao.IssueDAO;
import campusvoice.dao.VoteDAO;
import campusvoice.model.Issue;
import java.util.Map;

/**
 * ReverificationEngine
 * Member 2 Responsibility (Week 11):
 * Processes student feedback on resolved issues:
 * If majority feedback indicates 'STILL_FACING', automatically resets status to 'REOPENED'.
 */
public class ReverificationEngine {

    private final IssueDAO issueDAO;
    private final VoteDAO voteDAO;

    public ReverificationEngine() {
        this.issueDAO = new IssueDAO();
        this.voteDAO = new VoteDAO();
    }

    public ReverificationEngine(IssueDAO issueDAO, VoteDAO voteDAO) {
        this.issueDAO = issueDAO;
        this.voteDAO = voteDAO;
    }

    /**
     * Evaluates reverification votes and resets to REOPENED if majority report problem persists.
     */
    public boolean evaluateAndProcess(String issueId) {
        Issue issue = issueDAO.findById(issueId);
        if (issue == null) return false;

        Map<String, Integer> counts = voteDAO.getReverificationCounts(issueId);
        int total = counts.getOrDefault("TOTAL", 0);
        int stillFacing = counts.getOrDefault("STILL_FACING", 0);

        if (total > 0 && "RESOLVED".equalsIgnoreCase(issue.getStatus())) {
            double stillFacingPercent = ((double) stillFacing / total) * 100.0;
            if (stillFacingPercent > 50.0) {
                return issueDAO.updateStatus(
                        issueId,
                        "REOPENED",
                        String.format("Automatically reopened by Re-verification Engine: %d/%d (%.1f%%) students report problem persists",
                                stillFacing, total, stillFacingPercent),
                        "STUDENT_BODY"
                );
            }
        }
        return false;
    }
}
