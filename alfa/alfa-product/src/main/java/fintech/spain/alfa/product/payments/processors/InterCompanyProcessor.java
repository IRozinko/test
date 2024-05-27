package fintech.spain.alfa.product.payments.processors;

import fintech.lending.core.payments.AddPaymentTransactionCommand;
import fintech.lending.core.payments.LendingPaymentsService;
import fintech.payments.InstitutionService;
import fintech.payments.model.InstitutionAccount;
import fintech.payments.model.Payment;
import fintech.payments.model.PaymentAutoProcessingResult;
import fintech.payments.spi.PaymentAutoProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static fintech.lending.creditline.TransactionConstants.TRANSACTION_SUB_TYPE_INTER_COMPANY_TRANSFER;
import static fintech.payments.model.PaymentAutoProcessingResult.notProcessed;
import static fintech.payments.model.PaymentAutoProcessingResult.processed;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Process as inter-company transfer if counter party account is found in institution accounts
 */
@Slf4j
@Component
public class InterCompanyProcessor implements PaymentAutoProcessor {

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private LendingPaymentsService lendingPaymentsService;

    @Override
    public PaymentAutoProcessingResult autoProcessPayment(Payment payment, LocalDate when) {
        String counterpartyAccount = payment.getCounterpartyAccount();
        if (isBlank(counterpartyAccount)) {
            return notProcessed();
        }

        Optional<InstitutionAccount> institutionAccount = institutionService.findAccountByNumber(counterpartyAccount);
        if (institutionAccount.isPresent() && !Objects.equals(institutionAccount.get().getId(), payment.getAccountId())) {
            lendingPaymentsService.addPaymentTransaction(
                AddPaymentTransactionCommand.builder()
                    .paymentId(payment.getId())
                    .amount(payment.getAmount())
                    .comments("")
                    .transactionSubType(TRANSACTION_SUB_TYPE_INTER_COMPANY_TRANSFER)
                    .build()
            );
            return processed();
        }
        return notProcessed();
    }
}
