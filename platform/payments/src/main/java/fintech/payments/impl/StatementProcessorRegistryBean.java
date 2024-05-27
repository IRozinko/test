package fintech.payments.impl;

import fintech.Validate;
import fintech.payments.spi.StatementParser;
import fintech.payments.spi.StatementProcessorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StatementProcessorRegistryBean implements StatementProcessorRegistry {

    private Map<String, Class<? extends StatementParser>> parsers = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void add(String fileFormatName,
                    Class<? extends StatementParser> parser) {
        parsers.put(fileFormatName, parser);
    }

    @Override
    public StatementParser getParser(String fileFormatName) {
        Validate.notNull(fileFormatName, "File format must not be null");
        Class<? extends StatementParser> parserClass = parsers.get(fileFormatName);
        Validate.notNull(parserClass, "Parser Class for statement format [%s] not found", fileFormatName);
        return applicationContext.getBean(parserClass);
    }

}
