package fintech.payments.spi;

public interface StatementProcessorRegistry {

    void add(String fileFormatName, Class<? extends StatementParser> parser);

    StatementParser getParser(String fileFormatName);

}
