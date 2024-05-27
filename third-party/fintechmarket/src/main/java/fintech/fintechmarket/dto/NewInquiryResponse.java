package fintech.fintechmarket.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NewInquiryResponse {

    private Data data;

    @lombok.Data
    @Accessors(chain = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Data {
        private Integer lockVersion;
        private Scenario scenario;
        private ScenarioVersion scenarioVersion;
        private List<Field> fields = null;
    }

    @lombok.Data
    @Accessors(chain = true)
    public static class Field {
        private String key;
    }

    @lombok.Data
    @Accessors(chain = true)
    public static class Scenario {
        private String key;
    }

    @lombok.Data
    @Accessors(chain = true)
    public static class ScenarioVersion {
        private String id;
    }
}
