package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.BigDecimalUtils;
import fintech.DateUtils;
import fintech.TimeMachine;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.AutoCompletePrecondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class IsGoodClient implements AutoCompletePrecondition {

    @Autowired
    private LoanService loanService;

    @Autowired
    private SettingsService settingsService;

    @Override
    public boolean isTrueFor(ActivityContext context) {
        AlfaSettings.GoodClientSettings settings = settingsService.getJson(AlfaSettings.LENDING_RULES_BASIC, AlfaSettings.GoodClientSettings.class);

        List<Loan> paidLoans = loanService.findLoans(LoanQuery.paidLoans(context.getClientId()));

        return isGoodClient(settings, paidLoans, TimeMachine.today());
    }

    static boolean isGoodClient(AlfaSettings.GoodClientSettings settings, List<Loan> paidLoans, LocalDate checkDate) {
        LocalDate periodFrom = checkDate.minusMonths(settings.getMonthsToCheckDpd());
        List<Loan> paidLoanInPeriod = paidLoans.stream().filter(l -> l.getCloseDate() != null).filter(l -> DateUtils.goe(l.getCloseDate(), periodFrom)).collect(Collectors.toList());
        Integer overdueDays = paidLoanInPeriod.stream().map(Loan::getOverdueDays).max(Comparator.naturalOrder()).orElse(0);

        BigDecimal repaidPrincipal = paidLoans.stream().map(Loan::getPrincipalPaid).reduce(amount(0), BigDecimal::add);

        return paidLoans.size() >= settings.getMinNumberOfPaidLoans()
            && overdueDays <= settings.getMaxDpd()
            && BigDecimalUtils.goe(repaidPrincipal, settings.getMinRepaidPrincipalAmount());
    }
}
