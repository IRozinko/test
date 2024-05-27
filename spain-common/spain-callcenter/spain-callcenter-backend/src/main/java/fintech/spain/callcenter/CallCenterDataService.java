package fintech.spain.callcenter;

import java.util.List;
import java.util.Optional;

public interface CallCenterDataService {

    void addCall(AddCallCommand command);

    void updateCallStatus(Long callId, CallStatus status);

    List<Call> find(CallQuery query);

    Optional<Call> findFirst(CallQuery query);
}
