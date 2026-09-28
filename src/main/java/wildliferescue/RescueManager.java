package wildliferescue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns the ArrayList of rescue cases. All business rules (unique IDs, search,
 * status updates, report) live here - NOT in the console class - so they can
 * be unit tested without typing anything.
 */
public class RescueManager {

    private static final String DOUBLE_RULE = "=".repeat(50) + System.lineSeparator();
    private static final String SINGLE_RULE = "-".repeat(50) + System.lineSeparator();

    private final ArrayList<RescueCase> rescueCases = new ArrayList<>();

    /** Adds a case. Rejects null and duplicate IDs. */
    public void addCase(RescueCase rescueCase) {
        if (rescueCase == null) {
            throw new IllegalArgumentException("Rescue case cannot be empty.");
        }
        if (idExists(rescueCase.getCaseId())) {
            throw new IllegalArgumentException("Rescue Case ID '" + rescueCase.getCaseId() + "' already exists.");
        }
        rescueCases.add(rescueCase);
    }

    /** IDs are compared ignoring case, so "wr101" and "WR101" count as the same. */
    public boolean idExists(String caseId) {
        return findById(caseId) != null;
    }

    /** Returns the matching case, or null if none is found. */
    public RescueCase findById(String caseId) {
        if (caseId == null || caseId.isBlank()) {
            return null;
        }
        String target = caseId.trim();
        for (RescueCase rc : rescueCases) {
            if (rc.getCaseId().equalsIgnoreCase(target)) {
                return rc;
            }
        }
        return null;
    }

    /** Sets a new status. Returns false if the ID was not found. */
    public boolean updateStatus(String caseId, RescueStatus newStatus) {
        RescueCase rc = findById(caseId);
        if (rc == null) {
            return false;
        }
        rc.setStatus(newStatus);
        return true;
    }

    /** Read-only view so outside code cannot bypass addCase() validation. */
    public List<RescueCase> getAllCases() {
        return Collections.unmodifiableList(rescueCases);
    }

    public int getCaseCount() {
        return rescueCases.size();
    }

    public double getTotalRescueCost() {
        double total = 0;
        for (RescueCase rc : rescueCases) {
            total += rc.calculateTotalCost();   // polymorphism: each type runs its own formula
        }
        return total;
    }

    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(DOUBLE_RULE).append("WILDLIFE RESCUE REPORT").append(System.lineSeparator()).append(DOUBLE_RULE);

        if (rescueCases.isEmpty()) {
            sb.append("No rescue cases have been recorded.").append(System.lineSeparator());
        }
        for (int i = 0; i < rescueCases.size(); i++) {
            sb.append(System.lineSeparator()).append(rescueCases.get(i).generateReportEntry());
            if (i < rescueCases.size() - 1) {
                sb.append(System.lineSeparator()).append(SINGLE_RULE);
            }
        }

        sb.append(System.lineSeparator()).append(DOUBLE_RULE)
          .append(String.format("%-22s: %d%n", "Total Rescue Cases", getCaseCount()))
          .append(String.format("%-22s: %s%n", "Total Rescue Cost", Money.format(getTotalRescueCost())))
          .append(DOUBLE_RULE);
        return sb.toString();
    }
}
