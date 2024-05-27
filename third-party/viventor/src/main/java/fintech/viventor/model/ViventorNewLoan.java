package fintech.viventor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ViventorNewLoan {

    private String id;

    private ViventorLoanType type;

    private String currency;

    private BigDecimal amount;

    @JsonProperty("interest_rate")
    private BigDecimal interestRate;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("maturity_date")
    private LocalDate maturityDate;

    @JsonProperty("country_code")
    private String countryCode;

    private String purpose;

    private boolean buyback;

    @JsonProperty("payment_guarantee")
    private boolean paymentGuarantee;

}
