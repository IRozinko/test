package fintech.viventor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ViventorConsumer {

    private String gender;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    private BigDecimal liabilities;

    private BigDecimal income;

    @JsonProperty("loan_count")
    private Integer loanCount;

    private Integer dependants;

    private String city;

    private String region;

    private String country;

    @JsonProperty("postal_code")
    private String postalCode;

}
