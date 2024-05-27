package fintech.spain.equifax.impl;

import fintech.PredicateBuilder;
import fintech.Validate;
import fintech.spain.equifax.EquifaxService;
import fintech.spain.equifax.db.EquifaxEntity;
import fintech.spain.equifax.db.EquifaxRepository;
import fintech.spain.equifax.mock.MockEquifaxProvider;
import fintech.spain.equifax.model.EquifaxQuery;
import fintech.spain.equifax.model.EquifaxRequest;
import fintech.spain.equifax.model.EquifaxResponse;
import fintech.spain.equifax.model.EquifaxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;

import static fintech.spain.equifax.db.Entities.equifax;

@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class EquifaxServiceBean implements EquifaxService {

    private final EquifaxRepository repository;

    @Resource(name = "${spain.equifax.provider:" + MockEquifaxProvider.NAME + "}")
    private EquifaxProvider provider;

    @Override
    public Optional<EquifaxResponse> findLatestResponse(EquifaxQuery query) {
        PredicateBuilder predicates = toPredicates(query);
        return repository.findFirst(predicates.allOf(), equifax.id.desc())
            .map(EquifaxEntity::toValueObject);
    }

    @Override
    public EquifaxResponse get(Long id) {
        return repository.getRequired(id).toValueObject();
    }

    @Override
    public EquifaxResponse request(EquifaxRequest request) {
        Validate.notBlank(request.getDocumentNumber(), "No document number");

        EquifaxResponse response = makeRequest(request);

        EquifaxEntity entity = new EquifaxEntity(response);
        entity.setClientId(request.getClientId());
        entity.setApplicationId(request.getApplicationId());
        entity.setDocumentNumber(request.getDocumentNumber());

        return repository.save(entity)
            .toValueObject();
    }

    private EquifaxResponse makeRequest(EquifaxRequest request) {
        try {
            return provider.request(request);
        } catch (Exception e) {
            log.error("Error during request to Equifax", e);
            return new EquifaxResponse()
                .setStatus(EquifaxStatus.ERROR)
                .setError(e.getMessage());
        }
    }

    private PredicateBuilder toPredicates(EquifaxQuery query) {
        return new PredicateBuilder()
            .addIfPresent(query.getClientId(), equifax.clientId::eq)
            .addIfPresent(query.getDocumentNumber(), equifax.documentNumber::eq)
            .addIfPresent(query.getCreatedAfter(), equifax.createdAt::after)
            .addIfPresent(query.getStatus(), equifax.status::in);
    }

}
