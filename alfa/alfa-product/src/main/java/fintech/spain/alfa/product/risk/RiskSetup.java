package fintech.spain.alfa.product.risk;

import fintech.risk.checklist.CheckListConstants;
import fintech.risk.checklist.CheckListService;
import fintech.risk.checklist.commands.AddCheckListTypeCommand;
import fintech.risk.checklist.model.CheckListAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RiskSetup {

    @Autowired
    private CheckListService checkListService;

    public void setUp() {
        initChecklists();
    }

    private void initChecklists() {
        checkListService.addType(AddCheckListTypeCommand.builder().type(CheckListConstants.CHECKLIST_TYPE_PROVINCE_CODE).action(CheckListAction.BLACKLIST).build());
        checkListService.addType(AddCheckListTypeCommand.builder().type(CheckListConstants.CHECKLIST_TYPE_DNI).action(CheckListAction.BLACKLIST).build());
        checkListService.addType(AddCheckListTypeCommand.builder().type(CheckListConstants.CHECKLIST_TYPE_EMAIL).action(CheckListAction.BLACKLIST).build());
        checkListService.addType(AddCheckListTypeCommand.builder().type(CheckListConstants.CHECKLIST_TYPE_PHONE).action(CheckListAction.BLACKLIST).build());
    }
}
