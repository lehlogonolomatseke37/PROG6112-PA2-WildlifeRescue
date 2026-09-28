package wildliferescue;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Formats amounts the South African way: R20 500.00 */
public final class Money {

    private static final DecimalFormat FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        FORMAT = new DecimalFormat("#,##0.00", symbols);
    }

    private Money() {
    }

    public static String format(double amount) {
        return "R" + FORMAT.format(amount);
    }
}
