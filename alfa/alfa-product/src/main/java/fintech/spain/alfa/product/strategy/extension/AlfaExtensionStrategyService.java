package fintech.spain.alfa.product.strategy.extension;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.BigDecimalUtils;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.product.db.AlfaExtensionStrategyEntity;
import fintech.spain.alfa.product.db.AlfaExtensionStrategyRepository;
import fintech.strategy.spi.StrategyPropertiesRepository;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class AlfaExtensionStrategyService implements StrategyPropertiesRepository {

    private final AlfaExtensionStrategyRepository extensionStrategyRepository;

    @Autowired
    public AlfaExtensionStrategyService(AlfaExtensionStrategyRepository extensionStrategyRepository) {
        this.extensionStrategyRepository = extensionStrategyRepository;
    }

    @Override
    public void saveStrategy(Long calculationStrategyId, JsonNode props) {
        ExtensionStrategyProperties properties = JsonUtils.treeToValue(props, ExtensionStrategyProperties.class);

        validate(properties);
        extensionStrategyRepository.deleteByStrategyId(calculationStrategyId);

        List<AlfaExtensionStrategyEntity> newEntities = properties.getExtensions()
            .stream()
            .map(e -> {
                AlfaExtensionStrategyEntity entity = new AlfaExtensionStrategyEntity();
                entity.setCalculationStrategyId(calculationStrategyId);
                entity.setRate(e.getRate());
                entity.setTerm(e.getTerm());
                return entity;
            })
            .collect(Collectors.toList());

        extensionStrategyRepository.save(newEntities);
        extensionStrategyRepository.flush();
    }

    private void validate(ExtensionStrategyProperties properties) {
        properties.getExtensions()
            .forEach(e -> {
                Validate.isTrue(e.getTerm() > 0L, "Invalid term");
                Validate.isTrue(e.getRate() != null && BigDecimalUtils.isPositive(e.getRate()), "Invalid rate");
            });

        Validate.isTrue(properties.getExtensions()
            .stream()
            .map(ExtensionStrategyProperties.ExtensionOption::getTerm)
            .distinct()
            .count() == properties.getExtensions().size(), "Same term is not allowed in options");
    }

    @Override
    public JsonNode getStrategyPropertiesAsJson(Long calculationStrategyId) {
        return JsonUtils.toJsonNode(getStrategyProperties(calculationStrategyId));
    }

    @Override
    public Object getStrategyProperties(Long calculationStrategyId) {
        ExtensionStrategyProperties properties = new ExtensionStrategyProperties();

        List<AlfaExtensionStrategyEntity> extensionOptions = extensionStrategyRepository.findAll(Entities.extensionStrategy.calculationStrategyId.eq(calculationStrategyId));
        extensionOptions
            .stream()
            .sorted(Comparator.comparing(AlfaExtensionStrategyEntity::getTerm))
            .forEach(o -> properties.getExtensions().add(new ExtensionStrategyProperties.ExtensionOption()
                .setRate(o.getRate())
                .setTerm(o.getTerm()))
            );

        return properties;
    }

    @Override
    public boolean supports(String strategyType, String calculationType) {
        return StrategyType.EXTENSION.getType().equals(strategyType) && AlfaExtensionStrategy.CALCULATION_TYPE.name().equals(calculationType);
    }
}
