package fintech.spain.asnef.commands;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExportFileCommand {

    private Long logId;

    private LocalDate exportedAt;
}
