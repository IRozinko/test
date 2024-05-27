package fintech.spain.unnax.charge;

import fintech.spain.unnax.charge.model.ChargeClientCardRequest;
import fintech.spain.unnax.model.UnnaxResponse;

import javax.validation.Valid;

public interface ChargeSavedCardUnnaxClient {

    UnnaxResponse<Void> charge(@Valid ChargeClientCardRequest request);

}
