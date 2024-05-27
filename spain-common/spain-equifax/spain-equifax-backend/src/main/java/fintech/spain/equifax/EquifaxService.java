package fintech.spain.equifax;

import fintech.spain.equifax.model.EquifaxQuery;
import fintech.spain.equifax.model.EquifaxRequest;
import fintech.spain.equifax.model.EquifaxResponse;

import java.util.Optional;

public interface EquifaxService {

    Optional<EquifaxResponse> findLatestResponse(EquifaxQuery query);

    EquifaxResponse request(EquifaxRequest request);

    EquifaxResponse get(Long id);
}
