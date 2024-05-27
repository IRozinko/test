package fintech.spain.alfa.product.workflow.dormants.handler;

import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.commands.SaveCreditLimitCommand;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.settings.LocSettings;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.min;
import static fintech.BigDecimalUtils.roundHundreds;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class LocCreditLimitActivity implements ActivityHandler {

    @Autowired
    private SettingsService settingsService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public ActivityResult handle(ActivityContext context) {
        LocSettings.LocCreditLimitSettings settings = settingsService.getJson(LocSettings.LOC_CREDIT_LIMIT_SETTINGS, LocSettings.LocCreditLimitSettings.class);
        BigDecimal creditLimit = calculateCreditLimit(context, settings.getCreditLimitCalculatedCoefficient(), settings.getMaxCreditLimitAllowed());
        loanApplicationService.saveCreditLimit(new SaveCreditLimitCommand(context.getWorkflow().getApplicationId(), creditLimit));
        return ActivityResult.resolution(Resolutions.OK, "");
    }

    private BigDecimal calculateCreditLimit(ActivityContext context, BigDecimal coefficient, BigDecimal maxCreditLimit) {
        List<Loan> paidLoans = loanService.findLoans(LoanQuery.paidLoans(context.getClientId()));
        BigDecimal maxPrincipalPaid = paidLoans.stream().map(Loan::getPrincipalPaid).max(BigDecimal::compareTo).orElse(amount(0));
        BigDecimal calculatedMaxLimit = roundHundreds(maxPrincipalPaid.multiply(coefficient));
        return min(calculatedMaxLimit, maxCreditLimit);
    }
}
