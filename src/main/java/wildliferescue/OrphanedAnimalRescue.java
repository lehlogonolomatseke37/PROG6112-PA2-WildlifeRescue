package wildliferescue;

public class OrphanedAnimalRescue extends RescueCase {

    public static final double FOSTER_CARE_FEE = 2500.00;
    public static final int NEWBORN_MONTHS = 3;
    public static final int YOUNG_MONTHS = 12;

    private final int estimatedAgeMonths;
    private final double feedingCost;
    private final boolean fosterCareRequired;

    public OrphanedAnimalRescue(String caseId, String animalName, String species, String location,
                                String ranger, int rescueDays, double dailyCareCost,
                                int estimatedAgeMonths, double feedingCost, boolean fosterCareRequired) {
        super(caseId, animalName, species, location, ranger, rescueDays, dailyCareCost);
        this.estimatedAgeMonths = Validator.requirePositive(estimatedAgeMonths, "Estimated Age (Months)");
        this.feedingCost = Validator.requirePositive(feedingCost, "Feeding Cost");
        this.fosterCareRequired = fosterCareRequired;
    }

    @Override
    public String getRescueType() {
        return "Orphaned Animal Rescue";
    }

    /** Care cost + feeding cost, plus R2 500 if foster care is required. */
    @Override
    public double calculateTotalCost() {
        double total = calculateBaseCareCost() + feedingCost;
        if (fosterCareRequired) {
            total += FOSTER_CARE_FEE;
        }
        return total;
    }

    /** Younger orphans are more vulnerable: under 3 months = Critical, under 12 = High. */
    @Override
    public RescuePriority determinePriority() {
        if (estimatedAgeMonths < NEWBORN_MONTHS) {
            return RescuePriority.CRITICAL;
        }
        if (estimatedAgeMonths < YOUNG_MONTHS) {
            return RescuePriority.HIGH;
        }
        return RescuePriority.MEDIUM;
    }

    @Override
    public String getDetails() {
        return super.getDetails()
                + line("Estimated Age (Months)", estimatedAgeMonths)
                + line("Feeding Cost", Money.format(feedingCost))
                + line("Foster Care Required", fosterCareRequired ? "Yes" : "No")
                + line("Total Cost", Money.format(calculateTotalCost()));
    }

    public int getEstimatedAgeMonths() {
        return estimatedAgeMonths;
    }

    public double getFeedingCost() {
        return feedingCost;
    }

    public boolean isFosterCareRequired() {
        return fosterCareRequired;
    }
}
