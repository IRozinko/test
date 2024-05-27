package fintech.lending.core.overpayment;

import fintech.lending.core.loan.commands.RepayLoanWithOverpaymentCommand;

import java.util.List;

public interface OverpaymentService {

    Long applyOverpayment(ApplyOverpaymentCommand command);

    Long refundOverpayment(RefundOverpaymentCommand command);

    List<Long> userOverpayment(RepayLoanWithOverpaymentCommand command);
}
