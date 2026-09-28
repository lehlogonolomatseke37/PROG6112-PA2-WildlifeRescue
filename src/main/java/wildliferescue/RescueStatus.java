package wildliferescue;

/** The fixed set of states a rescue case can be in. */
public enum RescueStatus {
    REPORTED("Reported"),
    IN_PROGRESS("Rescue in Progress"),
    UNDER_OBSERVATION("Under Observation"),
    IN_REHABILITATION("In Rehabilitation"),
    COMPLETED("Rescue Completed");

    private final String label;

    RescueStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
