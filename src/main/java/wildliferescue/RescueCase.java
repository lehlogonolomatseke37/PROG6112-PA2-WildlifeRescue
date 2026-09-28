package wildliferescue;

/**
 * Superclass for every rescue case. It holds what ALL rescues share and leaves
 * the type-specific rules (cost, priority, type name) abstract so each subclass
 * must supply its own version.
 */
public abstract class RescueCase implements RescueOperations {

    private final String caseId;
    private final String animalName;
    private final String species;
    private final String location;
    private final String ranger;
    private final int rescueDays;
    private final double dailyCareCost;
    private RescueStatus status;

    protected RescueCase(String caseId, String animalName, String species, String location,
                         String ranger, int rescueDays, double dailyCareCost) {
        this.caseId = Validator.requireText(caseId, "Rescue Case ID");
        this.animalName = Validator.requireText(animalName, "Animal Name");
        this.species = Validator.requireText(species, "Species");
        this.location = Validator.requireText(location, "Rescue Location");
        this.ranger = Validator.requireText(ranger, "Assigned Ranger");
        this.rescueDays = Validator.requirePositive(rescueDays, "Number of Rescue Days");
        this.dailyCareCost = Validator.requirePositive(dailyCareCost, "Daily Care Cost");
        this.status = RescueStatus.REPORTED;
    }

    // ---- Abstract: every subclass MUST implement these ----

    public abstract String getRescueType();

    public abstract double calculateTotalCost();

    public abstract RescuePriority determinePriority();

    // ---- Shared behaviour ----

    /** Days x daily rate. Every subclass builds its total on top of this. */
    protected final double calculateBaseCareCost() {
        return rescueDays * dailyCareCost;
    }

    @Override
    public void startRescue() {
        if (status == RescueStatus.COMPLETED) {
            throw new IllegalStateException("Case " + caseId + " is already completed and cannot be restarted.");
        }
        if (status == RescueStatus.IN_PROGRESS) {
            throw new IllegalStateException("Case " + caseId + " is already in progress.");
        }
        status = RescueStatus.IN_PROGRESS;
    }

    @Override
    public void completeRescue() {
        if (status == RescueStatus.COMPLETED) {
            throw new IllegalStateException("Case " + caseId + " is already completed.");
        }
        if (status == RescueStatus.REPORTED) {
            throw new IllegalStateException("Case " + caseId + " must be started before it can be completed.");
        }
        status = RescueStatus.COMPLETED;
    }

    @Override
    public String generateSummary() {
        return line("Case ID", caseId)
                + line("Type", getRescueType())
                + line("Species", species)
                + line("Ranger", ranger)
                + line("Priority", determinePriority())
                + line("Status", status)
                + line("Total Cost", Money.format(calculateTotalCost()));
    }

    /** One case's block in the rescue report. */
    public String generateReportEntry() {
        return line("Case ID", caseId)
                + line("Type", getRescueType())
                + line("Species", species)
                + line("Location", location)
                + line("Ranger", ranger)
                + line("Priority", determinePriority())
                + line("Status", status)
                + line("Total Cost", Money.format(calculateTotalCost()));
    }

    /**
     * Full details. Subclasses override this, call super.getDetails(),
     * then append their own type-specific lines.
     */
    public String getDetails() {
        return line("Case ID", caseId)
                + line("Type", getRescueType())
                + line("Animal Name", animalName)
                + line("Species", species)
                + line("Location", location)
                + line("Ranger", ranger)
                + line("Rescue Days", rescueDays)
                + line("Daily Care Cost", Money.format(dailyCareCost))
                + line("Base Care Cost", Money.format(calculateBaseCareCost()))
                + line("Priority", determinePriority())
                + line("Status", status);
    }

    /** Lines up labels so the console output reads like a table. */
    protected static String line(String label, Object value) {
        return String.format("%-22s: %s%n", label, value);
    }

    // ---- Getters (encapsulation: fields are private, read through methods) ----

    public String getCaseId() {
        return caseId;
    }

    public String getAnimalName() {
        return animalName;
    }

    public String getSpecies() {
        return species;
    }

    public String getLocation() {
        return location;
    }

    public String getRanger() {
        return ranger;
    }

    public int getRescueDays() {
        return rescueDays;
    }

    public double getDailyCareCost() {
        return dailyCareCost;
    }

    public RescueStatus getStatus() {
        return status;
    }

    /** Manual status change (menu option 3). Validated: cannot be null. */
    public void setStatus(RescueStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be empty.");
        }
        this.status = status;
    }
}
