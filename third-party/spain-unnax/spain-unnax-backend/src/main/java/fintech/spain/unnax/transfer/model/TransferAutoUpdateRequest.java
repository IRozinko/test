package fintech.spain.unnax.transfer.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferAutoUpdateRequest {

    private Boolean retry;
    private Boolean cancel;

    public static TransferAutoUpdateRequest cancel() {
        return TransferAutoUpdateRequest.builder()
            .cancel(true)
            .build();
    }

    public static TransferAutoUpdateRequest retry() {
        return TransferAutoUpdateRequest.builder()
            .retry(true)
            .cancel(false)
            .build();
    }

}
