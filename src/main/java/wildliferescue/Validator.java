package wildliferescue;

/**
 * One place for every validation rule. Both the model classes (constructors)
 * and the console UI call these, so the rules can never drift apart.
 */
public final class Validator {

    private Validator() {
        // utility class - no objects
    }

    /** Returns the trimmed text, or throws if it is null/blank. */
    public static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
        return value.trim();
    }

    public static double requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero.");
        }
        return value;
    }

    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero.");
        }
        return value;
    }
}
