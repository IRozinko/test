package fintech.spain.inglobaly;

import fintech.spain.inglobaly.model.InglobalyQuery;
import fintech.spain.inglobaly.model.InglobalyRequest;
import fintech.spain.inglobaly.model.InglobalyResponse;

import java.util.Optional;

public interface InglobalyService {

    InglobalyResponse request(InglobalyRequest request);

    InglobalyResponse get(Long id);

    Optional<InglobalyResponse> findLatest(InglobalyQuery query);
}
