package fintech.spain.platform.web.impl;

import fintech.PredicateBuilder;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.platform.web.db.SpecialLinkEntity;
import fintech.spain.platform.web.db.SpecialLinkRepository;
import fintech.spain.platform.web.model.SpecialLink;
import fintech.spain.platform.web.model.SpecialLinkActivated;
import fintech.spain.platform.web.model.SpecialLinkDeactivated;
import fintech.spain.platform.web.model.command.BuildLinkCommand;
import fintech.spain.platform.web.model.command.SpecialLinkQuery;
import fintech.spain.platform.web.spi.SpecialLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static fintech.spain.platform.web.db.Entities.specialLink;
import static fintech.spain.platform.web.model.command.SpecialLinkQuery.byToken;

@Slf4j
@Component
@Transactional
public class SpecialLinkServiceBean implements SpecialLinkService {
    private static final Long DEFAULT_EXPIRATION_HOURS = 2L;

    private final SpecialLinkRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public SpecialLinkServiceBean(SpecialLinkRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public SpecialLink buildLink(BuildLinkCommand command) {
        SpecialLinkEntity entity = repository
            .getFirstByClientIdAndType(command.getClientId(), command.getType())
            .orElseGet(() -> buildSpecialLink(command));

        entity.setReusable(command.isReusable());
        entity.setAutoLoginRequired(command.isAutoLoginRequired());
        if (command.getExpiresAt() != null) {
            Validate.isTrue(command.getExpiresAt().isAfter(TimeMachine.now()), "Token expiration date cannot be in the past");

            entity.setExpiresAt(command.getExpiresAt());
        } else {
            entity.setExpiresAt(TimeMachine.now().plusHours(DEFAULT_EXPIRATION_HOURS));
        }

        repository.save(entity);
        SpecialLink link = entity.toValueObject();
        log.info("Created link {}", link);
        return link;
    }

    @Override
    public Optional<SpecialLink> findLink(SpecialLinkQuery query) {
        return repository.findFirst(toPredicate(query).allOf(), specialLink.id.desc())
            .map(SpecialLinkEntity::toValueObject);
    }

    @Override
    public SpecialLink findRequiredLink(SpecialLinkQuery query) {
        return findLink(query)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Can't find special link: {%s}", query)));
    }

    @Override
    public SpecialLink activateLink(String token) {
        return activateLink(token, Collections.emptyMap());
    }

    @Override
    public SpecialLink activateLink(String token, Map<String, Object> activationParameters) {
        Validate.isTrue(!isExpired(token), "Token %s is already expired", token);

        SpecialLink link = findRequiredLink(byToken(token));

        eventPublisher.publishEvent(new SpecialLinkActivated(link, activationParameters));
        log.info("Link activated {}", link);

        if (!link.isReusable()) {
            deactivateLink(link);
        }
        return link;
    }

    @Override
    public boolean deactivateLink(long clientId, SpecialLinkType type) {
        Optional<SpecialLink> link = repository.getFirstByClientIdAndType(clientId, type)
            .map(SpecialLinkEntity::toValueObject);
        link.ifPresent(this::deactivateLink);
        return link.isPresent();
    }

    @Override
    public boolean isExpired(String token) {
        SpecialLink link = findRequiredLink(byToken(token));
        return !TimeMachine.now().isBefore(link.getExpiresAt());
    }

    private void deactivateLink(SpecialLink link) {
        SpecialLinkEntity specialLinkEntity = repository.getRequired(link.getId());
        specialLinkEntity.setExpiresAt(TimeMachine.now().minusSeconds(1));
        eventPublisher.publishEvent(new SpecialLinkDeactivated(link));
        log.info("Link deactivated {}", link);
    }

    private SpecialLinkEntity buildSpecialLink(BuildLinkCommand command) {
        SpecialLinkEntity linkEntity = new SpecialLinkEntity();
        linkEntity.setClientId(command.getClientId());
        linkEntity.setToken(UUID.randomUUID().toString());
        linkEntity.setType(command.getType());
        linkEntity.setExpiresAt(command.getExpiresAt());
        return linkEntity;
    }

    private PredicateBuilder toPredicate(SpecialLinkQuery query) {
        return new PredicateBuilder()
            .addIfPresent(query.getClientId(), specialLink.clientId::eq)
            .addIfPresent(query.getType(), specialLink.type::eq)
            .addIfPresent(query.getToken(), specialLink.token::eq)
            .addIfPresent(query.getOnlyValid(), a -> specialLink.expiresAt.gt(TimeMachine.now()));
    }
}
