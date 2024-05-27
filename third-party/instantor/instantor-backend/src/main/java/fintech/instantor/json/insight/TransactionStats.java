package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "numIncTrns12M",
    "numUniqueIncTrns1W",
    "numTrnsTotal",
    "numUniqueTrns1W",
    "numUniqueIncTrns1M",
    "meanIncAmount1W",
    "sumAmounts12M",
    "numUniqueOutTrnsTotal",
    "meanAmount1M",
    "meanIncAmount1M",
    "numUniqueTrns3M",
    "sumAmounts3M",
    "maxIncAmount1M",
    "numUniqueOutTrns1W",
    "meanAmount1W",
    "meanOutAmount3M",
    "numOutTrns12M",
    "meanAmountTotal",
    "numOutTrns6M",
    "sumIncAmounts1M",
    "sumOutAmounts3M",
    "meanOutAmount6M",
    "maxIncAmount1W",
    "sumOutAmountsTotal",
    "numTrns3M",
    "sumOutAmounts12M",
    "numTrns12M",
    "sumIncAmountsTotal",
    "maxOutAmount1M",
    "maxIncAmount3M",
    "sumIncAmounts12M",
    "sumAmountsTotal",
    "meanAmount12M",
    "numIncTrns3M",
    "numUniqueOutTrns12M",
    "numTrns6M",
    "meanOutAmount1W",
    "numIncTrnsTotal",
    "meanOutAmount12M",
    "numIncTrns1M",
    "version",
    "numUniqueOutTrns3M",
    "meanAmount6M",
    "meanOutAmountTotal",
    "numOutTrns1M",
    "numIncTrns1W",
    "numTrns1W",
    "maxOutAmount1W",
    "maxOutAmount12M",
    "numUniqueOutTrns6M",
    "sumIncAmounts3M",
    "numOutTrns1W",
    "meanIncAmount6M",
    "meanOutAmount1M",
    "sumIncAmounts1W",
    "numUniqueTrns12M",
    "numOutTrns3M",
    "numUniqueTrns6M",
    "meanIncAmount12M",
    "maxIncAmount12M",
    "numUniqueIncTrns3M",
    "sumOutAmounts6M",
    "sumAmounts1M",
    "sumAmounts6M",
    "maxIncAmount6M",
    "numUniqueTrns1M",
    "meanIncAmount3M",
    "numOutTrnsTotal",
    "sumIncAmounts6M",
    "numUniqueOutTrns1M",
    "meanIncAmountTotal",
    "sumOutAmounts1M",
    "maxOutAmount3M",
    "maxOutAmount6M",
    "sumAmounts1W",
    "numUniqueTrnsTotal",
    "maxIncAmountTotal",
    "numUniqueIncTrns6M",
    "numIncTrns6M",
    "sumOutAmounts1W",
    "maxOutAmountTotal",
    "numUniqueIncTrns12M",
    "meanAmount3M",
    "numUniqueIncTrnsTotal",
    "numTrns1M"
})
@Data
public class TransactionStats {

