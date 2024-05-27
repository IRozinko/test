
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sumCollections3M",
    "numCollectionsTotal",
    "sumCollections6M",
    "numCollections1W",
    "sumCollections12M",
    "sumCollectionsTotal",
    "numCollections1M",
    "version",
    "numCollections6M",
    "sumCollections1M",
    "sumCollections1W",
    "numCollections12M",
    "numCollections3M"
})
@Data
public class Collections {

    private BigDecimal sumCollections3M;
    private BigDecimal numCollectionsTotal;
    private BigDecimal sumCollections6M;
    private BigDecimal numCollections1W;
    private BigDecimal sumCollections12M;
    private BigDecimal sumCollectionsTotal;
    private BigDecimal numCollections1M;
    private String version;
    private BigDecimal numCollections6M;
    private BigDecimal sumCollections1M;
    private BigDecimal sumCollections1W;
    private BigDecimal numCollections12M;
    private BigDecimal numCollections3M;
}
