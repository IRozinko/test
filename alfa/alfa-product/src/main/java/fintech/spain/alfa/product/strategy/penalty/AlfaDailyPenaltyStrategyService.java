package fintech.spain.alfa.product.strategy.penalty;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.BigDecimalUtils;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.product.db.AlfaDailyPenaltyStrategyEntity;
import fintech.spain.alfa.product.db.AlfaDailyPenaltyStrategyRepository;
import fintech.strategy.spi.StrategyPropertiesRepository;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AlfaDailyPenaltyStrategyService implements StrategyPropertiesRepository {

    private final AlfaDailyPenaltyStrategyRepository dailyPenaltyStrategyRepository;

    @Autowired
    public AlfaDailyPenaltyStrategyService(AlfaDailyPenaltyStrategyRepository dailyPenaltyStrategyRepository) {
        this.dailyPenaltyStrategyRepository = dailyPenaltyStrategyRepository;
    }

    @Override
    public void saveStrategy(Long calculationStrategyId, JsonNode props) {
        DailyPenaltyStrategyProperties properties = JsonUtils.treeToValue(props, DailyPenaltyStrategyProperties.class);

        validate(properties);
        dailyPenaltyStrategyRepository.deleteByStrategyId(calculationStrategyId);

        AlfaDailyPenaltyStrategyEntity entity = new AlfaDailyPenaltyStrategyEntity();
        entity.setCalculationStrategyId(calculationStrategyId);
        entity.setPenaltyRate(properties.getPenaltyRate());

        dailyPenaltyStrategyRepository.saveAndFlush(entity);
    }

    private void validate(DailyPenaltyStrategyProperties properties) {
        Validate.isTrue(properties.getPenaltyRate() != null, "Null penalty rate");
        Validate.isTrue(!BigDecimalUtils.isNegative(properties.getPenaltyRate()), "Negative penalty rate");
    }

    @Override
    public JsonNode getStrategyPropertiesAsJson(Long calculationStrategyId) {
        return JsonUtils.toJsonNode(getStrategyProperties(calculationStrategyId));
    }

    @Override
    public Object getStrategyProperties(Long calculationStrategyId) {
        AlfaDailyPenaltyStrategyEntity entity = dailyPenaltyStrategyRepository.findOne(Entities.dailyPenaltyStrategy.calculationStrategyId.eq(calculationStrategyId));

        DailyPenaltyStrategyProperties properties = new DailyPenaltyStrategyProperties();
        properties.setPenaltyRate(entity.getPenaltyRate());

        return properties;
    }

    @Override
    public boolean supports(String strategyType, String calculationType) {
        return StrategyType.PENALTY.getType().equals(strategyType) && AlfaDailyPenaltyStrategy.CALCULATION_TYPE.name().equals(calculationType);
    }
}
