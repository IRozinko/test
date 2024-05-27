package fintech.spain.alfa.product.payments.processors;

import com.google.common.collect.ImmutableList;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.SettleDisbursementCommand;
import fintech.payments.DisbursementService;
import fintech.payments.model.*;
import fintech.payments.spi.PaymentAutoProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static fintech.BigDecimalUtils.eq;
import static fintech.payments.DisbursementService.DisbursementQuery.byFileName;
import static fintech.payments.DisbursementService.DisbursementQuery.byLoan;
import static fintech.payments.DisbursementService.DisbursementQuery.byReference;
import static fintech.payments.model.PaymentAutoProcessingResult.notProcessed;
import static fintech.payments.model.PaymentAutoProcessingResult.processed;

@Slf4j
@Component
public class DisbursementProcessor implements PaymentAutoProcessor {

    @Autowired
    private DisbursementService disbursementService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanFinder loanFinder;

    @Override
    public PaymentAutoProcessingResult autoProcessPayment(Payment payment, LocalDate when) {
        if (PaymentType.OUTGOING != payment.getPaymentType()) {
            return notProcessed();
        }

        List<Disbursement> maybeDisbursements = findDisbursements(payment);
        if (maybeDisbursements.isEmpty()) {
            return notProcessed();
        }

        BigDecimal disbursementsAmount = maybeDisbursements.stream().map(Disbursement::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!eq(disbursementsAmount, payment.getAmount()))
            return notProcessed();

        maybeDisbursements.forEach(disbursement -> {
            log.info("Settling disbursement [{}] with payment [{}]", disbursement, payment);
            loanService.settleDisbursement(new SettleDisbursementCommand()
                .setDisbursementId(disbursement.getId())
                .setPaymentId(payment.getId())
                .setAmount(disbursement.getAmount()));
        });
        return processed();
    }

    private List<Disbursement> findDisbursements(Payment payment) {
        Optional<Loan> maybeLoan = loanFinder.findLoan(payment);
        List<Disbursement> disbursements = new ArrayList<>();

        maybeLoan.ifPresent(loan -> disbursementService.getOptional(byLoan(loan.getId(), DisbursementStatusDetail.EXPORTED))
            .map(ImmutableList::of)
            .ifPresent(disbursements::addAll));

        if (disbursements.isEmpty())
            disbursements.addAll(findDisbursementsByReference(payment.getDetails()));

        if (disbursements.isEmpty())
            disbursements.addAll(findDisbursementsByFileName(payment));

        return disbursements;
    }

    private List<Disbursement> findDisbursementsByReference(String details) {
        Optional<String> disbursementReference = NumberMatcher.extractDisbursementReference(details);
        if (disbursementReference.isPresent()) {
            List<Disbursement> disbursements = disbursementService.findDisbursements(byReference(disbursementReference.get()));
            if (disbursements.size() == 1) {
                return ImmutableList.of(disbursements.get(0));
            }
        }
        return Collections.emptyList();
    }

    private List<Disbursement> findDisbursementsByFileName(Payment payment) {
        Optional<String> disbursementMsgId = NumberMatcher.extractDisbursementMsgId(payment.getDetails());
        if (disbursementMsgId.isPresent()) {
            return disbursementService.findDisbursements(byFileName(disbursementMsgId.get(), DisbursementStatusDetail.EXPORTED));
        }
        return Collections.emptyList();
    }

}
