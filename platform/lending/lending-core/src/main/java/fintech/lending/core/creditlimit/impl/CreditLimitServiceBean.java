package fintech.lending.core.creditlimit.impl;

import fintech.TimeMachine;
import fintech.Validate;
import fintech.lending.core.creditlimit.AddCreditLimitCommand;
import fintech.lending.core.creditlimit.CreditLimit;
import fintech.lending.core.creditlimit.CreditLimitService;
import fintech.lending.core.creditlimit.db.CreditLimitEntity;
import fintech.lending.core.creditlimit.db.CreditLimitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.lending.core.db.Entities.creditLimit;


@Slf4j
@Component
public class CreditLimitServiceBean implements CreditLimitService {

    @Autowired
    private CreditLimitRepository repository;

    @Transactional
    @Override
    public void addLimit(AddCreditLimitCommand command) {
        log.info("Adding credit limit {}", command);
        Validate.isZeroOrPositive(command.getLimit(), "Credit limit must be zero or positive: %s", command);
        Validate.notNull(command.getActiveFrom());
        Validate.notBlank(command.getReason());
        Validate.notNull(command.getClientId());
        Long clientId = command.getClientId();

        CreditLimitEntity entity = new CreditLimitEntity();
        entity.setClientId(clientId);
        entity.setLimit(command.getLimit());
        entity.setReason(command.getReason());
        entity.setActiveFrom(command.getActiveFrom());
        repository.saveAndFlush(entity);
    }

    @Override
    public Optional<CreditLimit> getClientLimit(Long clientId, LocalDate when) {
        return repository.findFirst(
            creditLimit.clientId.eq(clientId).and(creditLimit.activeFrom.loe(when)), creditLimit.id.desc())
            .map(CreditLimitEntity::toValueObject);
    }

    @Override
    public Optional<CreditLimit> getClientLimit(Long clientId) {
        return getClientLimit(clientId, TimeMachine.today());
    }

    @Override
    public List<CreditLimit> findAll(long clientId) {
        return repository.findAll(creditLimit.clientId.eq(clientId))
            .stream()
            .map(CreditLimitEntity::toValueObject)
            .collect(Collectors.toList());
    }
}
