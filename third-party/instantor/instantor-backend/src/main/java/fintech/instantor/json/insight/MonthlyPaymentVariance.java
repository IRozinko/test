
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "numMonthlyExpenseStreams",
    "maxNormStdMonthlyExpenseStreams",
    "maxStdMonthlyExpenseStreams",
    "avgNormStdMonthlyExpenseStreams",
    "avgStdMonthlyExpenseStreams",
    "avgStdTiming",
    "version",
    "maxStdTiming"
})
@Data
public class MonthlyPaymentVariance {

    private BigDecimal numMonthlyExpenseStreams;
    private BigDecimal maxNormStdMonthlyExpenseStreams;
    private BigDecimal maxStdMonthlyExpenseStreams;
    private BigDecimal avgNormStdMonthlyExpenseStreams;
    private BigDecimal avgStdMonthlyExpenseStreams;
    private BigDecimal avgStdTiming;
    private String version;
    private BigDecimal maxStdTiming;
}
