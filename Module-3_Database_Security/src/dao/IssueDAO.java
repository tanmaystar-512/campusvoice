package campusvoice.dao;

import campusvoice.model.Issue;
import campusvoice.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Issue Data Access Object (DAO)
 * Manages storage and retrieval of student grievances.
 * 
 * CORE REQUIREMENT (Weeks 6 & 10):
 * Pure anonymous insertion: Absolutely zero student/user identification is persisted.
 */
public class IssueDAO {

    /**
     * Persists a new issue strictly without student identifying foreign keys.
     */
    public boolean createIssue(Issue issue) {
        String sql = "INSERT INTO issues (issue_id, class_id, category, title, description, priority, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, issue.getIssueId());
            stmt.setInt(2, issue.getClassId());
            stmt.setString(3, issue.getCategory());
            stmt.setString(4, issue.getTitle());
            stmt.setString(5, issue.getDescription());
            stmt.setString(6, issue.getPriority() != null ? issue.getPriority() : "MEDIUM");
            stmt.setString(7, issue.getStatus() != null ? issue.getStatus() : "UNDER_VERIFICATION");

            int rows = stmt.executeUpdate();

            // Record initial creation audit log
            String auditSql = "INSERT INTO issue_status (issue_id, old_status, new_status, remarks, updated_by_role) "
                            + "VALUES (?, NULL, ?, 'Issue submitted anonymously for peer verification', 'SYSTEM')";
            try (PreparedStatement auditStmt = conn.prepareStatement(auditSql)) {
                auditStmt.setString(1, issue.getIssueId());
                auditStmt.setString(2, issue.getStatus() != null ? issue.getStatus() : "UNDER_VERIFICATION");
                auditStmt.executeUpdate();
            }

            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[IssueDAO] Error creating issue: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            DBConnection.closeQuietly(stmt, conn);
        }
    }

    public Issue findById(String issueId) {
        String sql = "SELECT i.*, c.class_name FROM issues i "
                   + "LEFT JOIN classes c ON i.class_id = c.class_id "
                   + "WHERE i.issue_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, issueId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractIssueFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[IssueDAO] Error finding issue by id: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return null;
    }

    /**
     * Retrieves active issues for a specific registered class for peer voting (Week 7).
     */
    public List<Issue> findActiveByClassId(int classId) {
        List<Issue> list = new ArrayList<>();
        String sql = "SELECT i.*, c.class_name FROM issues i "
                   + "LEFT JOIN classes c ON i.class_id = c.class_id "
                   + "WHERE i.class_id = ? AND i.status IN ('REPORTED', 'UNDER_VERIFICATION') "
                   + "ORDER BY i.created_at DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, classId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractIssueFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[IssueDAO] Error fetching class issues: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return list;
    }

    /**
     * Admin dynamic search and filter query (Week 10).
     */
    public List<Issue> findAll(String status, String category, String department, Integer classId) {
        List<Issue> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT i.*, c.class_name FROM issues i LEFT JOIN classes c ON i.class_id = c.class_id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND i.status = ?");
            params.add(status.trim());
        }
        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            sql.append(" AND i.category = ?");
            params.add(category.trim());
        }
        if (department != null && !department.trim().isEmpty() && !"ALL".equalsIgnoreCase(department)) {
            sql.append(" AND i.department_assigned = ?");
            params.add(department.trim());
        }
        if (classId != null && classId > 0) {
            sql.append(" AND i.class_id = ?");
            params.add(classId);
        }

        sql.append(" ORDER BY i.created_at DESC");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractIssueFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[IssueDAO] Error filtering issues: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return list;
    }

    /**
     * Updates status and records audit history (Weeks 8 & 11).
     */
    public boolean updateStatus(String issueId, String newStatus, String remarks, String updatedByRole) {
        Issue current = findById(issueId);
        if (current == null) return false;

        String oldStatus = current.getStatus();
        String updateSql = "UPDATE issues SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE issue_id = ?";
        String auditSql = "INSERT INTO issue_status (issue_id, old_status, new_status, remarks, updated_by_role) "
                        + "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement updateStmt = null;
        PreparedStatement auditStmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newStatus);
            updateStmt.setString(2, issueId);
            updateStmt.executeUpdate();

            auditStmt = conn.prepareStatement(auditSql);
            auditStmt.setString(1, issueId);
            auditStmt.setString(2, oldStatus);
            auditStmt.setString(3, newStatus);
            auditStmt.setString(4, remarks);
            auditStmt.setString(5, updatedByRole != null ? updatedByRole : "SYSTEM");
            auditStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[IssueDAO] Error updating issue status: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            DBConnection.closeQuietly(updateStmt, auditStmt, conn);
        }
    }

    /**
     * Admin updates department assignment, status, and resolution remarks (Week 10 & 11).
     */
    public boolean updateResolution(String issueId, String department, String status, String remarks) {
        Issue current = findById(issueId);
        if (current == null) return false;

        String oldStatus = current.getStatus();
        String sql = "UPDATE issues SET department_assigned = ?, status = ?, resolution_remarks = ?, updated_at = CURRENT_TIMESTAMP "
                   + "WHERE issue_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement auditStmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, department);
            stmt.setString(2, status);
            stmt.setString(3, remarks);
            stmt.setString(4, issueId);
            stmt.executeUpdate();

            String auditSql = "INSERT INTO issue_status (issue_id, old_status, new_status, remarks, updated_by_role) "
                            + "VALUES (?, ?, ?, ?, 'ADMIN')";
            auditStmt = conn.prepareStatement(auditSql);
            auditStmt.setString(1, issueId);
            auditStmt.setString(2, oldStatus);
            auditStmt.setString(3, status);
            auditStmt.setString(4, remarks != null ? remarks : "Admin assigned department/updated status");
            auditStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[IssueDAO] Error updating resolution: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            DBConnection.closeQuietly(stmt, auditStmt, conn);
        }
    }

    /**
     * Aggregated metrics for Admin Dashboard KPI cards (Week 10).
     */
    public Map<String, Integer> getDashboardCounts() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as cnt FROM issues GROUP BY status";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            int total = 0;
            while (rs.next()) {
                String st = rs.getString("status");
                int c = rs.getInt("cnt");
                counts.put(st, c);
                total += c;
            }
            counts.put("TOTAL", total);
        } catch (SQLException e) {
            System.err.println("[IssueDAO] Error fetching dashboard counts: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return counts;
    }

    private Issue extractIssueFromResultSet(ResultSet rs) throws SQLException {
        Issue issue = new Issue();
        issue.setIssueId(rs.getString("issue_id"));
        issue.setClassId(rs.getInt("class_id"));
        issue.setCategory(rs.getString("category"));
        issue.setTitle(rs.getString("title"));
        issue.setDescription(rs.getString("description"));
        issue.setPriority(rs.getString("priority"));
        issue.setStatus(rs.getString("status"));
        issue.setDepartmentAssigned(rs.getString("department_assigned"));
        issue.setResolutionRemarks(rs.getString("resolution_remarks"));
        issue.setCreatedAt(rs.getTimestamp("created_at"));
        issue.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            issue.setClassName(rs.getString("class_name"));
        } catch (SQLException ignored) {}

        return issue;
    }
}
