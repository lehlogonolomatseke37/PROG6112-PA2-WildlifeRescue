package wildliferescue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Rescue cost calculations for every rescue type, with and without the extra fee. */
class RescueCostTest {

    private static final double DELTA = 0.001;   // tolerance for comparing doubles

    @Test
    @DisplayName("Injured: care + vet + R5 000 surgery (matches brief example WR101 = R20 500)")
    void injuredWithSurgery() {
        RescueCase rc = new InjuredAnimalRescue("WR101", "Tembo", "African Elephant", "Kruger", "S. Nkosi",
                10, 500, "Snare wound", 10500, true);
        assertEquals(20500.00, rc.calculateTotalCost(), DELTA);   // 5000 + 10500 + 5000
    }

    @Test
    @DisplayName("Injured: no surgery fee when surgery is not required")
    void injuredWithoutSurgery() {
        RescueCase rc = new InjuredAnimalRescue("WR103", "Lulu", "Leopard", "Pilanesberg", "T. Dlamini",
                4, 250, "Leg sprain", 1200, false);
        assertEquals(2200.00, rc.calculateTotalCost(), DELTA);    // 1000 + 1200
    }

    @Test
    @DisplayName("Orphaned: care + feeding + R2 500 foster care (matches brief example WR102 = R11 500)")
    void orphanedWithFosterCare() {
        RescueCase rc = new OrphanedAnimalRescue("WR102", "Nandi", "White Rhino", "Hluhluwe", "P. Mokoena",
                10, 600, 8, 3000, true);
        assertEquals(11500.00, rc.calculateTotalCost(), DELTA);   // 6000 + 3000 + 2500
    }

    @Test
    @DisplayName("Orphaned: no foster fee when foster care is not required")
    void orphanedWithoutFosterCare() {
        RescueCase rc = new OrphanedAnimalRescue("WR104", "Kiki", "Vervet Monkey", "Durban", "L. Naidoo",
                5, 100, 14, 400, false);
        assertEquals(900.00, rc.calculateTotalCost(), DELTA);     // 500 + 400
    }

    @Test
    @DisplayName("Endangered: care + security + R8 000 specialist team")
    void endangeredWithSpecialistTeam() {
        RescueCase rc = new EndangeredSpeciesRescue("WR105", "Shadow", "Pangolin", "Limpopo", "J. Botha",
                5, 800, ConservationStatus.ENDANGERED, 6000, true);
        assertEquals(18000.00, rc.calculateTotalCost(), DELTA);   // 4000 + 6000 + 8000
    }

    @Test
    @DisplayName("Endangered: no specialist fee when team is not required")
    void endangeredWithoutSpecialistTeam() {
        RescueCase rc = new EndangeredSpeciesRescue("WR106", "Rex", "Wild Dog", "Madikwe", "K. Mahlangu",
                2, 300, ConservationStatus.VULNERABLE, 1500, false);
        assertEquals(2100.00, rc.calculateTotalCost(), DELTA);    // 600 + 1500
    }

    @Test
    @DisplayName("Money formats amounts the way the brief's example report does")
    void currencyFormat() {
        assertEquals("R20 500.00", Money.format(20500));
        assertEquals("R32 000.00", Money.format(32000));
    }
}
