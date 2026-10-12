package campusvoice.model;

import java.sql.Timestamp;

/**
 * StatusHistory Model
 * Represents an audit trail record of status transitions for an issue.
 */
public class StatusHistory {
    private int historyId;
    private String issueId;
    private String oldStatus;
    private String newStatus;
    private String remarks;
    private String updatedByRole;
    private Timestamp updatedAt;

    public StatusHistory() {}

    public StatusHistory(int historyId, String issueId, String oldStatus, String newStatus, String remarks, String updatedByRole, Timestamp updatedAt) {
        this.historyId = historyId;
        this.issueId = issueId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.remarks = remarks;
        this.updatedByRole = updatedByRole;
        this.updatedAt = updatedAt;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUpdatedByRole() {
        return updatedByRole;
    }

    public void setUpdatedByRole(String updatedByRole) {
        this.updatedByRole = updatedByRole;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
