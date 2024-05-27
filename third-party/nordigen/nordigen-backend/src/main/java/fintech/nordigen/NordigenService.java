package fintech.nordigen;

import fintech.nordigen.model.NordigenQuery;
import fintech.nordigen.model.NordigenRequestCommand;
import fintech.nordigen.model.NordigenResult;

import java.util.Optional;

public interface NordigenService {

    NordigenResult request(NordigenRequestCommand command);

    Optional<NordigenResult> findLatest(NordigenQuery query);

    NordigenResult get(Long id);
}
