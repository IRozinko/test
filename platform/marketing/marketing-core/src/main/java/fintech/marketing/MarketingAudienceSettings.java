package fintech.marketing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarketingAudienceSettings {

    private List<AudienceCondition> audienceConditions = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class AudienceCondition {
        private String type;
        private Map<String, Object> params = new HashMap<>();
    }

}
