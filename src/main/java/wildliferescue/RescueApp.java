package wildliferescue;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Console user interface. Its only job is reading input and printing output;
 * every rule is delegated to RescueManager / the model classes.
 */
public class RescueApp {

    private final RescueManager manager;
    private final Scanner in;
    private final PrintStream out;

    public RescueApp(RescueManager manager, Scanner in, PrintStream out) {
        this.manager = manager;
        this.in = in;
        this.out = out;
    }

    public static void main(String[] args) {
        new RescueApp(new RescueManager(), new Scanner(System.in), System.out).run();
    }

    public void run() {
        boolean running = true;
        try {
            while (running) {
                printMainMenu();
                int choice = readMenuChoice("Select an option: ", 1, 6);
                switch (choice) {
                    case 1 -> createRescueCase();
                    case 2 -> searchRescueCase();
                    case 3 -> updateRescueStatus();
                    case 4 -> displayAllRescueCases();
                    case 5 -> out.println(System.lineSeparator() + manager.generateReport());
                    case 6 -> running = false;
                    default -> out.println("Invalid option.");   // unreachable: readMenuChoice guards the range
                }
            }
        } catch (NoSuchElementException e) {
            // input stream closed (e.g. Ctrl+D) - fall through to a clean exit
        }
        out.println("Exiting Wildlife Rescue Operations System. Goodbye.");
    }

    // ---------------------------------------------------------------- menus

    private void printMainMenu() {
        String rule = "=".repeat(40);
        out.println();
        out.println(rule);
        out.println("WILDLIFE RESCUE OPERATIONS SYSTEM");
        out.println(rule);
        out.println();
        out.println("1. Create Rescue Case");
        out.println("2. Search Rescue Case");
        out.println("3. Update Rescue Status");
        out.println("4. Display All Rescue Cases");
        out.println("5. Rescue Report");
        out.println("6. Exit");
        out.println();
    }

    // ------------------------------------------------------------- option 1

    private void createRescueCase() {
        out.println();
        out.println("--- CREATE RESCUE CASE ---");
        out.println("1. Injured Animal Rescue");
        out.println("2. Orphaned Animal Rescue");
        out.println("3. Endangered Species Rescue");
        int type = readMenuChoice("Select rescue type: ", 1, 3);

        String id = readUniqueId();
        String name = readText("Animal Name: ", "Animal Name");
        String species = readText("Species: ", "Species");
        String location = readText("Rescue Location: ", "Rescue Location");
        String ranger = readText("Assigned Ranger: ", "Assigned Ranger");
        int days = readPositiveInt("Number of Rescue Days: ", "Number of Rescue Days");
        double daily = readPositiveDouble("Daily Care Cost (R): ", "Daily Care Cost");

        RescueCase rescueCase;   // declared as the SUPERCLASS type - polymorphism
        switch (type) {
            case 1 -> rescueCase = new InjuredAnimalRescue(id, name, species, location, ranger, days, daily,
                    readText("Injury Description: ", "Injury Description"),
                    readPositiveDouble("Veterinary Treatment Cost (R): ", "Veterinary Treatment Cost"),
                    readYesNo("Surgery Required? (Y/N): "));
            case 2 -> rescueCase = new OrphanedAnimalRescue(id, name, species, location, ranger, days, daily,
                    readPositiveInt("Estimated Age (Months): ", "Estimated Age"),
                    readPositiveDouble("Feeding Cost (R): ", "Feeding Cost"),
                    readYesNo("Foster Care Required? (Y/N): "));
            default -> rescueCase = new EndangeredSpeciesRescue(id, name, species, location, ranger, days, daily,
                    readClassification(),
                    readPositiveDouble("Security Cost (R): ", "Security Cost"),
                    readYesNo("Specialist Team Required? (Y/N): "));
        }

        manager.addCase(rescueCase);
        out.println();
        out.println("Rescue case " + rescueCase.getCaseId() + " created successfully.");
        out.print(rescueCase.generateSummary());
    }

    // ------------------------------------------------------------- option 2

    private void searchRescueCase() {
        RescueCase rc = findExistingCase();
        if (rc != null) {
            out.println();
            out.println("--- RESCUE CASE DETAILS ---");
            out.print(rc.getDetails());   // runs the subclass override
            out.println();
            out.println("--- RESCUE SUMMARY ---");
            out.print(rc.generateSummary());
        }
    }

