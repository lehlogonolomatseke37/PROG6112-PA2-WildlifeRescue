package wildliferescue;

/** IUCN-style conservation classifications used for endangered species rescues. */
public enum ConservationStatus {
    CRITICALLY_ENDANGERED("Critically Endangered"),
    ENDANGERED("Endangered"),
    VULNERABLE("Vulnerable"),
    NEAR_THREATENED("Near Threatened");

    private final String label;

    ConservationStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
