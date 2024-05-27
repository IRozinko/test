package fintech.spain.callcenter.impl;

import com.querydsl.core.types.Predicate;
import fintech.spain.callcenter.AddCallCommand;
import fintech.spain.callcenter.Call;
import fintech.spain.callcenter.CallCenterDataService;
import fintech.spain.callcenter.CallQuery;
import fintech.spain.callcenter.CallStatus;
import fintech.spain.callcenter.db.CallEntity;
import fintech.spain.callcenter.db.CallRepository;
import fintech.spain.callcenter.db.Entities;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.allOf;

@Component
@Transactional
public class CallCenterDataServiceBean implements CallCenterDataService {

    private final CallRepository callRepository;

    public CallCenterDataServiceBean(CallRepository callRepository) {
        this.callRepository = callRepository;
    }

    @Override
    public void addCall(AddCallCommand command) {
        CallEntity entity = new CallEntity();
        entity.setProviderId(command.getProviderCallId());
        entity.setClientId(command.getClientId());
        entity.setStatus(command.getStatus());
        callRepository.save(entity);
    }

    @Override
    public void updateCallStatus(Long callId, CallStatus status) {
        CallEntity entity = callRepository.getRequired(callId);
        entity.setStatus(status);
    }

    @Override
    public List<Call> find(CallQuery query) {
        return callRepository.findAll(allOf(toPredicates(query))).stream()
            .map(CallEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Call> findFirst(CallQuery query) {
        return callRepository.getOptional(allOf(toPredicates(query)))
            .map(CallEntity::toValueObject);
    }

    private List<Predicate> toPredicates(CallQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getClientId() != null) {
            predicates.add(Entities.call.clientId.eq(query.getClientId()));
        }
        if (query.getProviderId() != null) {
            predicates.add(Entities.call.providerId.eq(query.getProviderId()));
        }
        if (!query.getStatuses().isEmpty()) {
            predicates.add(Entities.call.status.in(query.getStatuses()));
        }
        return predicates;
    }
}
