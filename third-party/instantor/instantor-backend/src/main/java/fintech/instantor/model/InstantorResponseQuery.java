package fintech.instantor.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class InstantorResponseQuery {

    private Long clientId;
    private LocalDateTime createdAfter;
    private InstantorResponseStatus responseStatus;
    private String accountNumber;
    private List<InstantorProcessingStatus> processingStatus = new ArrayList<>();

    public static InstantorResponseQuery byClientIdAndResponseStatus(Long clientId, InstantorResponseStatus status) {
        InstantorResponseQuery responseQuery = new InstantorResponseQuery();
        responseQuery.setClientId(clientId);
        responseQuery.setResponseStatus(status);
        return responseQuery;
    }
}
