package wildliferescue;

/** Urgency level. Each rescue type decides its own priority. */
public enum RescuePriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical");

    private final String label;

    RescuePriority(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
