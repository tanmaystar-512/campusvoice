package campusvoice.dao;

import campusvoice.model.Evidence;
import campusvoice.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Evidence Data Access Object (DAO)
 * Manages attachments and evidence metadata for issues (Week 9).
 */
public class EvidenceDAO {

    public boolean addEvidence(Evidence evidence) {
        String sql = "INSERT INTO evidence (issue_id, file_name, file_path, file_type, file_size) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, evidence.getIssueId());
            stmt.setString(2, evidence.getFileName());
            stmt.setString(3, evidence.getFilePath());
            stmt.setString(4, evidence.getFileType());
            stmt.setLong(5, evidence.getFileSize());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EvidenceDAO] Error adding evidence: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeQuietly(stmt, conn);
        }
    }

    public List<Evidence> findByIssueId(String issueId) {
        List<Evidence> list = new ArrayList<>();
        String sql = "SELECT * FROM evidence WHERE issue_id = ? ORDER BY uploaded_at ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, issueId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Evidence ev = new Evidence();
                ev.setEvidenceId(rs.getInt("evidence_id"));
                ev.setIssueId(rs.getString("issue_id"));
                ev.setFileName(rs.getString("file_name"));
                ev.setFilePath(rs.getString("file_path"));
                ev.setFileType(rs.getString("file_type"));
                ev.setFileSize(rs.getLong("file_size"));
                ev.setUploadedAt(rs.getTimestamp("uploaded_at"));
                list.add(ev);
            }
        } catch (SQLException e) {
            System.err.println("[EvidenceDAO] Error listing evidence: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return list;
    }
}
