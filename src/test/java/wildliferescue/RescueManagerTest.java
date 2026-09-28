package wildliferescue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Searching, duplicate prevention, status updates, validation and the report. */
class RescueManagerTest {

    private RescueManager manager;
    private RescueCase elephant;
    private RescueCase rhino;

    @BeforeEach
    void setUp() {   // runs before EVERY test, so each test starts from the same clean state
        manager = new RescueManager();
        elephant = new InjuredAnimalRescue("WR101", "Tembo", "African Elephant", "Kruger", "S. Nkosi",
                10, 500, "Snare wound", 10500, true);
        rhino = new OrphanedAnimalRescue("WR102", "Nandi", "White Rhino", "Hluhluwe", "P. Mokoena",
                10, 600, 8, 3000, true);
        manager.addCase(elephant);
        manager.addCase(rhino);
    }

    // ---- searching ----

    @Test
    @DisplayName("Search returns the exact case stored under that ID")
    void searchFindsExistingCase() {
        RescueCase found = manager.findById("WR102");
        assertNotNull(found);
        assertSame(rhino, found);
        assertEquals("White Rhino", found.getSpecies());
    }

    @Test
    @DisplayName("Search ignores letter case and surrounding spaces")
    void searchIsCaseInsensitive() {
        assertSame(elephant, manager.findById("  wr101 "));
    }

    @Test
    @DisplayName("Search for an unknown or blank ID returns null")
    void searchMissingCase() {
        assertNull(manager.findById("WR999"));
        assertNull(manager.findById(""));
    }

    // ---- duplicate IDs ----

    @Test
    @DisplayName("Adding a duplicate Rescue Case ID is rejected and the list is unchanged")
    void duplicateIdRejected() {
        RescueCase duplicate = new EndangeredSpeciesRescue("WR101", "Shadow", "Pangolin", "Limpopo", "J. Botha",
                5, 800, ConservationStatus.ENDANGERED, 6000, false);
        assertThrows(IllegalArgumentException.class, () -> manager.addCase(duplicate));
        assertEquals(2, manager.getCaseCount());
    }

    @Test
    @DisplayName("Duplicate check is case-insensitive (wr101 == WR101)")
    void duplicateIdDifferentCaseRejected() {
        RescueCase duplicate = new InjuredAnimalRescue("wr101", "X", "Lion", "Kruger", "Ranger",
                1, 100, "Cut", 100, false);
        assertThrows(IllegalArgumentException.class, () -> manager.addCase(duplicate));
        assertTrue(manager.idExists("wR101"));
    }

    // ---- status updates ----

    @Test
    @DisplayName("New cases start as Reported")
    void newCaseIsReported() {
        assertEquals(RescueStatus.REPORTED, elephant.getStatus());
    }

    @Test
    @DisplayName("startRescue -> In Progress, completeRescue -> Completed")
    void startThenComplete() {
        elephant.startRescue();
        assertEquals(RescueStatus.IN_PROGRESS, elephant.getStatus());
        elephant.completeRescue();
        assertEquals(RescueStatus.COMPLETED, elephant.getStatus());
    }

    @Test
    @DisplayName("Cannot complete a rescue that was never started")
    void cannotCompleteBeforeStart() {
        assertThrows(IllegalStateException.class, () -> rhino.completeRescue());
        assertEquals(RescueStatus.REPORTED, rhino.getStatus());
    }

    @Test
    @DisplayName("Cannot restart a completed rescue")
    void cannotRestartCompleted() {
        rhino.startRescue();
        rhino.completeRescue();
        assertThrows(IllegalStateException.class, () -> rhino.startRescue());
    }

    @Test
    @DisplayName("Manual status update through the manager")
    void managerUpdatesStatus() {
        assertTrue(manager.updateStatus("WR102", RescueStatus.UNDER_OBSERVATION));
        assertEquals(RescueStatus.UNDER_OBSERVATION, rhino.getStatus());
    }

    @Test
    @DisplayName("Updating an unknown ID returns false")
    void updateUnknownId() {
        assertFalse(manager.updateStatus("WR999", RescueStatus.COMPLETED));
    }

    // ---- validation ----

    @Test
    @DisplayName("Blank ID, species, location or ranger is rejected")
    void blankFieldsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new InjuredAnimalRescue(" ", "A", "Lion", "K", "R",
                1, 1, "Cut", 1, false));
        assertThrows(IllegalArgumentException.class, () -> new InjuredAnimalRescue("X1", "A", "", "K", "R",
                1, 1, "Cut", 1, false));
        assertThrows(IllegalArgumentException.class, () -> new InjuredAnimalRescue("X1", "A", "Lion", "", "R",
                1, 1, "Cut", 1, false));
        assertThrows(IllegalArgumentException.class, () -> new InjuredAnimalRescue("X1", "A", "Lion", "K", "",
                1, 1, "Cut", 1, false));
    }

    @Test
    @DisplayName("Zero or negative numbers are rejected")
    void nonPositiveNumbersRejected() {
        assertThrows(IllegalArgumentException.class, () -> new OrphanedAnimalRescue("X1", "A", "Rhino", "K", "R",
                0, 100, 5, 100, false));
        assertThrows(IllegalArgumentException.class, () -> new OrphanedAnimalRescue("X1", "A", "Rhino", "K", "R",
                1, -5, 5, 100, false));
        assertThrows(IllegalArgumentException.class, () -> new EndangeredSpeciesRescue("X1", "A", "Pangolin", "K",
                "R", 1, 100, ConservationStatus.ENDANGERED, 0, false));
    }

    // ---- summary ----

    @Test
    @DisplayName("Summary contains every field the brief requires, using the subclass's cost and priority")
    void summaryContainsRequiredFields() {
        elephant.startRescue();
        String summary = elephant.generateSummary();
        assertTrue(summary.contains("WR101"));
        assertTrue(summary.contains("Injured Animal Rescue"));
        assertTrue(summary.contains("African Elephant"));
        assertTrue(summary.contains("S. Nkosi"));
        assertTrue(summary.contains("Critical"));
        assertTrue(summary.contains("Rescue in Progress"));
        assertTrue(summary.contains("R20 500.00"));
    }

    // ---- report ----

    @Test
    @DisplayName("Report totals match the brief example: 2 cases, R32 000.00")
    void reportTotals() {
        assertEquals(2, manager.getCaseCount());
        assertEquals(32000.00, manager.getTotalRescueCost(), 0.001);

        String report = manager.generateReport();
        assertTrue(report.contains("WILDLIFE RESCUE REPORT"));
        assertTrue(report.contains("R32 000.00"));
        assertTrue(report.contains("Hluhluwe"));   // location is included in each entry
    }

    @Test
    @DisplayName("The list returned to callers cannot be modified directly")
    void listIsReadOnly() {
        assertThrows(UnsupportedOperationException.class, () -> manager.getAllCases().clear());
    }
}
