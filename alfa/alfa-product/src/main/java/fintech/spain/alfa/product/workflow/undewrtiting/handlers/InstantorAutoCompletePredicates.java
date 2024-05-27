package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.TimeMachine;
import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows;
import fintech.workflow.spi.ActivityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;

@Slf4j
@Component
public class InstantorAutoCompletePredicates {

    @Autowired
    private ClientBankAccountService bankAccountService;

    @Autowired
    private IsGoodClient isGoodClient;

    @Autowired
    private LoanService loanService;

    @Autowired
    private LastInstantorResponseIsValid lastInstantorResponseIsValid;

    public Predicate<ActivityContext> repaidLoanIssuedInLast90Days() {
        return context -> loanService.findLoans(LoanQuery.paidLoans(context.getClientId()).setIssueDateFrom(TimeMachine.today().minusDays(90))).stream()
            .max(Comparator.comparing(Loan::getIssueDate))
            .filter(loan -> loan.getOverdueDays() < 30)
            .isPresent();
    }

    public Predicate<ActivityContext> hasPrimaryBankAccount() {
        return context -> bankAccountService.findPrimaryByClientId(context.getClientId()).isPresent();
    }

    public Predicate<ActivityContext> isGoodClient() {
        return context -> isGoodClient.isTrueFor(context);
    }

    public Predicate<ActivityContext> hasAttribute(String attributeName) {
        return context -> context.getWorkflow().hasAttribute(attributeName);
    }

    public Predicate<ActivityContext> skipInstantorManualCheck() {
        return context -> !Objects.equals(context.getWorkflow().activity(UnderwritingWorkflows.Activities.INSTANTOR_RULES).getResolution(), Resolutions.MANUAL);
    }

    public Predicate<ActivityContext> hasValidInstantorResponse() {
        return hasAttribute(Attributes.INSTANTOR_RESPONSE_ID)
            .and(context -> lastInstantorResponseIsValid.isTrueFor(context));
    }
}