    @JsonProperty("numIncTrns12M")
    public BigDecimal numIncTrns12M;
    @JsonProperty("numUniqueIncTrns1W")
    public Integer numUniqueIncTrns1W;
    @JsonProperty("numTrnsTotal")
    public Integer numTrnsTotal;
    @JsonProperty("numUniqueTrns1W")
    public Integer numUniqueTrns1W;
    @JsonProperty("numUniqueIncTrns1M")
    public Integer numUniqueIncTrns1M;
    @JsonProperty("meanIncAmount1W")
    public BigDecimal meanIncAmount1W;
    @JsonProperty("sumAmounts12M")
    public BigDecimal sumAmounts12M;
    @JsonProperty("numUniqueOutTrnsTotal")
    public Integer numUniqueOutTrnsTotal;
    @JsonProperty("meanAmount1M")
    public BigDecimal meanAmount1M;
    @JsonProperty("meanIncAmount1M")
    public BigDecimal meanIncAmount1M;
    @JsonProperty("numUniqueTrns3M")
    public Integer numUniqueTrns3M;
    @JsonProperty("sumAmounts3M")
    public BigDecimal sumAmounts3M;
    @JsonProperty("maxIncAmount1M")
    public BigDecimal maxIncAmount1M;
    @JsonProperty("numUniqueOutTrns1W")
    public Integer numUniqueOutTrns1W;
    @JsonProperty("meanAmount1W")
    public BigDecimal meanAmount1W;
    @JsonProperty("meanOutAmount3M")
    public BigDecimal meanOutAmount3M;
    @JsonProperty("numOutTrns12M")
    public BigDecimal numOutTrns12M;
    @JsonProperty("meanAmountTotal")
    public BigDecimal meanAmountTotal;
    @JsonProperty("numOutTrns6M")
    public BigDecimal numOutTrns6M;
    @JsonProperty("sumIncAmounts1M")
    public BigDecimal sumIncAmounts1M;
    @JsonProperty("sumOutAmounts3M")
    public BigDecimal sumOutAmounts3M;
    @JsonProperty("meanOutAmount6M")
    public BigDecimal meanOutAmount6M;
    @JsonProperty("maxIncAmount1W")
    public BigDecimal maxIncAmount1W;
    @JsonProperty("sumOutAmountsTotal")
    public BigDecimal sumOutAmountsTotal;
    @JsonProperty("numTrns3M")
    public Integer numTrns3M;
    @JsonProperty("sumOutAmounts12M")
    public BigDecimal sumOutAmounts12M;
    @JsonProperty("numTrns12M")
    public Integer numTrns12M;
    @JsonProperty("sumIncAmountsTotal")
    public BigDecimal sumIncAmountsTotal;
    @JsonProperty("maxOutAmount1M")
    public BigDecimal maxOutAmount1M;
    @JsonProperty("maxIncAmount3M")
    public BigDecimal maxIncAmount3M;
    @JsonProperty("sumIncAmounts12M")
    public BigDecimal sumIncAmounts12M;
    @JsonProperty("sumAmountsTotal")
    public BigDecimal sumAmountsTotal;
    @JsonProperty("meanAmount12M")
    public BigDecimal meanAmount12M;
    @JsonProperty("numIncTrns3M")
    public BigDecimal numIncTrns3M;
    @JsonProperty("numUniqueOutTrns12M")
    public Integer numUniqueOutTrns12M;
    @JsonProperty("numTrns6M")
    public Integer numTrns6M;
    @JsonProperty("meanOutAmount1W")
    public BigDecimal meanOutAmount1W;
    @JsonProperty("numIncTrnsTotal")
    public BigDecimal numIncTrnsTotal;
    @JsonProperty("meanOutAmount12M")
    public BigDecimal meanOutAmount12M;
    @JsonProperty("numIncTrns1M")
    public BigDecimal numIncTrns1M;
    @JsonProperty("version")
    public String version;
    @JsonProperty("numUniqueOutTrns3M")
    public Integer numUniqueOutTrns3M;
    @JsonProperty("meanAmount6M")
    public BigDecimal meanAmount6M;
    @JsonProperty("meanOutAmountTotal")
    public BigDecimal meanOutAmountTotal;
    @JsonProperty("numOutTrns1M")
    public BigDecimal numOutTrns1M;
    @JsonProperty("numIncTrns1W")
    public BigDecimal numIncTrns1W;
    @JsonProperty("numTrns1W")
    public Integer numTrns1W;
    @JsonProperty("maxOutAmount1W")
    public BigDecimal maxOutAmount1W;
    @JsonProperty("maxOutAmount12M")
    public BigDecimal maxOutAmount12M;
    @JsonProperty("numUniqueOutTrns6M")
    public Integer numUniqueOutTrns6M;
    @JsonProperty("sumIncAmounts3M")
    public BigDecimal sumIncAmounts3M;
    @JsonProperty("numOutTrns1W")
    public BigDecimal numOutTrns1W;
    @JsonProperty("meanIncAmount6M")
    public BigDecimal meanIncAmount6M;
    @JsonProperty("meanOutAmount1M")
    public BigDecimal meanOutAmount1M;
    @JsonProperty("sumIncAmounts1W")
    public BigDecimal sumIncAmounts1W;
    @JsonProperty("numUniqueTrns12M")
    public Integer numUniqueTrns12M;
    @JsonProperty("numOutTrns3M")
    public BigDecimal numOutTrns3M;
    @JsonProperty("numUniqueTrns6M")
    public Integer numUniqueTrns6M;
    @JsonProperty("meanIncAmount12M")
    public BigDecimal meanIncAmount12M;
    @JsonProperty("maxIncAmount12M")
    public BigDecimal maxIncAmount12M;
    @JsonProperty("numUniqueIncTrns3M")
    public Integer numUniqueIncTrns3M;
    @JsonProperty("sumOutAmounts6M")
    public BigDecimal sumOutAmounts6M;
    @JsonProperty("sumAmounts1M")
    public BigDecimal sumAmounts1M;
    @JsonProperty("sumAmounts6M")
    public BigDecimal sumAmounts6M;
    @JsonProperty("maxIncAmount6M")
    public BigDecimal maxIncAmount6M;
    @JsonProperty("numUniqueTrns1M")
    public Integer numUniqueTrns1M;
    @JsonProperty("meanIncAmount3M")
    public BigDecimal meanIncAmount3M;
    @JsonProperty("numOutTrnsTotal")
    public BigDecimal numOutTrnsTotal;
    @JsonProperty("sumIncAmounts6M")
    public BigDecimal sumIncAmounts6M;
    @JsonProperty("numUniqueOutTrns1M")
    public Integer numUniqueOutTrns1M;
    @JsonProperty("meanIncAmountTotal")
    public BigDecimal meanIncAmountTotal;
    @JsonProperty("sumOutAmounts1M")
    public BigDecimal sumOutAmounts1M;
    @JsonProperty("maxOutAmount3M")
    public BigDecimal maxOutAmount3M;
    @JsonProperty("maxOutAmount6M")
    public BigDecimal maxOutAmount6M;
    @JsonProperty("sumAmounts1W")
    public BigDecimal sumAmounts1W;
    @JsonProperty("numUniqueTrnsTotal")
    public Integer numUniqueTrnsTotal;
    @JsonProperty("maxIncAmountTotal")
    public BigDecimal maxIncAmountTotal;
    @JsonProperty("numUniqueIncTrns6M")
    public Integer numUniqueIncTrns6M;
    @JsonProperty("numIncTrns6M")
    public BigDecimal numIncTrns6M;
    @JsonProperty("sumOutAmounts1W")
    public BigDecimal sumOutAmounts1W;
    @JsonProperty("maxOutAmountTotal")
    public BigDecimal maxOutAmountTotal;
    @JsonProperty("numUniqueIncTrns12M")
    public Integer numUniqueIncTrns12M;
    @JsonProperty("meanAmount3M")
    public BigDecimal meanAmount3M;
    @JsonProperty("numUniqueIncTrnsTotal")
    public Integer numUniqueIncTrnsTotal;
    @JsonProperty("numTrns1M")
    public Integer numTrns1M;

}
