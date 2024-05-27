
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "lastMonthSum(1000 to 2000)",
    "trend(0 to 5)",
    "lastMonthSum(50 to 100)",
    "lastMonthSum(-5 to 0)",
    "trend(20 to 50)",
    "trend(-200 to -100)",
    "lastMonthSum(2000 to inf)",
    "trend(500 to 1000)",
    "lastMonthSum(500 to 1000)",
    "lastMonthSum(0 to 5)",
    "lastMonthSum(20 to 50)",
    "lastMonthSum(-50 to -20)",
    "trend(-inf to -2000)",
    "trend(200 to 500)",
    "version",
    "trend(-1000 to -500)",
    "trend(-100 to -50)",
    "trend(-10 to -5)",
    "trend(10 to 20)",
    "trend(-2000 to -1000)",
    "lastMonthSum(10 to 20)",
    "lastMonthSum(-200 to -100)",
    "lastMonthSum(-2000 to -1000)",
    "lastMonthSum(200 to 500)",
    "lastMonthSum(-inf to -2000)",
    "trend(5 to 10)",
    "trend(-50 to -20)",
    "trend(100 to 200)",
    "lastMonthSum(5 to 10)",
    "lastMonthSum(100 to 200)",
    "trend(1000 to 2000)",
    "trend(50 to 100)",
    "trend(-500 to -200)",
    "lastMonthSum(-1000 to -500)",
    "trend(-20 to -10)",
    "lastMonthSum(-20 to -10)",
    "lastMonthSum(-500 to -200)",
    "trend(-5 to 0)",
    "lastMonthSum(-10 to -5)",
    "trend(2000 to inf)",
    "lastMonthSum(-100 to -50)"
})
@Data
public class SpendingDistribution {

    @JsonProperty("lastMonthSum(1000 to 2000)")
    private BigDecimal lastMonthSum_1000_to_2000;

    @JsonProperty("trend(0 to 5)")
    private BigDecimal trend_0_to_5;

    @JsonProperty("lastMonthSum(50 to 100)")
    private BigDecimal lastMonthSum_50_to_100;

    @JsonProperty("lastMonthSum(-5 to 0)")
    private BigDecimal lastMonthSum_minus_5_to_0;

    @JsonProperty("trend(20 to 50)")
    private BigDecimal trend_20_to_50;

    @JsonProperty("trend(-200 to -100)")
    private BigDecimal trend_minus_200_to_minus_100;

    @JsonProperty("lastMonthSum(2000 to inf)")
    private BigDecimal lastMonthSum_2000_to_inf;

    @JsonProperty("trend(500 to 1000)")
    private BigDecimal trend_500_to_1000;

    @JsonProperty("lastMonthSum(500 to 1000)")
    private BigDecimal lastMonthSum_500_to_1000;

    @JsonProperty("lastMonthSum(0 to 5)")
    private BigDecimal lastMonthSum_0_to_5;

    @JsonProperty("lastMonthSum(20 to 50)")
    private BigDecimal lastMonthSum_20_to_50;

    @JsonProperty("lastMonthSum(-50 to -20)")
    private BigDecimal lastMonthSum_minus_50_to_minus_20;

    @JsonProperty("trend(-inf to -2000)")
    private BigDecimal trend_minus_inf_to_minus_2000;

    @JsonProperty("trend(200 to 500)")
    private BigDecimal trend_200_to_500;

    @JsonProperty("version")
    private String version;

    @JsonProperty("trend(-1000 to -500)")
    private BigDecimal trend_minus_1000_to_minus_500;

    @JsonProperty("trend(-100 to -50)")
    private BigDecimal trend_minus_100_to_minus_50;

    @JsonProperty("trend(-10 to -5)")
    private BigDecimal trend_minus_10_to_minus_5;

    @JsonProperty("trend(10 to 20)")
    private BigDecimal trend_10_to_20;

    @JsonProperty("trend(-2000 to -1000)")
    private BigDecimal trend_minus_2000_to_minus_1000;

    @JsonProperty("lastMonthSum(10 to 20)")
    private BigDecimal lastMonthSum_10_to_20;

    @JsonProperty("lastMonthSum(-200 to -100)")
    private BigDecimal lastMonthSum_minus_200_to_minus_100;

    @JsonProperty("lastMonthSum(-2000 to -1000)")
    private BigDecimal lastMonthSum_minus_2000_to_minus_1000;

    @JsonProperty("lastMonthSum(200 to 500)")
    private BigDecimal lastMonthSum_200_to_500;

    @JsonProperty("lastMonthSum(-inf to -2000)")
    private BigDecimal lastMonthSum_minus_inf_to_minus_2000;

    @JsonProperty("trend(5 to 10)")
    private BigDecimal trend_5_to_10;

    @JsonProperty("trend(-50 to -20)")
    private BigDecimal trend_minus_50_to_minus_20;

    @JsonProperty("trend(100 to 200)")
    private BigDecimal trend_100_to_200;

    @JsonProperty("lastMonthSum(5 to 10)")
    private BigDecimal lastMonthSum_5_to_10;

    @JsonProperty("lastMonthSum(100 to 200)")
    private BigDecimal lastMonthSum_100_to_200;

    @JsonProperty("trend(1000 to 2000)")
    private BigDecimal trend_1000_to_2000;

    @JsonProperty("trend(50 to 100)")
    private BigDecimal trend_50_to_100;

    @JsonProperty("trend(-500 to -200)")
    private BigDecimal trend_minus_500_to_minus_200;

    @JsonProperty("lastMonthSum(-1000 to -500)")
    private BigDecimal lastMonthSum_minus_1000_to_minus_500;

    @JsonProperty("trend(-20 to -10)")
    private BigDecimal trend_minus_20_to_minus_10;

    @JsonProperty("lastMonthSum(-20 to -10)")
    private BigDecimal lastMonthSum_minus_20_to_minus_10;

    @JsonProperty("lastMonthSum(-500 to -200)")
    private BigDecimal lastMonthSum_minus_500_to_minus_200;

    @JsonProperty("trend(-5 to 0)")
    private BigDecimal trend_minus_5_to_0;

    @JsonProperty("lastMonthSum(-10 to -5)")
    private BigDecimal lastMonthSum_minus_10_to_minus_5;

    @JsonProperty("trend(2000 to inf)")
    private BigDecimal trend_2000_to_inf;

    @JsonProperty("lastMonthSum(-100 to -50)")
    private BigDecimal lastMonthSum_minus_100_to_minus_50;
}
