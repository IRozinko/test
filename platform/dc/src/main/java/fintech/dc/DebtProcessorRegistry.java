package fintech.dc;

public interface DebtProcessorRegistry {

    void add(String fileFormatName, Class<? extends DebtParser> parser);

    DebtParser getParser(String fileFormatName);
}
