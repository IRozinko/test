package fintech.iovation.model;

import lombok.Data;

@Data
public class CheckTransactionCommand {

    private Long clientId;
    private Long applicationId;
    private String ipAddress;
    private String clientNumber;

}
