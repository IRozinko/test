package fintech.spain.alfa.product.lending.impl;

import fintech.crm.bankaccount.ClientBankAccount;
import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.payments.InstitutionService;
import fintech.payments.model.Institution;
import fintech.payments.model.InstitutionAccount;
import fintech.payments.settigs.PaymentsSettings;
import fintech.payments.settigs.PaymentsSettingsService;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.payments.PaymentsSetup;
import org.apache.commons.lang3.StringUtils;
import org.iban4j.IbanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class InstitutionFinder {

    @Autowired
    private ClientBankAccountService clientBankAccountService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private SettingsService settingsService;

    Institution findDisbursementInstitution(Long clientId) {
        ClientBankAccount clientBankAccount = clientBankAccountService.findPrimaryByClientId(clientId).orElseThrow(() -> new IllegalStateException(String.format("Client with id [%s] without primary bank account", clientId)));
        String clientBankCode = IbanUtil.getBankCode(clientBankAccount.getAccountNumber());

        PaymentsSettings paymentSettings = settingsService.getJson(PaymentsSettingsService.PAYMENT_SETTINGS, PaymentsSettings.class);
        if (paymentSettings.isUnnaxEnabled())
            return institutionService.getInstitution(PaymentsSetup.INSTITUTION_UNNAX);

        return institutionService.getAllInstitutions().stream()
            .filter(institution -> !institution.isDisabled())
            .filter(institution -> {
                Optional<InstitutionAccount> institutionAccountOptional = institution.findPrimaryAccount();
                if (!institutionAccountOptional.isPresent()) {
                    return false;
                }
                if (StringUtils.isBlank(institution.getStatementExportFormat())) {
                    return false;
                }
                String institutionNationalBankCode = IbanUtil.getBankCode(institutionAccountOptional.get().getAccountNumber());
                return StringUtils.equals(clientBankCode, institutionNationalBankCode);
            }).findFirst().orElseGet(() -> institutionService.getPrimaryInstitution());
    }
}
