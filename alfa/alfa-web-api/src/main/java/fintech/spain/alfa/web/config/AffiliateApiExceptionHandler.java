package fintech.spain.alfa.web.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fintech.affiliate.AffiliateService;
import fintech.affiliate.model.SaveAffiliateRequestCommand;
import fintech.spain.alfa.web.common.AffiliateApiError;
//import fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi;
import fintech.spain.web.common.ApiLocalization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
//@RestControllerAdvice(assignableTypes = AffiliateRegistrationApi.class)
@Order(-1)
public class AffiliateApiExceptionHandler {

    private static final Map<String, String> FIELD_MAPPING = ImmutableMap.<String, String>builder()
        .put("amount", "principal")
        .put("firstName", "name")
        .put("lastName", "surname")
        .put("documentNumber", "id_doc_number")
        .put("dateOfBirth", "birth_date")
        .put("postalCode", "zipcode")
        .put("mobilePhone", "phone")
        .put("otherPhone", "other_phone")
        .put("iban", "IBAN")
        .put("applicationUuid", "request_id")
        .build();

    @Autowired
    private ApiLocalization apiLocalization;

    @Autowired
    private AffiliateService affiliateService;

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AffiliateApiError methodArgumentNotValid(MethodArgumentNotValidException e, Locale locale) {
        Map<String, List<String>> messages = Maps.newHashMap();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            String field = FIELD_MAPPING.getOrDefault(fieldError.getField(), fieldError.getField());
            messages.put(field, messages.getOrDefault(field, Lists.newArrayList()));
            messages.get(field).add(localizeErrorMessage(fieldError, locale));
        }

        AffiliateApiError response = new AffiliateApiError(messages);

        affiliateService.saveAffiliateRequest(new SaveAffiliateRequestCommand()
            .setRequestType(e.getParameter().getMethod().getName())
            .setRequest(e.getBindingResult().getTarget())
            .setResponse(response));

        return response;
    }

    private String localizeErrorMessage(FieldError fieldError, Locale locale) {
        return apiLocalization.localizeErrorMessage(fieldError, locale);
    }
}
