package fintech.spain.unnax.transfer;

import fintech.spain.unnax.model.TransferAutoUpdateResponse;
import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.transfer.model.TransferAutoDetails;
import fintech.spain.unnax.transfer.model.TransferAutoRequest;
import fintech.spain.unnax.transfer.model.TransferAutoResponse;
import fintech.spain.unnax.transfer.model.TransferAutoUpdateRequest;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface TransferAutoUnnaxClient {

    UnnaxResponse<TransferAutoResponse> transferAuto(@Valid TransferAutoRequest request);

    UnnaxResponse<TransferAutoDetails> getDetails(@NotBlank String orderCode);

    UnnaxResponse<TransferAutoUpdateResponse> update(String orderCode, TransferAutoUpdateRequest requestBody);
}
