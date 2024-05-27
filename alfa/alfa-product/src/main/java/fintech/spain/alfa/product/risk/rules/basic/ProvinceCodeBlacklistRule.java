package fintech.spain.alfa.product.risk.rules.basic;

import fintech.crm.address.ClientAddress;
import fintech.crm.address.ClientAddressService;
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

import java.util.Optional;


@RuleBean
public class ProvinceCodeBlacklistRule implements Rule {

    @Autowired
    private ClientAddressService clientAddressService;

    @Autowired
    private CheckListService checkListService;

    @Override
    public RuleResult execute(RuleContext context, RuleResultBuilder builder) {

        Optional<ClientAddress> maybeAddress = clientAddressService.getClientPrimaryAddress(context.getClientId(), AlfaConstants.ADDRESS_TYPE_ACTUAL);
        if (!maybeAddress.isPresent()) {
            return builder.reject(AlfaConstants.REJECT_REASON_NO_ADDRESS);
        }
        ClientAddress address = maybeAddress.get();
        if (StringUtils.isBlank(address.getPostalCode())) {
            return builder.reject(AlfaConstants.REJECT_REASON_NO_POSTAL_CODE);
        }
        String provinceCode = StringUtils.left(address.getPostalCode(), 2);
        builder.addCheck("ProvinceCode", "", provinceCode);
        boolean provinceAllowed = checkListService.isAllowed(CheckListQuery.builder().type(CheckListConstants.CHECKLIST_TYPE_PROVINCE_CODE).value1(provinceCode).build());
        if (!provinceAllowed) {
            return builder.reject(AlfaConstants.REJECT_REASON_PROVINCE_NOT_ALLOWED);
        } else {
            return builder.approve();
        }
    }

    @Override
    public String getName() {
        return "ProvinceCodeBlacklist";
    }
}
