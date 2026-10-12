package campusvoice.model;

import java.sql.Timestamp;

/**
 * Vote Model
 * Represents an anonymous vote cast by a student on a specific issue.
 * Notice: Voter identity is never stored directly. Instead, a one-way hashed
 * token is stored to prevent duplicate votes while ensuring vote privacy.
 */
public class Vote {
    private int voteId;
    private String issueId;
    private VoteOption response;
    private String voterTokenHash;
    private Timestamp votedAt;

    public Vote() {}

    public Vote(int voteId, String issueId, VoteOption response, String voterTokenHash, Timestamp votedAt) {
        this.voteId = voteId;
        this.issueId = issueId;
        this.response = response;
        this.voterTokenHash = voterTokenHash;
        this.votedAt = votedAt;
    }

    public int getVoteId() {
        return voteId;
    }

    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public VoteOption getResponse() {
        return response;
    }

    public void setResponse(VoteOption response) {
        this.response = response;
    }

    public String getVoterTokenHash() {
        return voterTokenHash;
    }

    public void setVoterTokenHash(String voterTokenHash) {
        this.voterTokenHash = voterTokenHash;
    }

    public Timestamp getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(Timestamp votedAt) {
        this.votedAt = votedAt;
    }
}
