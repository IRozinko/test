package fintech.iovation.impl;

import com.google.common.base.Throwables;
import com.querydsl.core.types.Predicate;
import fintech.Validate;
import fintech.iovation.IovationService;
import fintech.iovation.db.Entities;
import fintech.iovation.db.IovationBlackBoxEntity;
import fintech.iovation.db.IovationBlackBoxRepository;
import fintech.iovation.db.IovationTransactionEntity;
import fintech.iovation.db.IovationTransactionRepository;
import fintech.iovation.model.CheckTransactionCommand;
import fintech.iovation.model.IovationBlackBoxCreatedEvent;
import fintech.iovation.model.IovationBlackboxQuery;
import fintech.iovation.model.IovationStatus;
import fintech.iovation.model.IovationTransaction;
import fintech.iovation.model.SaveBlackboxCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
public class IovationServiceBean implements IovationService {

    @Autowired
    private IovationBlackBoxRepository blackBoxRepository;

    @Autowired
    private IovationTransactionRepository transactionRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Resource(name = "${iovation.provider:" + MockIovationProvider.NAME + "}")
    private IovationProvider provider;

    @Override
    public Long saveBlackbox(SaveBlackboxCommand command) {
        log.info("Saving blackbox: [{}]", command);
        IovationBlackBoxEntity entity = new IovationBlackBoxEntity();
        entity.setClientId(command.getClientId());
        entity.setIpAddress(command.getIpAddress());
        entity.setBlackBox(command.getBlackBox());
        entity.setLoanApplicationId(command.getLoanApplicationId());
        Long blackBoxId = blackBoxRepository.saveAndFlush(entity).getId();
        eventPublisher.publishEvent(new IovationBlackBoxCreatedEvent().setClientId(command.getClientId()));
        return blackBoxId;
    }

    @Override
    public Long checkTransaction(CheckTransactionCommand command) {
        log.info("Checking transaction: [{}]", command);
        Validate.notBlank(command.getIpAddress(), "No ip address");

        String blackbox = findLatestBlackBox(new IovationBlackboxQuery()
            .setClientId(command.getClientId()))
            .orElse("");

        IovationTransactionEntity entity = new IovationTransactionEntity();
        entity.setApplicationId(command.getApplicationId());
        entity.setBlackBox(blackbox);
        entity.setClientId(command.getClientId());
        entity.setIpAddress(command.getIpAddress());
        try {
            IovationRequest request = new IovationRequest();
            request.setIpAddress(command.getIpAddress());
            request.setAccountCode(command.getClientNumber());
            request.setBeginBlackBox(blackbox);

            IovationResponse response = provider.request(request);
            log.info("Iovation transaction resposne: [{}]", response);

            entity.setStatus(IovationStatus.OK);
            entity.setEndBlackBox(response.getEndBlackBox());
            entity.setReason(response.getReason());
            entity.setResult(response.getResult());
            entity.setDetails(response.getDetails());
            entity.setTrackingNumber(response.getTrackingNumber());
            entity.setDeviceId(response.getDeviceId());
        } catch (Exception e) {
            log.warn("Iovation request failed", e);
            entity.setStatus(IovationStatus.ERROR);
            entity.setError(Throwables.getRootCause(e).getMessage());
        }
        return transactionRepository.saveAndFlush(entity).getId();
    }

    @Override
    public IovationTransaction getTransaction(Long id) {
        return transactionRepository.getRequired(id).toValueObject();
    }

    @Override
    public Optional<String> findLatestBlackBox(IovationBlackboxQuery query) {
        Page<IovationBlackBoxEntity> all = blackBoxRepository.findAll(allOf(toPredicates(query)), new QPageRequest(0, 1, Entities.blackbox.id.desc()));
        return all.getContent().stream().map(IovationBlackBoxEntity::getBlackBox).findFirst();
    }

    private List<Predicate> toPredicates(IovationBlackboxQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getClientId() != null) {
            predicates.add(Entities.blackbox.clientId.eq(query.getClientId()));
        }
        if (query.getApplicationId() != null) {
            predicates.add(Entities.blackbox.loanApplicationId.eq(query.getApplicationId()));
        }
        return predicates;
    }
}
