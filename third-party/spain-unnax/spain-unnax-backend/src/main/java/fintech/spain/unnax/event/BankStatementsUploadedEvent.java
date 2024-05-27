package fintech.spain.unnax.event;

import fintech.spain.unnax.callback.model.BankStatementsUploadedData;
import fintech.spain.unnax.callback.model.CallbackRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankStatementsUploadedEvent extends CallbackEvent {

    private final String requestCode;
    private final String link;
    private final String errorCode;
    private final String errorMessage;

    public BankStatementsUploadedEvent(CallbackRequest request) {
        super(request.getResponseId());
        BankStatementsUploadedData data = request.getDataAsValue(BankStatementsUploadedData.class);
        this.requestCode = data.getRequestCode();
        this.link = data.getLink();
        this.errorCode = data.getErrorCode();
        this.errorMessage = data.getErrorMessage();
    }

}
