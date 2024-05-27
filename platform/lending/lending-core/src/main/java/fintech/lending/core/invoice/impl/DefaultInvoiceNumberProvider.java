package fintech.lending.core.invoice.impl;

import fintech.lending.core.db.Entities;
import fintech.lending.core.invoice.db.InvoiceRepository;
import fintech.lending.core.invoice.spi.InvoiceNumberProvider;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class DefaultInvoiceNumberProvider implements InvoiceNumberProvider {

    @Autowired
    private LoanService loanService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public String nextInvoiceNumber(@NonNull Long loanId) {
        Loan loan = loanService.getLoan(loanId);

        long nextInvoiceIndex = countLoanInvoices(loan.getId()) + 1;

        return loan.getNumber() + "-" + nextInvoiceIndex;
    }


    private long countLoanInvoices(Long loanId) {
        return invoiceRepository.count(Entities.invoice.loanId.eq(loanId));
    }

}
