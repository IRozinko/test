package fintech.spain.inglobaly.model;

import lombok.Data;

@Data
public class InglobalyRequest {

    private Long clientId;
    private Long applicationId;
    private String documentNumber;

}
