package fintech.spain.asnef.commands;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ImportFileCommand {

    private Long fileId;

    private LocalDate responseReceivedAt;
}
