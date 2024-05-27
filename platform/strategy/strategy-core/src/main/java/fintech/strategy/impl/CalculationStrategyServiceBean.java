package fintech.strategy.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.db.BaseEntity;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.strategy.CalculationStrategy;
import fintech.strategy.CalculationStrategyService;
import fintech.strategy.FeeStrategyFactory;
import fintech.strategy.SaveCalculationStrategyCommand;
import fintech.strategy.UpdateCalculationStrategyCommand;
import fintech.strategy.db.CalculationStrategyEntity;
import fintech.strategy.db.CalculationStrategyRepository;
import fintech.strategy.event.CalculationStrategySavedEvent;
import fintech.strategy.spi.ExtensionStrategy;
import fintech.strategy.spi.ExtensionStrategyFactory;
import fintech.strategy.spi.FeeStrategy;
import fintech.strategy.spi.InterestStrategy;
import fintech.strategy.spi.InterestStrategyFactory;
import fintech.strategy.spi.PenaltyStrategy;
import fintech.strategy.spi.PenaltyStrategyFactory;
import fintech.strategy.spi.StrategyPropertiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fintech.strategy.db.Entities.strategy;

@Slf4j
@Component
@Transactional
class CalculationStrategyServiceBean implements CalculationStrategyService {

    private final CalculationStrategyRepository strategyRepository;

    private final LoanService loanService;

    private final ExtensionStrategyFactory extensionStrategyFactory;

    private final InterestStrategyFactory interestStrategyFactory;

    private final PenaltyStrategyFactory penaltyStrategyFactory;
    private final FeeStrategyFactory feeStrategyFactory;

