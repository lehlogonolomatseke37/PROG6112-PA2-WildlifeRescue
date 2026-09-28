package wildliferescue;

/**
 * Operations every rescue case type must support.
 * Any class that implements this interface promises to provide these three behaviours.
 */
public interface RescueOperations {

    /** Moves the case into "Rescue in Progress". */
    void startRescue();

    /** Moves the case into "Rescue Completed". */
    void completeRescue();

    /** Short summary: ID, type, species, ranger, priority, status, total cost. */
    String generateSummary();
}
