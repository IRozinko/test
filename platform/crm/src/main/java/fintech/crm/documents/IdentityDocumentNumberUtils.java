package fintech.crm.documents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentityDocumentNumberUtils {

    private static final String DNI_CODE = "[0-9]{8}[A-Z]{1}";
    private static final String NIE_CODE = "[XYZ]{1}[0-9]{7}[A-Z]{1}";
    public static final String DNI_LETTER_ASSOCIATION = "TRWAGMYFPDXBNJZSQVHLCKE";

    public static boolean isValidDniOrNie(final String identificationNumber) {
        return isValidDni(identificationNumber) || isValidNie(identificationNumber);
    }

    public static boolean isValidDni(final String identificationNumber) {
        return checkIdentificationNumberFormat(identificationNumber, DNI_CODE) && isValidDniLastLetter(identificationNumber);
    }

    public static boolean isValidNie(final String identificationNumber) {
        final char firstLetter = identificationNumber.charAt(0);
        String firstNumber;

        switch (firstLetter) {
            case 'X':
                firstNumber = "0";
                break;
            case 'Y':
                firstNumber = "1";
                break;
            case 'Z':
                firstNumber = "2";
                break;
            default:
                return false;
        }

        return checkIdentificationNumberFormat(identificationNumber, NIE_CODE) && isValidDni(firstNumber + identificationNumber.substring(1));
    }

    private static boolean isValidDniLastLetter(final String identificationNumber) {
        final int lastLetterIndex = identificationNumber.length() - 1;
        final char lastLetter = identificationNumber.charAt(lastLetterIndex);
        final int controlCode = Integer.parseInt(identificationNumber.substring(0, lastLetterIndex)) % 23;
        char charAt = DNI_LETTER_ASSOCIATION.charAt(controlCode);
        return charAt == lastLetter;
    }

    private static boolean checkIdentificationNumberFormat(final String identificationNumber, final String regularExpression) {
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(identificationNumber);
        return matcher.matches();
    }
}
