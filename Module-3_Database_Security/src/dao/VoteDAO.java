package campusvoice.dao;

import campusvoice.model.VoteOption;
import campusvoice.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Vote Data Access Object (DAO)
 * Manages anonymous peer voting records and duplicate-vote checks (Week 7).
 */
public class VoteDAO {

    /**
     * Checks if a student with the given token hash has already voted on this issue.
     */
    public boolean hasUserVoted(String issueId, String voterTokenHash) {
        String sql = "SELECT 1 FROM votes WHERE issue_id = ? AND voter_token_hash = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, issueId);
            stmt.setString(2, voterTokenHash);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[VoteDAO] Error checking previous vote: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
    }

    /**
     * Records a single peer vote anonymously.
     * Returns true if recorded, false if duplicate or error.
     */
    public boolean recordVote(String issueId, VoteOption response, String voterTokenHash) {
        String sql = "INSERT INTO votes (issue_id, response, voter_token_hash) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, issueId);
            stmt.setString(2, response.name());
            stmt.setString(3, voterTokenHash);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            // Duplicate key exception indicates student has already voted
            System.err.println("[VoteDAO] Vote recording failed (likely duplicate): " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeQuietly(stmt, conn);
        }
    }

    /**
     * Aggregates vote breakdown for an issue to feed into the Verification Engine (Week 8).
     */
    public Map<String, Integer> getVoteCounts(String issueId) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("FREQUENTLY", 0);
        counts.put("SOMETIMES", 0);
        counts.put("NEVER", 0);
        counts.put("TOTAL", 0);

        String sql = "SELECT response, COUNT(*) as cnt FROM votes WHERE issue_id = ? GROUP BY response";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, issueId);
            rs = stmt.executeQuery();

            int total = 0;
            while (rs.next()) {
                String resp = rs.getString("response");
                int c = rs.getInt("cnt");
                counts.put(resp, c);
                total += c;
            }
            counts.put("TOTAL", total);
        } catch (SQLException e) {
            System.err.println("[VoteDAO] Error getting vote counts: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return counts;
    }

    /**
     * Checks if a student has voted on re-verification (Week 11).
     */
    public boolean hasUserReverified(String issueId, String voterTokenHash) {
        String sql = "SELECT 1 FROM reverification_votes WHERE issue_id = ? AND voter_token_hash = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, issueId);
            stmt.setString(2, voterTokenHash);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[VoteDAO] Error checking reverification: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
    }

    /**
     * Records student feedback on whether an issue is truly resolved (Week 11).
     */
    public boolean recordReverification(String issueId, String response, String voterTokenHash) {
        String sql = "INSERT INTO reverification_votes (issue_id, response, voter_token_hash) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, issueId);
            stmt.setString(2, response);
            stmt.setString(3, voterTokenHash);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[VoteDAO] Error recording reverification: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeQuietly(stmt, conn);
        }
    }

    /**
     * Aggregates post-resolution feedback breakdown (Week 11).
     */
    public Map<String, Integer> getReverificationCounts(String issueId) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("RESOLVED", 0);
        counts.put("PARTIALLY_RESOLVED", 0);
        counts.put("STILL_FACING", 0);
        counts.put("TOTAL", 0);

        String sql = "SELECT response, COUNT(*) as cnt FROM reverification_votes WHERE issue_id = ? GROUP BY response";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, issueId);
            rs = stmt.executeQuery();

            int total = 0;
            while (rs.next()) {
                String resp = rs.getString("response");
                int c = rs.getInt("cnt");
                counts.put(resp, c);
                total += c;
            }
            counts.put("TOTAL", total);
        } catch (SQLException e) {
            System.err.println("[VoteDAO] Error getting reverification counts: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return counts;
    }
}
