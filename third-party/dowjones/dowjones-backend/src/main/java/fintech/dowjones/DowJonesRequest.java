package fintech.dowjones;

import fintech.dowjones.model.search.name.NameSearchResult;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString(exclude = {"responseBody"})
public class DowJonesRequest {
    private Long id;
    private Long clientId;
    private DowJonesResponseStatus status;
    private String reason;
    private String error;
    private String requestBody;
    private String responseBody;
    private int responseStatusCode;
    private NameSearchResult nameSearchResult;
    private String riskIndicator;
    private String url;



}
