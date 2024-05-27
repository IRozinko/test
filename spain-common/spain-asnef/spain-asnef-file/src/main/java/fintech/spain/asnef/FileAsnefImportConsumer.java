package fintech.spain.asnef;

import fintech.spain.asnef.LogType;

import java.time.LocalDate;

public interface FileAsnefImportConsumer {

    void consume(LogType logType, LocalDate when);
}
