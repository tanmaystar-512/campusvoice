package campusvoice.model;

import java.sql.Timestamp;

/**
 * Issue Model / DTO
 * Represents a student grievance.
 * 
 * CORE ARCHITECTURAL PRINCIPLE:
 * Strict Identity Decoupling: This entity deliberately does NOT contain any
 * reference to the submitting student (no userId, no studentId). Only classId
 * is retained so classmates can vote on it.
 */
public class Issue {
    private String issueId;            // e.g. "CV-1847"
    private int classId;               // Eligible class for peer voting
    private String category;           // e.g. "Academics", "IT Lab", "Maintenance", "Hostel", "Canteen"
    private String title;
    private String description;
    private String priority;           // "LOW", "MEDIUM", "HIGH", "URGENT"
    private String status;             // IssueStatus name: "REPORTED", "UNDER_VERIFICATION", "CLASS_VERIFIED", etc.
    private String departmentAssigned; // Assigned department for resolution
    private String resolutionRemarks;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Optional auxiliary display fields (populated in joins, not DB columns)
    private String className;
    private VerificationResult verificationResult;
    private Boolean userHasVoted;      // Set dynamically for the querying user session

    public Issue() {}

    public Issue(String issueId, int classId, String category, String title, String description, String priority, String status) {
        this.issueId = issueId;
        this.classId = classId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartmentAssigned() {
        return departmentAssigned;
    }

    public void setDepartmentAssigned(String departmentAssigned) {
        this.departmentAssigned = departmentAssigned;
    }

    public String getResolutionRemarks() {
        return resolutionRemarks;
    }

    public void setResolutionRemarks(String resolutionRemarks) {
        this.resolutionRemarks = resolutionRemarks;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public VerificationResult getVerificationResult() {
        return verificationResult;
    }

    public void setVerificationResult(VerificationResult verificationResult) {
        this.verificationResult = verificationResult;
    }

    public Boolean getUserHasVoted() {
        return userHasVoted;
    }

    public void setUserHasVoted(Boolean userHasVoted) {
        this.userHasVoted = userHasVoted;
    }
}
