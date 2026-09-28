package wildliferescue;

public class InjuredAnimalRescue extends RescueCase {

    public static final double SURGERY_FEE = 5000.00;
    public static final double HIGH_VET_COST_THRESHOLD = 10000.00;

    private final String injuryDescription;
    private final double vetTreatmentCost;
    private final boolean surgeryRequired;

    public InjuredAnimalRescue(String caseId, String animalName, String species, String location,
                               String ranger, int rescueDays, double dailyCareCost,
                               String injuryDescription, double vetTreatmentCost, boolean surgeryRequired) {
        super(caseId, animalName, species, location, ranger, rescueDays, dailyCareCost);
        this.injuryDescription = Validator.requireText(injuryDescription, "Injury Description");
        this.vetTreatmentCost = Validator.requirePositive(vetTreatmentCost, "Veterinary Treatment Cost");
        this.surgeryRequired = surgeryRequired;
    }

    @Override
    public String getRescueType() {
        return "Injured Animal Rescue";
    }

    /** Care cost + vet cost, plus R5 000 if surgery is required. */
    @Override
    public double calculateTotalCost() {
        double total = calculateBaseCareCost() + vetTreatmentCost;
        if (surgeryRequired) {
            total += SURGERY_FEE;
        }
        return total;
    }

    /** Surgery = Critical; expensive treatment = High; otherwise Medium. */
    @Override
    public RescuePriority determinePriority() {
        if (surgeryRequired) {
            return RescuePriority.CRITICAL;
        }
        if (vetTreatmentCost >= HIGH_VET_COST_THRESHOLD) {
            return RescuePriority.HIGH;
        }
        return RescuePriority.MEDIUM;
    }

    @Override
    public String getDetails() {
        return super.getDetails()
                + line("Injury Description", injuryDescription)
                + line("Vet Treatment Cost", Money.format(vetTreatmentCost))
                + line("Surgery Required", surgeryRequired ? "Yes" : "No")
                + line("Total Cost", Money.format(calculateTotalCost()));
    }

    public String getInjuryDescription() {
        return injuryDescription;
    }

    public double getVetTreatmentCost() {
        return vetTreatmentCost;
    }

    public boolean isSurgeryRequired() {
        return surgeryRequired;
    }
}
