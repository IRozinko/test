package fintech.nordigen.impl;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.querydsl.core.types.Predicate;
import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.nordigen.NordigenService;
import fintech.nordigen.db.Entities;
import fintech.nordigen.db.NordigenLogEntity;
import fintech.nordigen.db.NordigenLogRepository;
import fintech.nordigen.model.NordigenQuery;
import fintech.nordigen.model.NordigenRequestCommand;
import fintech.nordigen.model.NordigenResult;
import fintech.nordigen.model.NordigenStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.querydsl.core.types.ExpressionUtils.allOf;

@Slf4j
@Transactional
@Component
public class NordigenServiceBean implements NordigenService {

    @Autowired
    private NordigenLogRepository repository;

    @Resource(name = "${nordigen.provider:" + MockNordigenProvider.NAME + "}")
    private NordigenProvider provider;

    @Override
    public NordigenResult request(NordigenRequestCommand command) {
        log.info("Requesting Nordigen: [{}]", command);
        String nordigenJsonText = JsonUtils.writeValueAsString(command.getRequestBody());
        NordigenLogEntity entity = new NordigenLogEntity();
        entity.setClientId(command.getClientId());
        entity.setLoanId(command.getLoanId());
        entity.setApplicationId(command.getApplicationId());
        entity.setInstantorResponseId(command.getInstantorResponseId());
        entity.setRequestBody(nordigenJsonText);
        entity.setRequestedAt(TimeMachine.now());
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            NordigenResponse response = provider.request(command.getClientId(), nordigenJsonText);
            entity.setStatus(response.getStatus());
            entity.setResponseBody(response.getResponseBody());
            entity.setError(response.getError());
            entity.setResponseStatusCode(response.getResponseStatusCode());
        } catch (Exception e) {
            log.error("Noridgen request failed", e);
            entity.setStatus(NordigenStatus.ERROR);
            entity.setResponseStatusCode(-1);
            entity.setError(Throwables.getRootCause(e).getMessage());
        } finally {
            log.info("Completed Nordigen request: {} in {} ms", command.getRequestBody(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        entity = repository.saveAndFlush(entity);
        NordigenResult result = entity.toValueObject();
        log.info("Returning Nordigen result: [{}]", result);
        return result;
    }

    @Override
    public Optional<NordigenResult> findLatest(NordigenQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getApplicationId() != null) {
            predicates.add(Entities.log.applicationId.eq(query.getApplicationId()));
        }
        if (query.getClientId() != null) {
            predicates.add(Entities.log.clientId.eq(query.getClientId()));
        }
        if (!query.getStatuses().isEmpty()) {
            predicates.add(Entities.log.status.in(query.getStatuses()));
        }
        if (query.getInstantorResponseId() != null) {
            predicates.add(Entities.log.instantorResponseId.eq(query.getInstantorResponseId()));
        }
        Page<NordigenLogEntity> items = repository.findAll(allOf(predicates), new QPageRequest(0, 1, Entities.log.id.desc()));
        return items.getContent().stream().map(NordigenLogEntity::toValueObject).findFirst();
    }

    @Override
    public NordigenResult get(Long id) {
        return repository.getRequired(id).toValueObject();
    }
}
