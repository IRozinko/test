package fintech.spain.alfa.product.risk.rules.basic;

import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.risk.checklist.CheckListConstants;
import fintech.risk.checklist.CheckListService;
import fintech.risk.checklist.model.CheckListQuery;
import fintech.rules.RuleBean;
import fintech.rules.model.Rule;
import fintech.rules.model.RuleContext;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.AlfaConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@RuleBean
public class DniBlacklistRule implements Rule {

    @Autowired
    private ClientService clientService;

    @Autowired
    private CheckListService checkListService;

    @Override
    public RuleResult execute(RuleContext context, RuleResultBuilder builder) {
        Client client = clientService.get(context.getClientId());
        if (StringUtils.isBlank(client.getDocumentNumber())) {
            return builder.reject(AlfaConstants.REJECT_REASON_NO_DNI);
        }
        builder.addCheck("DNI", "", client.getDocumentNumber());
        boolean allowed = checkListService.isAllowed(CheckListQuery.builder().type(CheckListConstants.CHECKLIST_TYPE_DNI).value1(client.getDocumentNumber()).build());
        if (!allowed) {
            return builder.reject(AlfaConstants.REJECT_REASON_DNI_NOT_ALLOWED);
        } else {
            return builder.approve();
        }
    }

    @Override
    public String getName() {
        return "DniBlacklist";
    }
}
