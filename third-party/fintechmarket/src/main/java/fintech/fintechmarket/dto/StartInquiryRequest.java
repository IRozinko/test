package fintech.fintechmarket.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StartInquiryRequest {

    private Data data;

    @lombok.Data
    @Accessors(chain = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Data {
        private String brandKey;
        private String personExternalId;
        private String entityExternalId;
        private Integer lockVersion;
        private Map<String, Object> fields;
    }

}
