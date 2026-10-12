package campusvoice.model;

import java.sql.Timestamp;

/**
 * Evidence Model
 * Stores metadata of uploaded evidence files associated with an issue.
 */
public class Evidence {
    private int evidenceId;
    private String issueId;
    private String fileName;
    private String filePath;
    private String fileType;
    private long fileSize;
    private Timestamp uploadedAt;

    public Evidence() {}

    public Evidence(int evidenceId, String issueId, String fileName, String filePath, String fileType, long fileSize, Timestamp uploadedAt) {
        this.evidenceId = evidenceId;
        this.issueId = issueId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
    }

    public int getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(int evidenceId) {
        this.evidenceId = evidenceId;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
