package fintech.spain.web.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.Locale;

@Component
public class ApiLocalization {

    @Autowired
    private MessageSource messageSource;


    public String localizeErrorMessage(ObjectError error, Locale locale) {
        return localizeErrorMessage(error.getCodes(), error.getArguments(), error.getDefaultMessage(), locale);
    }

    public String localizeErrorMessage(String[] codes, Object[] arguments, String defaultMessage, Locale locale) {
        // String message = defaultMessage;
        String message = Arrays.toString(codes);
        for (String code : codes) {
            String localizedMessage = messageSource.getMessage(code, arguments, locale);
            if (!StringUtils.isBlank(localizedMessage)) {
                message = localizedMessage;
                break;
            }
        }
        return message;
    }
}
