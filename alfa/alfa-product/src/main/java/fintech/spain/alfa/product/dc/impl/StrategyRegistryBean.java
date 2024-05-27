package fintech.spain.alfa.product.dc.impl;

import fintech.Validate;
import fintech.spain.alfa.product.dc.StrategyIdentifier;
import fintech.spain.alfa.product.dc.StrategyRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StrategyRegistryBean implements StrategyRegistry {

    private Map<String, Class<? extends StrategyIdentifier>> strategyIdentifiers = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void add(String companyName,
                    Class<? extends StrategyIdentifier> parser) {
        strategyIdentifiers.put(companyName, parser);
    }

    @Override
    public StrategyIdentifier getStrategy(String companyName) {
        Validate.notNull(companyName, "Company name must not be null");
        Class<? extends StrategyIdentifier> strategyIdentifierClass = strategyIdentifiers.get(companyName);
        Validate.notNull(strategyIdentifierClass, "StrategyIdentifier Class [%s] not found", strategyIdentifierClass);
        return applicationContext.getBean(strategyIdentifierClass);
    }
}
