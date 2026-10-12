package campusvoice.model;

/**
 * Peer voting options for class-level verification:
 * - FREQUENTLY: Weighted 1.0
 * - SOMETIMES: Weighted 0.5
 * - NEVER: Weighted 0.0
 */
public enum VoteOption {
    FREQUENTLY(1.0, "Frequently"),
    SOMETIMES(0.5, "Sometimes"),
    NEVER(0.0, "Never");

    private final double weight;
    private final String label;

    VoteOption(double weight, String label) {
        this.weight = weight;
        this.label = label;
    }

    public double getWeight() {
        return weight;
    }

    public String getLabel() {
        return label;
    }

    public static VoteOption fromString(String text) {
        if (text == null) return null;
        for (VoteOption opt : VoteOption.values()) {
            if (opt.name().equalsIgnoreCase(text) || opt.label.equalsIgnoreCase(text)) {
                return opt;
            }
        }
        return null;
    }
}
