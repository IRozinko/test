package fintech.lending.core.repayments;

import fintech.Validate;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import fintech.lending.core.loan.spi.RepaymentStrategy;
import fintech.lending.core.overpayment.ApplyOverpaymentCommand;
import fintech.lending.core.overpayment.OverpaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.isPositive;

@Transactional
@Slf4j
@Component
public class LoanRepaymentStrategy implements RepaymentStrategy {

    @Autowired
    private LoanService loanService;

    @Autowired
    private OverpaymentService overpaymentService;

    @Autowired
    private InvoicesRepayment invoicesRepayment;

    @Autowired
    private LoanDueAmountsRepayment loanDueAmountsRepayment;

    @Autowired
    private LoanOutstandingAmountsRepayment loanOutstandingAmountsRepayment;

    @Autowired
    private InstallmentsRepayment installmentsRepayment;

    @Override
    public List<Long> repay(RepayLoanCommand command) {
        log.info("Processing repayment [{}]", command);
        Validate.isTrue(isPositive(command.getPaymentAmount()) || isPositive(command.getOverpaymentAmount()),
            "Invalid payment amount: [%s]", command);
        return distributePayment(command);
    }

    private List<Long> distributePayment(RepayLoanCommand command) {
        RunningAmount runningAmount = new RunningAmount(command.getPaymentAmount(), command.getOverpaymentAmount());
        List<Long> txIds = newArrayList();
        txIds.addAll(invoicesRepayment.repay(command, runningAmount));
        txIds.addAll(installmentsRepayment.repay(command, runningAmount));
        // probably not needed anymore
        txIds.addAll(loanDueAmountsRepayment.repay(command, runningAmount));
        txIds.addAll(loanOutstandingAmountsRepayment.repay(command, runningAmount));
        if (runningAmount.isPaymentAmountLeft()) {
            txIds.add(applyOverpayment(command, runningAmount));
        }
        Validate.isTrue(!txIds.isEmpty(), "No transactions created by repayment strategy");
        return txIds;
    }

    private Long applyOverpayment(RepayLoanCommand command, RunningAmount runningAmount) {
        Loan loan = loanService.getLoan(command.getLoanId());
        Long txId = overpaymentService.applyOverpayment(ApplyOverpaymentCommand.builder()
            .loanId(loan.getId())
            .clientId(loan.getClientId())
            .paymentId(command.getPaymentId())
            .amount(runningAmount.takePayment(runningAmount.getPaymentAmountLeft()))
            .comments(command.getComments())
            .build());
        runningAmount.resetAmountUsed();
        return txId;
    }
}
