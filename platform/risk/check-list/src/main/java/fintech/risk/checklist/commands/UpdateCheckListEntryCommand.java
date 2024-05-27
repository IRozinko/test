package fintech.risk.checklist.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateCheckListEntryCommand {

    private Long id;
    private String type;
    private String value1;
    private String comment;
}
