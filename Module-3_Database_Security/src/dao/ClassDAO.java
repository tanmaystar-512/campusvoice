package campusvoice.dao;

import campusvoice.model.ClassModel;
import campusvoice.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Data Access Object (DAO)
 * Handles operations related to academic class groups and classroom strength.
 */
public class ClassDAO {

    public ClassModel findById(int classId) {
        String sql = "SELECT * FROM classes WHERE class_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, classId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractClassFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ClassDAO] Error fetching class: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return null;
    }

    public List<ClassModel> findAll() {
        List<ClassModel> list = new ArrayList<>();
        String sql = "SELECT * FROM classes ORDER BY class_name ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractClassFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ClassDAO] Error listing classes: " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(rs, stmt, conn);
        }
        return list;
    }

    private ClassModel extractClassFromResultSet(ResultSet rs) throws SQLException {
        ClassModel cm = new ClassModel();
        cm.setClassId(rs.getInt("class_id"));
        cm.setClassName(rs.getString("class_name"));
        cm.setDepartment(rs.getString("department"));
        cm.setSemester(rs.getInt("semester"));
        cm.setSection(rs.getString("section"));
        cm.setTotalStrength(rs.getInt("total_strength"));
        cm.setCreatedAt(rs.getTimestamp("created_at"));
        return cm;
    }
}
