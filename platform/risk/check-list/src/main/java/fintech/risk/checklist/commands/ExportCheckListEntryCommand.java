package fintech.risk.checklist.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExportCheckListEntryCommand {
    @NotNull
    private String type;
}