    // ------------------------------------------------------------- option 3

    private void updateRescueStatus() {
        RescueCase rc = findExistingCase();
        if (rc == null) {
            return;
        }
        out.println("Current status: " + rc.getStatus());
        out.println("1. Start Rescue Operation");
        out.println("2. Complete Rescue Operation");
        out.println("3. Set Status Manually");
        int choice = readMenuChoice("Select an option: ", 1, 3);

        try {
            switch (choice) {
                case 1 -> rc.startRescue();
                case 2 -> rc.completeRescue();
                default -> manager.updateStatus(rc.getCaseId(), readStatus());
            }
            out.println();
            out.println("Status updated. Rescue summary:");
            out.print(rc.generateSummary());
        } catch (IllegalStateException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------- option 4

    private void displayAllRescueCases() {
        if (manager.getCaseCount() == 0) {
            out.println("No rescue cases have been recorded.");
            return;
        }
        for (RescueCase rc : manager.getAllCases()) {
            out.println("-".repeat(50));
            out.print(rc.getDetails());   // same call, different output per type
        }
        out.println("-".repeat(50));
    }

    // ------------------------------------------------------ input helpers

    private RescueCase findExistingCase() {
        if (manager.getCaseCount() == 0) {
            out.println("No rescue cases have been recorded yet.");
            return null;
        }
        String id = readText("Enter Rescue Case ID: ", "Rescue Case ID");
        RescueCase rc = manager.findById(id);
        if (rc == null) {
            out.println("Rescue case '" + id + "' was not found.");
        }
        return rc;
    }

    private String readUniqueId() {
        while (true) {
            String id = readText("Rescue Case ID: ", "Rescue Case ID");
            if (!manager.idExists(id)) {
                return id;
            }
            out.println("Error: Rescue Case ID '" + id + "' already exists. Enter a unique ID.");
        }
    }

    private String readText(String prompt, String field) {
        while (true) {
            out.print(prompt);
            try {
                return Validator.requireText(in.nextLine(), field);
            } catch (IllegalArgumentException e) {
                out.println("Error: " + e.getMessage());
            }
        }
    }

    private int readPositiveInt(String prompt, String field) {
        while (true) {
            out.print(prompt);
            try {
                return Validator.requirePositive(Integer.parseInt(in.nextLine().trim()), field);
            } catch (NumberFormatException e) {
                out.println("Error: " + field + " must be a whole number.");
            } catch (IllegalArgumentException e) {
                out.println("Error: " + e.getMessage());
            }
        }
    }

    private double readPositiveDouble(String prompt, String field) {
        while (true) {
            out.print(prompt);
            try {
                return Validator.requirePositive(Double.parseDouble(in.nextLine().trim()), field);
            } catch (NumberFormatException e) {
                out.println("Error: " + field + " must be a number (e.g. 1500 or 1500.50).");
            } catch (IllegalArgumentException e) {
                out.println("Error: " + e.getMessage());
            }
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            out.print(prompt);
            String answer = in.nextLine().trim().toUpperCase();
            if (answer.equals("Y") || answer.equals("YES")) {
                return true;
            }
            if (answer.equals("N") || answer.equals("NO")) {
                return false;
            }
            out.println("Error: please enter Y or N.");
        }
    }

    private int readMenuChoice(String prompt, int min, int max) {
        while (true) {
            out.print(prompt);
            try {
                int choice = Integer.parseInt(in.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // handled below
            }
            out.println("Error: invalid selection. Enter a number from " + min + " to " + max + ".");
        }
    }

    private ConservationStatus readClassification() {
        ConservationStatus[] options = ConservationStatus.values();
        out.println("Conservation Classification:");
        for (int i = 0; i < options.length; i++) {
            out.println((i + 1) + ". " + options[i]);
        }
        return options[readMenuChoice("Select classification: ", 1, options.length) - 1];
    }

    private RescueStatus readStatus() {
        RescueStatus[] options = RescueStatus.values();
        out.println("Available statuses:");
        for (int i = 0; i < options.length; i++) {
            out.println((i + 1) + ". " + options[i]);
        }
        return options[readMenuChoice("Select new status: ", 1, options.length) - 1];
    }
}
