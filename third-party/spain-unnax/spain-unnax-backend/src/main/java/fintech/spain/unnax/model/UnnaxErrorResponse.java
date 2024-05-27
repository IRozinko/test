package fintech.spain.unnax.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@NoArgsConstructor
public class UnnaxErrorResponse {

    public UnnaxErrorResponse(String detail) {
        this.detail = detail;
        this.code = "500";
    }

    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode data;
    private String detail;
    private String message;
    private String code;

}
