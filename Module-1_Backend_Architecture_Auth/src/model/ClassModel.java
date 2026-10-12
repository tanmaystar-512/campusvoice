package campusvoice.model;

import java.sql.Timestamp;

/**
 * ClassModel Bean / DTO
 * Represents an academic classroom group with its total student strength
 * used for calculating participation rate P = (Votes / Total Strength) * 100.
 */
public class ClassModel {
    private int classId;
    private String className;
    private String department;
    private int semester;
    private String section;
    private int totalStrength;
    private Timestamp createdAt;

    public ClassModel() {}

    public ClassModel(int classId, String className, String department, int semester, String section, int totalStrength) {
        this.classId = classId;
        this.className = className;
        this.department = department;
        this.semester = semester;
        this.section = section;
        this.totalStrength = totalStrength;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getTotalStrength() {
        return totalStrength;
    }

    public void setTotalStrength(int totalStrength) {
        this.totalStrength = totalStrength;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
