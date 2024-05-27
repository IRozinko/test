package fintech.viventor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PostLoanExtensionRequest {

    @JsonProperty("maturity_date")
    private LocalDate maturityDate;

}
