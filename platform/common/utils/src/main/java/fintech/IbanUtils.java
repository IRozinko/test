package fintech;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.StringUtils;
import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;

public class IbanUtils {

    public static boolean equals(String iban1, String iban2) {
        return StringUtils.equals(normalizeIban(iban1), normalizeIban(iban2));
    }

    public static String normalizeIban(String iban) {
        iban = StringUtils.replace(iban, " ", "");
        iban = StringUtils.upperCase(iban);
        return MoreObjects.firstNonNull(iban, "");
    }

    public static String extractNumber(String iban) {
        String account = normalizeIban(iban);
        if (account.length() > 4) {
            return account.substring(4);
        } else {
            return account;
        }
    }

    public static boolean isIbanValid(String iban) {
        try {
            iban = StringUtils.replace(iban, " ", "");
            iban = StringUtils.upperCase(iban);
            IbanUtil.validate(iban);
            return true;
        } catch (IbanFormatException | InvalidCheckDigitException | UnsupportedCountryException e) {
            return false;
        }
    }
}