    private final List<StrategyPropertiesRepository> strategyPropertiesRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CalculationStrategyServiceBean(CalculationStrategyRepository strategyRepository,
                                          LoanService loanService,
                                          Optional<ExtensionStrategyFactory> extensionStrategyFactory,
                                          Optional<InterestStrategyFactory> interestStrategyFactory,
                                          Optional<PenaltyStrategyFactory> penaltyStrategyFactory,
                                          Optional<FeeStrategyFactory> feeStrategyFactory, Optional<List<StrategyPropertiesRepository>> strategyPropertiesRepository,
                                          ApplicationEventPublisher eventPublisher) {
        this.strategyRepository = strategyRepository;
        this.loanService = loanService;
        this.extensionStrategyFactory = extensionStrategyFactory.orElse(null);
        this.interestStrategyFactory = interestStrategyFactory.orElse(null);
        this.penaltyStrategyFactory = penaltyStrategyFactory.orElse(null);
        this.feeStrategyFactory = feeStrategyFactory.orElse(null);
        this.strategyPropertiesRepository = strategyPropertiesRepository.orElse(new ArrayList<>());
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Long saveCalculationStrategy(SaveCalculationStrategyCommand command) {
        validate(command);

        log.info("saving new calculation strategy [{}]", command);

        if (command.isDefault()) {
            strategyRepository.resetDefault(command.getStrategyType());
        }

        CalculationStrategyEntity strategyEntity = new CalculationStrategyEntity();
        strategyEntity.setStrategyType(command.getStrategyType());
        strategyEntity.setCalculationType(command.getCalculationType());
        strategyEntity.setVersion(command.getVersion());
        strategyEntity.setEnabled(command.isEnabled());
        strategyEntity.setIsDefault(command.isDefault() ? true : null);

        Long strategyId = strategyRepository.saveAndFlush(strategyEntity).getId();

        StrategyPropertiesRepository propertiesRepository = this.strategyPropertiesRepository
            .stream()
            .filter(r -> r.supports(strategyEntity.getStrategyType(), strategyEntity.getCalculationType()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Cannot save strategy. Unsupported properties"));

        propertiesRepository.saveStrategy(strategyId, JsonUtils.toJsonNode(command.getProperties()));

        eventPublisher.publishEvent(new CalculationStrategySavedEvent(
            strategyEntity.toValueObject()
        ));

        return strategyId;
    }

    @Override
    public void updateCalculationStrategy(UpdateCalculationStrategyCommand command) {
        validate(command);

        log.info("updating calculation strategy [{}]", command);

        CalculationStrategyEntity strategyEntity = strategyRepository.findOne(command.getStrategyId());

        if (command.isDefault() && strategyEntity.getIsDefault() == null) {
            strategyRepository.resetDefault(strategyEntity.getStrategyType());
        }

        strategyEntity.setVersion(command.getVersion());
        strategyEntity.setEnabled(command.isEnabled());
        strategyEntity.setIsDefault(command.isDefault() ? true : null);

        strategyRepository.saveAndFlush(strategyEntity);

        StrategyPropertiesRepository propertiesRepository = this.strategyPropertiesRepository
            .stream()
            .filter(r -> r.supports(strategyEntity.getStrategyType(), strategyEntity.getCalculationType()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Cannot save strategy. Unsupported properties"));

        propertiesRepository.saveStrategy(strategyEntity.getId(), JsonUtils.toJsonNode(command.getProperties()));
    }

    @Override
    public Optional<Long> getDefaultStrategyId(String strategyType) {
        return strategyRepository
            .getOptional(strategy.isDefault.isTrue().and(strategy.strategyType.eq(strategyType)))
            .map(BaseEntity::getId);
    }

    private void validate(SaveCalculationStrategyCommand command) {
        Validate.notNull(command.getStrategyType(), "Null strategy type");
        Validate.notNull(command.getProperties(), "Null properties");
        Validate.notNull(command.getVersion(), "Null version");
        Validate.notNull(command.getCalculationType(), "Null calculation type");

        strategyRepository.getOptional(
            strategy.strategyType.eq(command.getStrategyType())
                .and(strategy.version.eq(command.getVersion())
                    .and(strategy.calculationType.eq(command.getCalculationType()))))
            .ifPresent(r -> {
                throw new IllegalArgumentException("Strategy with such full name exists " + command.strategyFullName());
            });
    }

    private void validate(UpdateCalculationStrategyCommand command) {
        Validate.notNull(command.getProperties(), "Null properties");
        Validate.notNull(command.getVersion(), "Null version");
        Validate.notNull(command.getStrategyId(), "Null strategy id");
    }

    @Override
    public Optional<ExtensionStrategy> getExtensionStrategyForLoan(Long loanId) {
        Loan loan = loanService.getLoan(loanId);
        CalculationStrategyEntity strategy = strategyRepository.getRequired(loan.getExtensionStrategyId());
        JsonNode properties = getProperties(strategy);

        return Optional.ofNullable(extensionStrategyFactory).map(r -> r.createFor(strategy.toValueObject(), properties, loan));
    }

    @Override
    public Optional<PenaltyStrategy> getPenaltyStrategyForLoan(Long loanId) {
        Loan loan = loanService.getLoan(loanId);
        CalculationStrategyEntity strategy = strategyRepository.getRequired(loan.getPenaltyStrategyId());
        JsonNode properties = getProperties(strategy);

        return Optional.ofNullable(penaltyStrategyFactory).map(r -> r.createFor(strategy.toValueObject(), properties, loan));
    }

    @Override
    public Optional<InterestStrategy> getInterestStrategyById(Long strategyId) {
        CalculationStrategyEntity strategy = strategyRepository.getRequired(strategyId);
        JsonNode properties = getProperties(strategy);

        return Optional.ofNullable(interestStrategyFactory).map(r -> r.createFor(strategy.toValueObject(), properties));
    }

    @Override
    public Optional<FeeStrategy> getFeeStrategyForLoan(Long loanId) {
        Loan loan = loanService.getLoan(loanId);
        CalculationStrategyEntity strategy = strategyRepository.getRequired(loan.getFeeStrategyId());
        JsonNode properties = getProperties(strategy);
        return Optional.ofNullable(feeStrategyFactory).map(r -> r.createFor(strategy.toValueObject(), properties, loan));
    }

    private JsonNode getProperties(CalculationStrategyEntity entity) {
        StrategyPropertiesRepository propertiesRepository = this.strategyPropertiesRepository
            .stream()
            .filter(r -> r.supports(entity.getStrategyType(), entity.getCalculationType()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Cannot save strategy. Unsupported properties"));

        return propertiesRepository.getStrategyPropertiesAsJson(entity.getId());
    }

    @Override
    public Object getStrategyProperties(Long strategyId) {
        CalculationStrategyEntity strategy = strategyRepository.getRequired(strategyId);

        StrategyPropertiesRepository propertiesRepository = this.strategyPropertiesRepository
            .stream()
            .filter(r -> r.supports(strategy.getStrategyType(), strategy.getCalculationType()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Cannot save strategy. Unsupported properties"));

        return propertiesRepository.getStrategyProperties(strategy.getId());
    }


    @Override
    public Optional<CalculationStrategy> getStrategy(String strategyType, String calculationType, String versionType, boolean enable) {
        return this.strategyRepository.getStrategy(strategyType, calculationType, versionType, enable)
               .map(val -> val.toValueObject());
    }
}
