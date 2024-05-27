package fintech.lending.core.loan.spi;

import fintech.Validate;
import fintech.lending.core.invoice.spi.InvoicingStrategy;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanDerivedValuesResolver;
import fintech.lending.core.penalty.PenaltyStrategy;
import fintech.lending.core.periods.impl.DefaultPeriodClosingStrategy;
import fintech.lending.core.periods.spi.PeriodClosingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.collect.Maps.newConcurrentMap;

@Component
@Slf4j
public class LoanRegistry {

    @Autowired
    private ApplicationContext applicationContext;

    private Map<Long, DisbursementStrategy> disbursementStrategies = newConcurrentMap();

    private Map<Long, InvoicingStrategy> invoicingStrategies = newConcurrentMap();

    private Map<Long, RepaymentStrategy> repaymentStrategies = newConcurrentMap();

    private Map<Long, PenaltyStrategy> penaltyStrategies = newConcurrentMap();

    private Map<Long, BreakLoanStrategy> breakLoanStrategies = newConcurrentMap();

    private Map<Long, LoanIssueStrategy> loanIssueStrategies = newConcurrentMap();

    private Map<Long, LoanDerivedValuesResolver> loanDerivedValueResolvers = newConcurrentMap();

    private PeriodClosingStrategy periodClosingStrategy;

    public RepaymentStrategy getRepaymentStrategy(Loan loan) {
        Validate.isTrue(repaymentStrategies.containsKey(loan.getProductId()), "No repayment strategy found for loan [%s]", loan);

        return repaymentStrategies.get(loan.getProductId());
    }

    public DisbursementStrategy getDisbursementStrategy(Loan loan) {
        Validate.isTrue(disbursementStrategies.containsKey(loan.getProductId()), "No disbursement strategy found for loan [%s]", loan);

        return disbursementStrategies.get(loan.getProductId());
    }

    public InvoicingStrategy getInvoicingStrategy(Loan loan) {
        Validate.isTrue(invoicingStrategies.containsKey(loan.getProductId()), "No invoicing strategy found for loan [%s]", loan);

        return invoicingStrategies.get(loan.getProductId());
    }

    public PenaltyStrategy getPenaltyStrategy(Loan loan) {
        Validate.isTrue(penaltyStrategies.containsKey(loan.getProductId()), "No penalty strategy found for loan [%s]", loan);

        return penaltyStrategies.get(loan.getProductId());
    }

    public BreakLoanStrategy getBreakLoanStrategy(Loan loan) {
        Validate.isTrue(breakLoanStrategies.containsKey(loan.getProductId()), "No break loan strategy found for loan [%s]", loan);
        return breakLoanStrategies.get(loan.getProductId());
    }


    public LoanIssueStrategy getLoanIssueStrategy(Long productId) {
        Validate.isTrue(loanIssueStrategies.containsKey(productId), "No loan issue strategy found for product [%s]", productId);
        return loanIssueStrategies.get(productId);
    }


    public PeriodClosingStrategy getPeriodClosingStrategy() {
        if (periodClosingStrategy == null) {
            periodClosingStrategy = applicationContext.getBean(DefaultPeriodClosingStrategy.class);
            log.warn("No period closing strategy found, setting {} as default", periodClosingStrategy);
        }

        return periodClosingStrategy;
    }

    public LoanDerivedValuesResolver getLoanDerivedValueResolver(Long productId) {
        Validate.isTrue(loanDerivedValueResolvers.containsKey(productId), "No loan derived value resolver found for product [%s]", productId);
        return loanDerivedValueResolvers.get(productId);
    }

    public void addRepaymentStrategy(Long productId, RepaymentStrategy repaymentStrategy) {
        repaymentStrategies.put(productId, repaymentStrategy);
    }

    public void addInvoicingStrategy(Long productId, InvoicingStrategy invoicingStrategy) {
        invoicingStrategies.put(productId, invoicingStrategy);
    }

    public void addDisbursementStrategy(Long productId, DisbursementStrategy disbursementStrategy) {
        disbursementStrategies.put(productId, disbursementStrategy);
    }

    public void addPenaltyStrategy(Long productId, PenaltyStrategy penaltyStrategy) {
        penaltyStrategies.put(productId, penaltyStrategy);
    }

    public void addBreakLoanStrategy(Long productId, BreakLoanStrategy strategy) {
         breakLoanStrategies.put(productId, strategy);
    }

    public void addLoanIssueStrategy(Long productId, LoanIssueStrategy strategy) {
        loanIssueStrategies.put(productId, strategy);
    }

    public void setPeriodClosingStrategy(PeriodClosingStrategy periodClosingStrategy) {
        this.periodClosingStrategy = periodClosingStrategy;
    }

    public void addLoanDerivedValueResolver(Long productId, LoanDerivedValuesResolver loanDerivedValueResolver) {
        loanDerivedValueResolvers.put(productId, loanDerivedValueResolver);
    }
}
