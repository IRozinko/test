package fintech.risk.checklist.commands;

import fintech.risk.checklist.model.CheckListAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AddCheckListTypeCommand {

    private String type;
    private CheckListAction action;
}
