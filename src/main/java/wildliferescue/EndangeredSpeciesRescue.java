package wildliferescue;

public class EndangeredSpeciesRescue extends RescueCase {

    public static final double SPECIALIST_TEAM_FEE = 8000.00;

    private final ConservationStatus classification;
    private final double securityCost;
    private final boolean specialistTeamRequired;

    public EndangeredSpeciesRescue(String caseId, String animalName, String species, String location,
                                   String ranger, int rescueDays, double dailyCareCost,
                                   ConservationStatus classification, double securityCost,
                                   boolean specialistTeamRequired) {
        super(caseId, animalName, species, location, ranger, rescueDays, dailyCareCost);
        if (classification == null) {
            throw new IllegalArgumentException("Conservation Classification cannot be blank.");
        }
        this.classification = classification;
        this.securityCost = Validator.requirePositive(securityCost, "Security Cost");
        this.specialistTeamRequired = specialistTeamRequired;
    }

    @Override
    public String getRescueType() {
        return "Endangered Species Rescue";
    }

    /** Care cost + security cost, plus R8 000 if a specialist team is required. */
    @Override
    public double calculateTotalCost() {
        double total = calculateBaseCareCost() + securityCost;
        if (specialistTeamRequired) {
            total += SPECIALIST_TEAM_FEE;
        }
        return total;
    }

    /**
     * Critically endangered or needing a specialist team = Critical;
     * Endangered = High; Vulnerable / Near Threatened = Medium.
     */
    @Override
    public RescuePriority determinePriority() {
        if (classification == ConservationStatus.CRITICALLY_ENDANGERED || specialistTeamRequired) {
            return RescuePriority.CRITICAL;
        }
        if (classification == ConservationStatus.ENDANGERED) {
            return RescuePriority.HIGH;
        }
        return RescuePriority.MEDIUM;
    }

    @Override
    public String getDetails() {
        return super.getDetails()
                + line("Classification", classification)
                + line("Security Cost", Money.format(securityCost))
                + line("Specialist Team", specialistTeamRequired ? "Yes" : "No")
                + line("Total Cost", Money.format(calculateTotalCost()));
    }

    public ConservationStatus getClassification() {
        return classification;
    }

    public double getSecurityCost() {
        return securityCost;
    }

    public boolean isSpecialistTeamRequired() {
        return specialistTeamRequired;
    }
}
