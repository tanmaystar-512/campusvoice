package campusvoice.model;

import java.sql.Timestamp;

/**
 * User Bean / DTO
 * Represents an authenticated user (Student or Administrator).
 * Important: Passwords are not exposed in client responses.
 */
public class User {
    private int userId;
    private String studentId;      // Institutional Roll No or Username
    private String passwordHash;
    private String fullName;
    private String email;
    private String role;           // "STUDENT" or "ADMIN"
    private Integer classId;       // Nullable for Admin
    private Timestamp createdAt;

    public User() {}

    public User(int userId, String studentId, String passwordHash, String fullName, String email, String role, Integer classId, Timestamp createdAt) {
        this.userId = userId;
        this.studentId = studentId;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.classId = classId;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isStudent() {
        return "STUDENT".equalsIgnoreCase(role);
    }
}
