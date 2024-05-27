package fintech.lending.core.invoice.spi;

public interface InvoiceNumberProvider {

    String nextInvoiceNumber(Long loanId);

}
