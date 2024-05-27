package fintech.nordigen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "partner",
    "info",
    "sum"
})
@Data
public class NordigenAccountTransaction {

    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("partner")
    private String partner;
    @JsonProperty("info")
    private String info;
    @JsonProperty("sum")
    private BigDecimal sum;
}
