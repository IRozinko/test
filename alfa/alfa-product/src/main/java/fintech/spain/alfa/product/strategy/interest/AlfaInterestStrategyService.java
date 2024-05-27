package fintech.spain.alfa.product.strategy.interest;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.BigDecimalUtils;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.product.db.AlfaMonthlyInterestStrategyEntity;
import fintech.spain.alfa.product.db.AlfaMonthlyInterestStrategyRepository;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties;
import fintech.strategy.spi.StrategyPropertiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AlfaInterestStrategyService implements StrategyPropertiesRepository {

    private final AlfaMonthlyInterestStrategyRepository interestStrategyRepository;

    @Autowired
    public AlfaInterestStrategyService(AlfaMonthlyInterestStrategyRepository interestStrategyRepository) {
        this.interestStrategyRepository = interestStrategyRepository;
    }

    @Override
    public void saveStrategy(Long calculationStrategyId, JsonNode props) {
        MonthlyInterestStrategyProperties properties = JsonUtils.treeToValue(props, MonthlyInterestStrategyProperties.class);

        validate(properties);
        interestStrategyRepository.deleteByStrategyId(calculationStrategyId);

        AlfaMonthlyInterestStrategyEntity entity = new AlfaMonthlyInterestStrategyEntity();
        entity.setCalculationStrategyId(calculationStrategyId);
        entity.setInterestRate(properties.getMonthlyInterestRate());
        entity.setUsingDecisionEngine(properties.isUsingDecisionEngine());
        entity.setScenario(properties.getScenario());

        interestStrategyRepository.saveAndFlush(entity);
    }

    private void validate(MonthlyInterestStrategyProperties properties) {
        Validate.isTrue(properties.getMonthlyInterestRate() != null
            && !BigDecimalUtils.isNegative(properties.getMonthlyInterestRate()), "Invalid interest rate");
    }

    @Override
    public JsonNode getStrategyPropertiesAsJson(Long calculationStrategyId) {
        return JsonUtils.toJsonNode(getStrategyProperties(calculationStrategyId));
    }

    @Override
    public Object getStrategyProperties(Long calculationStrategyId) {
        AlfaMonthlyInterestStrategyEntity entity = interestStrategyRepository.findOne(Entities.interestStrategy.calculationStrategyId.eq(calculationStrategyId));

        MonthlyInterestStrategyProperties properties = new MonthlyInterestStrategyProperties();
        properties.setMonthlyInterestRate(entity.getInterestRate());
        properties.setUsingDecisionEngine(entity.isUsingDecisionEngine());
        properties.setScenario(entity.getScenario());

        return properties;
    }

    @Override
    public boolean supports(String strategyType, String calculationType) {
        return StrategyType.INTEREST.getType().equals(strategyType) && AlfaInterestStrategy.CALCULATION_TYPE.name().equals(calculationType);
    }


}
