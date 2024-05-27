package fintech.spain.alfa.product.payments.processors;

import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanStatus;
import fintech.payments.model.Payment;
import fintech.payments.model.PaymentAutoProcessingResult;
import fintech.payments.model.PaymentType;
import fintech.payments.spi.PaymentAutoProcessor;
import fintech.spain.alfa.product.extension.ApplyAndRepayExtensionFeeCommand;
import fintech.spain.alfa.product.extension.ExtensionService;
import fintech.strategy.model.ExtensionOffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

import static fintech.BigDecimalUtils.isZero;
import static fintech.payments.model.PaymentAutoProcessingResult.notProcessed;
import static fintech.payments.model.PaymentAutoProcessingResult.processed;

@Slf4j
@Component
public class LoanExtensionProcessor implements PaymentAutoProcessor {

    @Autowired
    private LoanFinder loanFinder;

    @Autowired
    private ExtensionService extensionService;

    @Override
    public PaymentAutoProcessingResult autoProcessPayment(Payment payment, LocalDate when) {
        if (PaymentType.INCOMING != payment.getPaymentType()) {
            return notProcessed();
        }

        Optional<Loan> maybeLoan = loanFinder.findLoan(payment);
        if (!maybeLoan.isPresent()) {
            return notProcessed();
        }

        Loan loan = maybeLoan.get();
        if (loan.getStatus() == LoanStatus.CLOSED || isZero(loan.getTotalDue())) {
            return notProcessed();
        }

        Optional<ExtensionOffer> extensionOffer = extensionService.findOfferForLoan(loan.getId(), payment.getAmount(), true, when);
        if (!extensionOffer.isPresent()) {
            return notProcessed();
        }

        extensionService.applyAndRepayExtensionFee(new ApplyAndRepayExtensionFeeCommand()
            .setValueDate(payment.getValueDate())
            .setLoanId(loan.getId())
            .setPaymentId(payment.getId())
            .setPaymentAmount(payment.getAmount())
            .setExtensionOffer(extensionOffer.get())
        );
        return processed();
    }
}
