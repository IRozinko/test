package fintech.risk.checklist.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ImportChecklistEntryCommand {

    private Long fileId;
    private String type;

    private boolean override;
}
