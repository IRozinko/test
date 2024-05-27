package fintech.payments.commands;

import lombok.Data;

@Data
public class StatementImportCommand {
    Long fileId;
    Long institutionId;
}
