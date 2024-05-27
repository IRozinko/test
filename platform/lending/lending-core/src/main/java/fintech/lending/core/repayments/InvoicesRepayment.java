package fintech.lending.core.repayments;

import fintech.lending.core.invoice.Invoice;
import fintech.lending.core.invoice.InvoiceQuery;
import fintech.lending.core.invoice.InvoiceService;
import fintech.lending.core.invoice.InvoiceStatus;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InvoicesRepayment {

    @Autowired
    private InvoiceRepayment invoiceRepayment;

    @Autowired
    private InvoiceService invoiceService;

    public List<Long> repay(RepayLoanCommand command, RunningAmount runningAmount) {
        List<Long> txIds = new ArrayList<>();
        for (Invoice invoice : findOpenInvoices(command.getLoanId())) {
            invoiceRepayment.repay(command, invoice, runningAmount).ifPresent(txIds::add);
        }
        return txIds;
    }

    private List<Invoice> findOpenInvoices(Long loanId) {
        return invoiceService.find(InvoiceQuery.builder()
            .loanId(loanId)
            .status(InvoiceStatus.OPEN)
            .build());
    }
}
