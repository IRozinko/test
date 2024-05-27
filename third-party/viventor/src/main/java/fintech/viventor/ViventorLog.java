package fintech.viventor;

import lombok.Data;

@Data
public class ViventorLog {

    private Long id;
    private Long loanId;
    private ViventorRequestType requestType;
    private String requestBody;
    private String responseBody;
    private int responseStatusCode;
    private ViventorResponseStatus status;

    public boolean isOk() {
        return status == ViventorResponseStatus.OK;
    }

}
