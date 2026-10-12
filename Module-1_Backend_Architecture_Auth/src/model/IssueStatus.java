package campusvoice.model;

/**
 * Lifecycle states of an issue according to CampusVoice specifications:
 * REPORTED -> UNDER_VERIFICATION -> CLASS_VERIFIED -> UNDER_INVESTIGATION -> RESOLVED -> REOPENED
 */
public enum IssueStatus {
    REPORTED("Reported"),
    UNDER_VERIFICATION("Under Verification"),
    CLASS_VERIFIED("Class Verified"),
    UNDER_INVESTIGATION("Under Investigation"),
    RESOLVED("Resolved"),
    REOPENED("Reopened");

    private final String displayName;

    IssueStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static IssueStatus fromString(String text) {
        if (text == null) return null;
        for (IssueStatus status : IssueStatus.values()) {
            if (status.name().equalsIgnoreCase(text) || status.displayName.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return null;
    }
}
