package fintech.spain.scoring.impl;

import com.querydsl.core.types.Predicate;
import fintech.Validate;
import fintech.spain.scoring.SpainScoringService;
import fintech.spain.scoring.db.Entities;
import fintech.spain.scoring.db.SpainScoringLogEntity;
import fintech.spain.scoring.db.SpainScoringLogRepository;
import fintech.spain.scoring.model.ScoringQuery;
import fintech.spain.scoring.model.ScoringRequestCommand;
import fintech.spain.scoring.model.ScoringResult;
import fintech.spain.scoring.spi.ScoringResponse;
import fintech.spain.scoring.spi.SpainScoringProvider;
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

import static com.querydsl.core.types.ExpressionUtils.allOf;

@Slf4j
@Transactional
@Component
public class SpainScoringServiceBean implements SpainScoringService {

    @Autowired
    private SpainScoringLogRepository logRepository;

    @Resource(name = "${spain.scoring.provider:" + MockSpainScoringProvider.NAME + "}")
    private SpainScoringProvider provider;

    @Override
    public ScoringResult requestScore(ScoringRequestCommand command) {
        log.info("Requesting score: [{}]", command);
        SpainScoringLogEntity entity = new SpainScoringLogEntity(command);
        try {
            ScoringResponse response = provider.request(command);
            entity.setResponse(response);
        } catch (Exception e) {
            log.error("Score request failed", e);
            entity.setError(e);
        }
        ScoringResult result = logRepository.saveAndFlush(entity)
            .toScoringResult();
        log.info("Returning scoring result: [{}]", result);
        return result;
    }

    @Override
    public Optional<ScoringResult> findLatest(ScoringQuery query) {
        Validate.notNull(query.getType(), "Type is required");
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(Entities.log.type.eq(query.getType()));
        if (query.getApplicationId() != null) {
            predicates.add(Entities.log.applicationId.eq(query.getApplicationId()));
        }
        if (query.getClientId() != null) {
            predicates.add(Entities.log.clientId.eq(query.getClientId()));
        }
        if (!query.getStatuses().isEmpty()) {
            predicates.add(Entities.log.status.in(query.getStatuses()));
        }
        Page<SpainScoringLogEntity> items = logRepository.findAll(allOf(predicates), new QPageRequest(0, 1, Entities.log.id.desc()));
        return items.getContent().stream().map(SpainScoringLogEntity::toScoringResult).findFirst();
    }

    @Override
    public ScoringResult get(Long id) {
        return logRepository.getRequired(id).toScoringResult();
    }
}
