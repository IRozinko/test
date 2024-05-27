package fintech.webanalytics.impl;

import com.querydsl.core.types.Predicate;
import fintech.webanalytics.WebAnalyticsService;
import fintech.webanalytics.db.WebAnalyticsEventEntity;
import fintech.webanalytics.db.WebAnalyticsEventRepository;
import fintech.webanalytics.model.SaveEventCommand;
import fintech.webanalytics.model.WebAnalyticsEvent;
import fintech.webanalytics.model.WebAnalyticsEventQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.ExpressionUtils.allOf;
import static fintech.webanalytics.db.Entities.event;

@Slf4j
@Transactional
@Component
public class WebAnalyticsServiceBean implements WebAnalyticsService {

    @Autowired
    private WebAnalyticsEventRepository repository;

    @Override
    public Long saveEvent(SaveEventCommand command) {
        log.info("Saving web analytics event: [{}]", command);
        WebAnalyticsEventEntity entity = new WebAnalyticsEventEntity();
        entity.setClientId(command.getClientId());
        entity.setApplicationId(command.getApplicationId());
        entity.setLoanId(command.getLoanId());
        entity.setIpAddress(command.getIpAddress());
        entity.setEventType(command.getEventType());
        entity.setUtmSource(command.getUtmSource());
        entity.setUtmMedium(command.getUtmMedium());
        entity.setUtmCampaign(command.getUtmCampaign());
        entity.setUtmTerm(command.getUtmTerm());
        entity.setUtmContent(command.getUtmContent());
        entity.setGclid(command.getGclid());
        return repository.saveAndFlush(entity).getId();
    }

    @Override
    public Optional<WebAnalyticsEvent> findLatest(WebAnalyticsEventQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getClientId() != null) {
            predicates.add(event.clientId.eq(query.getClientId()));
        }
        if (query.getApplicationId() != null) {
            predicates.add(event.applicationId.eq(query.getApplicationId()));
        }
        if (!query.getEventTypes().isEmpty()) {
            predicates.add(event.eventType.in(query.getEventTypes()));
        }
        Page<WebAnalyticsEventEntity> entities = repository.findAll(allOf(predicates), new QPageRequest(0, 1, event.id.desc()));
        return entities.getContent().stream().map(WebAnalyticsEventEntity::toValueObject).findFirst();
    }
}
