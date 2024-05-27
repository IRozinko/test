package fintech.spain.dc.command;

import fintech.dc.commands.PostLoanCommand;
import fintech.lending.core.loan.Loan;

public class PostLoanCommandFactory {

    public static PostLoanCommand fromLoan(Loan loan, boolean triggerActionsImmediately) {
        PostLoanCommand command = new PostLoanCommand();
        command.setTriggerActionsImmediately(triggerActionsImmediately);
        command.setLoanId(loan.getId());
        command.setClientId(loan.getClientId());
        command.setDpd(loan.getOverdueDays());
        command.setMaxDpd(loan.getMaxOverdueDays());
        command.setLoanStatus(loan.getStatus().name());
        command.setLoanStatusDetail(loan.getStatusDetail().name());
        command.setPaymentDueDate(loan.getPaymentDueDate());
        command.setMaturityDate(loan.getMaturityDate());
        command.setLoanNumber(loan.getNumber());
        command.setPeriodCount(loan.getPeriodCount());

        command.setTotalDue(loan.getTotalDue());
        command.setInterestDue(loan.getInterestDue());
        command.setPrincipalDue(loan.getPrincipalDue());
        command.setPenaltyDue(loan.getPenaltyDue());
        command.setFeeDue(loan.getFeeDue());
        command.setTotalOutstanding(loan.getTotalOutstanding());
        command.setInterestOutstanding(loan.getInterestOutstanding());
        command.setPrincipalOutstanding(loan.getPrincipalOutstanding());
        command.setPenaltyOutstanding(loan.getPenaltyOutstanding());
        command.setFeeOutstanding(loan.getFeeOutstanding());
        command.setInterestPaid(loan.getInterestPaid());
        command.setPrincipalPaid(loan.getPrincipalPaid());
        command.setPenaltyPaid(loan.getPenaltyPaid());
        command.setFeePaid(loan.getFeePaid());
        command.setTotalPaid(loan.getTotalPaid());
        return command;
    }
}
