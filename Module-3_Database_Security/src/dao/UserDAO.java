package campusvoice.dao;

import campusvoice.model.User;
import campusvoice.util.DBConnection;
import campusvoice.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User Data Access Object (DAO)
 * Handles database operations for student and admin authentication,
 * custom user registration, forgot password reset, and change password.
 */
public class UserDAO {

    /**
     * Authenticates a user given their student ID / username and raw password.
     */
    public User authenticate(String studentId, String plainPassword) {
        if (studentId == null || plainPassword == null) {
            return null;
        }

        String sql = "SELECT user_id, student_id, password_hash, full_name, email, role, class_id, created_at "
                   + "FROM users WHERE student_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, studentId.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (PasswordUtil.verifyPassword(plainPassword, storedHash)) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error authenticating user: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return null;
    }

    /**
     * Registers a new user with custom User ID and Password.
     */
    public boolean createUser(User user) {
        if (user == null || user.getStudentId() == null || user.getPasswordHash() == null) {
            return false;
        }

        String sql = "INSERT INTO users (student_id, password_hash, full_name, email, role, class_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getStudentId().trim());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFullName() != null ? user.getFullName().trim() : user.getStudentId());
            stmt.setString(4, user.getEmail() != null ? user.getEmail().trim().toLowerCase() : user.getStudentId() + "@campus.edu");
            stmt.setString(5, user.getRole() != null ? user.getRole().toUpperCase() : "STUDENT");
            if (user.getClassId() != null && user.getClassId() > 0) {
                stmt.setInt(6, user.getClassId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error registering user: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeQuietly(stmt, conn);
        }
    }

    public boolean isStudentIdTaken(String studentId) {
        if (studentId == null) return false;
        String sql = "SELECT 1 FROM users WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, studentId.trim());
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error checking studentId: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
    }

    public boolean isEmailTaken(String email) {
        if (email == null) return false;
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email.trim());
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error checking email: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
    }

    /**
     * Resets password using security verification (Student ID + registered Email).
     */
    public boolean resetPassword(String studentId, String email, String newPlainPassword) {
        if (studentId == null || email == null || newPlainPassword == null) {
            return false;
        }

        String checkSql = "SELECT user_id FROM users WHERE student_id = ? AND LOWER(email) = LOWER(?)";
        String updateSql = "UPDATE users SET password_hash = ? WHERE student_id = ?";

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, studentId.trim());
            checkStmt.setString(2, email.trim());
            rs = checkStmt.executeQuery();

            if (!rs.next()) {
                return false; // Student ID and Email do not match
            }

            String newHash = PasswordUtil.hashPassword(newPlainPassword);
            updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newHash);
            updateStmt.setString(2, studentId.trim());

            return updateStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error resetting password: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeQuietly(rs, checkStmt, updateStmt, conn);
        }
    }

    /**
     * Changes password when authenticated by verifying the old password.
     */
    public boolean changePassword(String studentId, String oldPlainPassword, String newPlainPassword) {
        if (studentId == null || oldPlainPassword == null || newPlainPassword == null) {
            return false;
        }

        User user = authenticate(studentId, oldPlainPassword);
        if (user == null) {
            return false; // Old password verification failed
        }

        String sql = "UPDATE users SET password_hash = ? WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            String newHash = PasswordUtil.hashPassword(newPlainPassword);
            stmt.setString(1, newHash);
            stmt.setString(2, studentId.trim());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error changing password: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeQuietly(stmt, conn);
        }
    }

    public User findByStudentId(String studentId) {
        String sql = "SELECT * FROM users WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, studentId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error finding user: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return null;
    }

    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error finding user by id: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return null;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setStudentId(rs.getString("student_id"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));

        int classId = rs.getInt("class_id");
        if (!rs.wasNull()) {
            user.setClassId(classId);
        }
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
