package fintech.spain.asnef;

import fintech.spain.asnef.LogType;

import java.time.LocalDate;

public interface FileAsnefExportConsumer {

    void consume(LogType logType, LocalDate when);
}
