package fintech.spain.alfa.product.strategy.penalty;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.BigDecimalUtils;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.spain.alfa.product.db.*;
import fintech.strategy.spi.StrategyPropertiesRepository;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.penalty.DpdPenaltyStrategyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class AlfaDpdPenaltyStrategyService implements StrategyPropertiesRepository {

    private final AlfaDpdPenaltyStrategyRepository penaltyStrategyRepository;
    private final AlfaDpdPenaltyStrategyPenaltyRepository penaltyRepository;

    @Override
    public void saveStrategy(Long calculationStrategyId, JsonNode props) {
        DpdPenaltyStrategyProperties properties = JsonUtils.treeToValue(props, DpdPenaltyStrategyProperties.class);

        validate(properties);

        penaltyStrategyRepository.getOptionalId(Entities.dpdPenaltyStrategy.calculationStrategyId.eq(calculationStrategyId))
            .ifPresent(strategyId -> {
                penaltyRepository.deleteAllByDpdPenaltyStrategyId(strategyId);
                penaltyStrategyRepository.delete(strategyId);
            });

        AlfaDpdPenaltyStrategyEntity entity = new AlfaDpdPenaltyStrategyEntity();
        entity.setCalculationStrategyId(calculationStrategyId);
        Long strategyId = penaltyStrategyRepository.save(entity).getId();

        properties.getStrategies().stream()
            .map(p -> {
                AlfaDpdPenaltyStrategyPenaltyEntity penaltyEntity = new AlfaDpdPenaltyStrategyPenaltyEntity();
                penaltyEntity.setDpdPenaltyStrategyId(strategyId);
                penaltyEntity.setDaysFrom(p.getFrom());
                penaltyEntity.setPenaltyRate(p.getRate());
                return penaltyEntity;
            })
            .forEach(penaltyRepository::save);
    }

    private void validate(DpdPenaltyStrategyProperties properties) {
        List<DpdPenaltyStrategyProperties.PenaltyStrategy> strategies = properties.getStrategies();
        Validate.notNull(strategies, "could not find strategies");
        for (DpdPenaltyStrategyProperties.PenaltyStrategy strategy : strategies) {
            Validate.isTrue(strategy.getFrom() != null && strategy.getFrom() >= 0, "Days from must be a positive number");
            Validate.isTrue(strategy.getRate() != null && BigDecimalUtils.isPositive(strategy.getRate()), "Invalid rate amount");
        }
    }

    @Override
    public JsonNode getStrategyPropertiesAsJson(Long calculationStrategyId) {
        return JsonUtils.toJsonNode(getStrategyProperties(calculationStrategyId));
    }

    @Override
    public Object getStrategyProperties(Long calculationStrategyId) {
        Long strategyId = penaltyStrategyRepository.getOptionalId(Entities.dpdPenaltyStrategy.calculationStrategyId.eq(calculationStrategyId))
            .orElseThrow(() -> new RuntimeException("Could not find DPD penalty strategy with id " + calculationStrategyId));

        List<DpdPenaltyStrategyProperties.PenaltyStrategy> strategies = penaltyRepository.getAllByDpdPenaltyStrategyId(strategyId).stream()
            .sorted(Comparator.comparing(AlfaDpdPenaltyStrategyPenaltyEntity::getDaysFrom))
            .map(pe -> {
                DpdPenaltyStrategyProperties.PenaltyStrategy penaltyStrategy = new DpdPenaltyStrategyProperties.PenaltyStrategy();
                penaltyStrategy.setFrom(pe.getDaysFrom());
                penaltyStrategy.setRate(pe.getPenaltyRate());
                return penaltyStrategy;
            })
            .collect(Collectors.toList());

        DpdPenaltyStrategyProperties properties = new DpdPenaltyStrategyProperties();
        properties.setStrategies(strategies);
        return properties;
    }

    @Override
    public boolean supports(String strategyType, String calculationType) {
        return StrategyType.PENALTY.getType().equals(strategyType) && AlfaDpdPenaltyStrategy.CALCULATION_TYPE.name().equals(calculationType);
    }
}
