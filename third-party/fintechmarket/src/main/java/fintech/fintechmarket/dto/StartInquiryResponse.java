package fintech.fintechmarket.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StartInquiryResponse {

    private Data data;

    @lombok.Data
    @Accessors(chain = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Data {
        private String id;
        private String personExternalId;
        private String entityExternalId;
        private Map<String, Object> fields;
        private PrimaryResult primaryResult;
    }

    @lombok.Data
    @Accessors(chain = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Fields {
        private String internalFirstName;
        private String internalLastName;
    }

    @lombok.Data
    @Accessors(chain = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class PrimaryResult {

        private String decision;
        private String rating;
        private BigDecimal score;
        private List<String> arrayResult = null;
        private ScenarioVersion scenarioVersion;
        private Map<String, Object> scenarioVariablesResult;

    }

    @lombok.Data
    @Accessors(chain = true)
    public static class ScenarioVersion {
        private String id;
    }
}
