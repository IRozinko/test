package fintech.dc.impl;

import fintech.Validate;
import fintech.dc.DebtParser;
import fintech.dc.DebtProcessorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DebtProcessorRegistryBean implements DebtProcessorRegistry {

    private Map<String, Class<? extends DebtParser>> parsers = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void add(String fileFormatName,
                    Class<? extends DebtParser> parser) {
        parsers.put(fileFormatName, parser);
    }

    @Override
    public DebtParser getParser(String fileFormatName) {
        Validate.notNull(fileFormatName, "File format must not be null");
        Class<? extends DebtParser> parserClass = parsers.get(fileFormatName);
        Validate.notNull(parserClass, "Parser Class for debt format [%s] not found", fileFormatName);
        return applicationContext.getBean(parserClass);
    }
}
