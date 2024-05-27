package fintech.spain.alfa.product.web.impl;

import fintech.TimeMachine;
import fintech.Validate;
import fintech.spain.alfa.product.web.db.PopupEntity;
import fintech.spain.alfa.product.web.spi.PopupService;
import fintech.spain.alfa.product.web.converter.PopupInfoConverter;
import fintech.spain.alfa.product.web.db.PopupRepository;
import fintech.spain.alfa.product.web.model.PopupInfo;
import fintech.spain.alfa.product.web.model.PopupResolution;
import fintech.spain.alfa.product.web.model.PopupType;
import fintech.spain.alfa.product.web.model.event.PopupResolvedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fintech.spain.alfa.product.web.model.PopupResolution.EXHAUSTED;
import static fintech.spain.alfa.product.web.model.PopupResolution.NONE;

@Slf4j
@Service
@Transactional
public class PopupServiceBean implements PopupService {

    private final ApplicationEventPublisher eventPublisher;
    private final PopupRepository repository;

    @Autowired
    public PopupServiceBean(ApplicationEventPublisher eventPublisher, PopupRepository repository) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

    @Override
    public List<PopupInfo> getActual(long clientId) {
        return repository.findActual(clientId, TimeMachine.now()).stream()
            .map(PopupInfoConverter.INSTANCE::convert)
            .collect(Collectors.toList());
    }

    @Override
    public PopupInfo show(long clientId, PopupType type) {
        return show(clientId, type, null, null);
    }

    @Override
    public PopupInfo show(long clientId, PopupType type, Duration expiration) {
        return show(clientId, type,  expiration, null);
    }

    @Override
    public PopupInfo show(long clientId, PopupType type, Duration expiration, Map<String, String> attributes) {
        List<PopupEntity> actual = repository.findByClientIdAndTypeAndResolution(clientId, type, NONE);
        if (!actual.isEmpty()) {
            log.info("Skipping add popup. There is exists another actual with type {}", type);
            return null;
        }

        PopupEntity entity = new PopupEntity();
        entity.setClientId(clientId);
        entity.setType(type);
        entity.setResolution(NONE);
        entity.setAttributes(attributes);
        if(expiration != null) {
            entity.setValidUntil(TimeMachine.now().plus(expiration));
        }
        repository.save(entity);

        return PopupInfoConverter.INSTANCE.convert(entity);
    }

    @Override
    public void resolve(long popupId, PopupResolution resolution) {
        PopupEntity entity = repository.getRequired(popupId);
        Validate.validState(NONE.equals(entity.getResolution()), "Changing resolution of resolved popup is forbidden");

        entity.setResolution(resolution);
        entity.setResolvedAt(TimeMachine.now());
        repository.save(entity);
        fireEvent(entity);
    }

    @Override
    public void markAsExhausted(long clientId, PopupType type) {
        repository
            .findByClientIdAndTypeAndResolution(clientId, type, NONE).stream()
            .peek(entity -> {
                entity.setResolution(EXHAUSTED);
                entity.setResolvedAt(TimeMachine.now());
            })
            .peek(repository::save)
            .forEach(this::fireEvent);
    }

    private void fireEvent(PopupEntity entity) {
        PopupInfo popupInfo = PopupInfoConverter.INSTANCE.convert(entity);
        eventPublisher.publishEvent(new PopupResolvedEvent(popupInfo));
    }
}
