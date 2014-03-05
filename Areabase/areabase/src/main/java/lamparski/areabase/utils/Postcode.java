package lamparski.areabase.utils;

import java.util.Locale;
import java.util.regex.Pattern;

import javax.annotation.RegEx;

/**
 * Postcode validator.
 */
public class Postcode {
    /**
     * I found this pattern in the archives of the Cabinet Office, and it is verified
     * as working here: <a href="http://stackoverflow.com/a/164994">http://stackoverflow.com/a/164994</a>
     */
    @RegEx
    public static final String POSTCODE_REGEX = "(GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) [0-9][A-Z-[CIKMOV]]{2})";
    private static Pattern postcodePattern = Pattern.compile(POSTCODE_REGEX);

    /**
     * Checks the candidate postcode against the Royal Mail approved regex
     * @param postcode the postcode to validate
     * @return true if the postcode is valid
     */
    public static boolean isValid(String postcode){
        return postcodePattern.matcher(postcode.toUpperCase(Locale.UK)).matches();
    }
}
