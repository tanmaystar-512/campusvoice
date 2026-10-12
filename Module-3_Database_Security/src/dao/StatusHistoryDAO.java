package campusvoice.dao;

import campusvoice.model.StatusHistory;
import campusvoice.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Status History Data Access Object (DAO)
 * Tracks lifecycle events and audit trail for issues (Week 11).
 */
public class StatusHistoryDAO {

    public List<StatusHistory> findByIssueId(String issueId) {
        List<StatusHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM issue_status WHERE issue_id = ? ORDER BY updated_at ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, issueId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                StatusHistory sh = new StatusHistory();
                sh.setHistoryId(rs.getInt("history_id"));
                sh.setIssueId(rs.getString("issue_id"));
                sh.setOldStatus(rs.getString("old_status"));
                sh.setNewStatus(rs.getString("new_status"));
                sh.setRemarks(rs.getString("remarks"));
                sh.setUpdatedByRole(rs.getString("updated_by_role"));
                sh.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(sh);
            }
        } catch (SQLException e) {
            System.err.println("[StatusHistoryDAO] Error fetching status history: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return list;
    }
}
