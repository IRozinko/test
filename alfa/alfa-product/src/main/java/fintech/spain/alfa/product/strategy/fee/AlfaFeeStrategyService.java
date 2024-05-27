package fintech.spain.alfa.product.strategy.fee;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.BigDecimalUtils;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.spain.alfa.product.db.AlfaDailyPenaltyStrategyEntity;
import fintech.spain.alfa.product.db.AlfaExtensionStrategyEntity;
import fintech.spain.alfa.product.db.AlfaFeeStrategyEntity;
import fintech.spain.alfa.product.db.AlfaFeeStrategyRepository;
import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties;
import fintech.spain.alfa.strategy.fee.FeeStrategyProperties;
import fintech.strategy.spi.StrategyPropertiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class AlfaFeeStrategyService implements StrategyPropertiesRepository {

    private final AlfaFeeStrategyRepository alfaFeeStrategyRepository;

    @Autowired
    public AlfaFeeStrategyService(AlfaFeeStrategyRepository alfaFeeStrategyRepository) {
        this.alfaFeeStrategyRepository = alfaFeeStrategyRepository;
    }

    @Override
    public void saveStrategy(Long calculationStrategyId, JsonNode props) {
        FeeStrategyProperties properties = JsonUtils.treeToValue(props, FeeStrategyProperties.class);

        validate(properties);
        alfaFeeStrategyRepository.deleteByStrategyId(calculationStrategyId);

        List<AlfaFeeStrategyEntity> newEntities = properties.getFees()
            .stream()
            .map(e -> {
                AlfaFeeStrategyEntity entity = new AlfaFeeStrategyEntity();
                entity.setCalculationStrategyId(calculationStrategyId);
                entity.setFeeRate(e.getOneTimeFeeRate());
                entity.setCompany(e.getCompany());
                return entity;
            })
            .collect(Collectors.toList());

        alfaFeeStrategyRepository.save(newEntities);
        alfaFeeStrategyRepository.flush();
    }

    private void validate(FeeStrategyProperties properties) {
        properties.getFees()
            .forEach(e -> {
                Validate.isTrue(e.getCompany() != null, "Invalid company");
                Validate.isTrue(e.getOneTimeFeeRate() != null && BigDecimalUtils.isPositive(e.getOneTimeFeeRate()), "Invalid fee rate");
            });
    }

    @Override
    public JsonNode getStrategyPropertiesAsJson(Long calculationStrategyId) {
        return JsonUtils.toJsonNode(getStrategyProperties(calculationStrategyId));
    }

    @Override
    public Object getStrategyProperties(Long calculationStrategyId) {
        FeeStrategyProperties properties = new FeeStrategyProperties();
        List<AlfaFeeStrategyEntity> feeOptions = alfaFeeStrategyRepository.findAll(Entities.feeStrategy.calculationStrategyId.eq(calculationStrategyId));
        feeOptions
            .stream()
            .sorted(Comparator.comparing(AlfaFeeStrategyEntity::getCompany))
            .forEach(o -> properties.getFees().add(new FeeStrategyProperties.FeeOption()
                .setOneTimeFeeRate(o.getFeeRate())
                .setCompany(o.getCompany()))
            );
        return properties;
    }

    @Override
    public boolean supports(String strategyType, String calculationType) {
        return StrategyType.FEE.getType().equals(strategyType) && AlfaFeeStrategy.CALCULATION_TYPE.name().equals(calculationType);
    }
}
