package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "stdBalance3M",
    "meanBalance1W",
    "maxBalance12M",
    "minBalance1M",
    "meanBalance6M",
    "minBalance3M",
    "maxBalance1M",
    "lastBalance",
    "maxBalance6M",
    "changeInFirstLastBalance",
    "stdBalance1W",
    "meanBalance12M",
    "meanBalance1M",
    "meanBalance3M",
    "maxBalance3M",
    "maxBalance1W",
    "version",
    "firstBalance",
    "meanBalanceTotal",
    "stdBalance1M",
    "stdBalance6M",
    "maxBalanceTotal",
    "minBalance6M",
    "minBalance12M",
    "stdBalance12M",
    "stdBalanceTotal",
    "minBalanceTotal",
    "minBalance1W"
})
@Data
public class Balances {

    @JsonProperty("stdBalance3M")
    public BigDecimal stdBalance3M;
    @JsonProperty("meanBalance1W")
    public BigDecimal meanBalance1W;
    @JsonProperty("maxBalance12M")
    public BigDecimal maxBalance12M;
    @JsonProperty("minBalance1M")
    public BigDecimal minBalance1M;
    @JsonProperty("meanBalance6M")
    public BigDecimal meanBalance6M;
    @JsonProperty("minBalance3M")
    public BigDecimal minBalance3M;
    @JsonProperty("maxBalance1M")
    public BigDecimal maxBalance1M;
    @JsonProperty("lastBalance")
    public Integer lastBalance;
    @JsonProperty("maxBalance6M")
    public BigDecimal maxBalance6M;
    @JsonProperty("changeInFirstLastBalance")
    public BigDecimal changeInFirstLastBalance;
    @JsonProperty("stdBalance1W")
    public BigDecimal stdBalance1W;
    @JsonProperty("meanBalance12M")
    public BigDecimal meanBalance12M;
    @JsonProperty("meanBalance1M")
    public BigDecimal meanBalance1M;
    @JsonProperty("meanBalance3M")
    public BigDecimal meanBalance3M;
    @JsonProperty("maxBalance3M")
    public BigDecimal maxBalance3M;
    @JsonProperty("maxBalance1W")
    public BigDecimal maxBalance1W;
    @JsonProperty("version")
    public String version;
    @JsonProperty("firstBalance")
    public BigDecimal firstBalance;
    @JsonProperty("meanBalanceTotal")
    public BigDecimal meanBalanceTotal;
    @JsonProperty("stdBalance1M")
    public BigDecimal stdBalance1M;
    @JsonProperty("stdBalance6M")
    public BigDecimal stdBalance6M;
    @JsonProperty("maxBalanceTotal")
    public BigDecimal maxBalanceTotal;
    @JsonProperty("minBalance6M")
    public BigDecimal minBalance6M;
    @JsonProperty("minBalance12M")
    public BigDecimal minBalance12M;
    @JsonProperty("stdBalance12M")
    public BigDecimal stdBalance12M;
    @JsonProperty("stdBalanceTotal")
    public BigDecimal stdBalanceTotal;
    @JsonProperty("minBalanceTotal")
    public BigDecimal minBalanceTotal;
    @JsonProperty("minBalance1W")
    public BigDecimal minBalance1W;

}
