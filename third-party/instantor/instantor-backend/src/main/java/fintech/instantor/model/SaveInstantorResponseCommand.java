package fintech.instantor.model;

import lombok.Data;
import lombok.ToString;

@ToString(exclude = {"payloadJson"})
@Data
public class SaveInstantorResponseCommand {

    private InstantorResponseStatus status;
    private String payloadJson;
    private String error;
    private String paramMessageId;
    private String paramSource;
    private String paramTimestamp;

    // mainly for testing purposes
    private Long clientId;
}
