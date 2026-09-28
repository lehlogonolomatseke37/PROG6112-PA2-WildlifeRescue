package wildliferescue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Each rescue type decides priority with its own rules - test every branch. */
class RescuePriorityTest {

    private static InjuredAnimalRescue injured(double vetCost, boolean surgery) {
        return new InjuredAnimalRescue("I1", "A", "Lion", "Kruger", "Ranger", 1, 100, "Wound", vetCost, surgery);
    }

    private static OrphanedAnimalRescue orphan(int ageMonths) {
        return new OrphanedAnimalRescue("O1", "B", "Rhino", "Hluhluwe", "Ranger", 1, 100, ageMonths, 100, false);
    }

    private static EndangeredSpeciesRescue endangered(ConservationStatus cs, boolean team) {
        return new EndangeredSpeciesRescue("E1", "C", "Pangolin", "Limpopo", "Ranger", 1, 100, cs, 100, team);
    }

    @Test
    @DisplayName("Injured: surgery required -> Critical")
    void injuredSurgeryIsCritical() {
        assertEquals(RescuePriority.CRITICAL, injured(500, true).determinePriority());
    }

    @Test
    @DisplayName("Injured: vet cost at/above R10 000 without surgery -> High")
    void injuredExpensiveIsHigh() {
        assertEquals(RescuePriority.HIGH, injured(10000, false).determinePriority());
    }

    @Test
    @DisplayName("Injured: minor treatment -> Medium")
    void injuredMinorIsMedium() {
        assertEquals(RescuePriority.MEDIUM, injured(9999.99, false).determinePriority());
    }

    @Test
    @DisplayName("Orphaned: under 3 months -> Critical, under 12 -> High, older -> Medium")
    void orphanPriorityByAge() {
        assertEquals(RescuePriority.CRITICAL, orphan(2).determinePriority());
        assertEquals(RescuePriority.HIGH, orphan(3).determinePriority());
        assertEquals(RescuePriority.HIGH, orphan(11).determinePriority());
        assertEquals(RescuePriority.MEDIUM, orphan(12).determinePriority());
    }

    @Test
    @DisplayName("Endangered: critically endangered OR specialist team -> Critical")
    void endangeredCritical() {
        assertEquals(RescuePriority.CRITICAL,
                endangered(ConservationStatus.CRITICALLY_ENDANGERED, false).determinePriority());
        assertEquals(RescuePriority.CRITICAL,
                endangered(ConservationStatus.VULNERABLE, true).determinePriority());
    }

    @Test
    @DisplayName("Endangered: Endangered -> High, Vulnerable -> Medium")
    void endangeredHighAndMedium() {
        assertEquals(RescuePriority.HIGH, endangered(ConservationStatus.ENDANGERED, false).determinePriority());
        assertEquals(RescuePriority.MEDIUM, endangered(ConservationStatus.VULNERABLE, false).determinePriority());
    }
}
