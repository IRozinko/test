package fintech.spain.alfa.product.payments.processors;

import fintech.spain.alfa.product.AlfaConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberMatcher {

    private static final String DNI_CODE = "[0-9]{8}[a-zA-Z]{1}";
    private static final String NIE_CODE = "[xyzXYZ]{1}[0-9]{7}[a-zA-Z]{1}";
    private static final String DNI_CODE_AND_NIE_CODE = DNI_CODE + "|" + NIE_CODE;
    private static final String DISBURSEMENT_MSG_ID = "\\d{14}_.{3}";

    public static Optional<String> extractClientDni(String details) {
        if (StringUtils.isBlank(details)) {
            return Optional.empty();
        }
        Pattern pattern = Pattern.compile(DNI_CODE_AND_NIE_CODE);
        String textToMatch = details.toUpperCase();
        Matcher matcher = pattern.matcher(textToMatch);
        if (matcher.find()) {
            return Optional.of(matcher.group(0).toUpperCase());
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> extractClientNumber(String details) {
        if (StringUtils.isBlank(details)) {
            return Optional.empty();
        }
        Pattern pattern = Pattern.compile("(" + AlfaConstants.CLIENT_NUMBER_PREFIX.toUpperCase() + "\\d{" + AlfaConstants.CLIENT_NUMBER_LENGTH + "})");
        String textToMatch = details.toUpperCase();
        Matcher matcher = pattern.matcher(textToMatch);
        if (matcher.find()) {
            return Optional.of(matcher.group(1).toUpperCase());
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> extractLoanNumber(String details) {
        if (StringUtils.isBlank(details)) {
            return Optional.empty();
        }
        Pattern pattern = Pattern.compile("(" + AlfaConstants.CLIENT_NUMBER_PREFIX.toUpperCase() + "\\d{" + AlfaConstants.CLIENT_NUMBER_LENGTH + "}-\\d{3})");
        String textToMatch = StringUtils.replace(details.toUpperCase(), "\n", "");
        Matcher matcher = pattern.matcher(textToMatch);
        if (matcher.find()) {
            return Optional.of(matcher.group(1).toUpperCase());
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> extractDisbursementReference(String details) {
        if (StringUtils.isBlank(details)) {
            return Optional.empty();
        }

        Pattern pattern = Pattern.compile("(" + AlfaConstants.DISBURSEMENT_REFERENCE_PREFIX + "\\d{" + AlfaConstants.DISBURSEMENT_REFERENCE_LENGTH + "}" + AlfaConstants.DISBURSEMENT_REFERENCE_ENDING + ")");
        String textToMatch = StringUtils.replace(details.toLowerCase(), "\n", "");
        Matcher matcher = pattern.matcher(textToMatch);
        if (matcher.find()) {
            return Optional.of(matcher.group(1).toLowerCase());
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> extractDisbursementMsgId(String details) {
        if (StringUtils.isBlank(details)) {
            return Optional.empty();
        }

        Pattern pattern = Pattern.compile(DISBURSEMENT_MSG_ID);
        String textToMatch = StringUtils.replace(details, "\n", "");
        Matcher matcher = pattern.matcher(textToMatch);
        if (matcher.find()) {
            return Optional.of(matcher.group(0).toUpperCase());
        } else {
            return Optional.empty();
        }
    }
}
