package fintech.spain.crosscheck.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SpainCrosscheckResult {

    private Long id;
    private String dni;
    private Long clientId;
    private Long loanId;
    private Long applicationId;
    private SpainCrosscheckStatus status;
    private Long maxDpd;
    private Long openLoans;
    private boolean blacklisted;
    private boolean repeatedClient;
    private boolean activeRequest;
    private String activeRequestStatus;
    private String responseBody;
    private int responseStatusCode;
    private String error;
}
