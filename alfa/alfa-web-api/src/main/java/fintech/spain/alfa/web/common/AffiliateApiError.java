package fintech.spain.alfa.web.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class AffiliateApiError {

    String message = "Form Affiliate Not Valid";

    Map<String, List<String>> errors;

    @JsonProperty("status_code")
    int statusCode = 422;
}
