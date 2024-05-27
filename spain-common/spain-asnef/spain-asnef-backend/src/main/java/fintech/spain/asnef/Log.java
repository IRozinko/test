package fintech.spain.asnef;

import com.google.common.collect.Lists;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Log {

    private Long id;

    private LogType type;

    private LogStatus status;

    private LocalDate preparedAt;

    private LocalDate exportedAt;

    private LocalDate responseReceivedAt;

    private Long outgoingFileId;

    private Long incomingFileId;

    private List<LogRow> logRows = Lists.newArrayList();
}
