package campusvoice.model;

/**
 * ProblemMetadata
 * Generates and encapsulates distinct operational values for 12+ problem categories:
 * - Category-specific Issue ID prefix (e.g. CV-LAB-, CV-CLS-, CV-HST-, CV-CAN-, CV-NET-, CV-MED-, etc.)
 * - Automated Department routing
 * - SLA Resolution Target (hours)
 * - Baseline Severity Rating (1-10)
 * - Urgency factor & quorum requirements
 */
public class ProblemMetadata {

    private final String category;
    private final String idPrefix;
    private final String targetDepartment;
    private final int slaHours;
    private final int baseSeverity;
    private final double minParticipationThreshold;
    private final String suggestedPriority;

    public ProblemMetadata(String category, String idPrefix, String targetDepartment, int slaHours, int baseSeverity, double minParticipationThreshold, String suggestedPriority) {
        this.category = category;
        this.idPrefix = idPrefix;
        this.targetDepartment = targetDepartment;
        this.slaHours = slaHours;
        this.baseSeverity = baseSeverity;
        this.minParticipationThreshold = minParticipationThreshold;
        this.suggestedPriority = suggestedPriority;
    }

    public static ProblemMetadata forCategory(String category) {
        if (category == null) category = "Other";
        String normalized = category.trim().toUpperCase();

        if (normalized.contains("MED") || normalized.contains("HEALTH")) {
            return new ProblemMetadata("Health & Medical", "CV-MED-", "Campus Health Center & Emergency Desk", 6, 10, 20.0, "URGENT");
        } else if (normalized.contains("SEC") || normalized.contains("SAFE")) {
            return new ProblemMetadata("Campus Security", "CV-SEC-", "Chief Proctor & Campus Security Division", 12, 9, 20.0, "HIGH");
        } else if (normalized.contains("WIFI") || normalized.contains("NET") || normalized.contains("IT")) {
            return new ProblemMetadata("IT & WiFi", "CV-NET-", "Campus Network & Cyber Infrastructure", 12, 8, 25.0, "HIGH");
        } else if (normalized.contains("LAB")) {
            return new ProblemMetadata("Laboratory", "CV-LAB-", "IT Lab & Technical Support", 24, 8, 25.0, "HIGH");
        } else if (normalized.contains("HOSTEL")) {
            return new ProblemMetadata("Hostel", "CV-HST-", "Hostel Administration & Warden Desk", 24, 8, 25.0, "HIGH");
        } else if (normalized.contains("BUS") || normalized.contains("TRANSPORT")) {
            return new ProblemMetadata("Transportation", "CV-BUS-", "Transport & Fleet Operations", 24, 6, 25.0, "MEDIUM");
        } else if (normalized.contains("LIB") || normalized.contains("BOOK")) {
            return new ProblemMetadata("Library", "CV-LIB-", "Central Library & Digital Knowledge Desk", 24, 5, 25.0, "MEDIUM");
        } else if (normalized.contains("CANTEEN") || normalized.contains("MESS")) {
            return new ProblemMetadata("Canteen", "CV-CAN-", "Canteen & Hygiene Committee", 36, 6, 30.0, "MEDIUM");
        } else if (normalized.contains("CLASS") || normalized.contains("ROOM")) {
            return new ProblemMetadata("Classroom", "CV-CLS-", "Academic Infrastructure & Maintenance", 48, 6, 30.0, "MEDIUM");
        } else if (normalized.contains("SPORT") || normalized.contains("GYM")) {
            return new ProblemMetadata("Sports & Gym", "CV-SPT-", "Sports Complex & Physical Education Dept", 48, 4, 30.0, "LOW");
        } else if (normalized.contains("FACULTY") || normalized.contains("ACADEMIC")) {
            return new ProblemMetadata("Academic", "CV-ACAD-", "Academic Dean & HOD Office", 48, 7, 30.0, "HIGH");
        } else if (normalized.contains("ADMIN") || normalized.contains("FEE")) {
            return new ProblemMetadata("Administration", "CV-ADM-", "Registrar & Student Welfare Office", 72, 4, 30.0, "LOW");
        } else {
            return new ProblemMetadata("General", "CV-INF-", "Campus General Maintenance Desk", 48, 5, 30.0, "MEDIUM");
        }
    }

    public String getCategory() {
        return category;
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public String getTargetDepartment() {
        return targetDepartment;
    }

    public int getSlaHours() {
        return slaHours;
    }

    public int getBaseSeverity() {
        return baseSeverity;
    }

    public double getMinParticipationThreshold() {
        return minParticipationThreshold;
    }

    public String getSuggestedPriority() {
        return suggestedPriority;
    }
}
