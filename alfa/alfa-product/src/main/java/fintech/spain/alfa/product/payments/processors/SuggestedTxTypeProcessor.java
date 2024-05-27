package fintech.spain.alfa.product.payments.processors;

import fintech.lending.core.payments.AddPaymentTransactionCommand;
import fintech.lending.core.payments.LendingPaymentsService;
import fintech.payments.StatementService;
import fintech.payments.model.Payment;
import fintech.payments.model.PaymentAutoProcessingResult;
import fintech.payments.model.StatementRow;
import fintech.payments.spi.PaymentAutoProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

import static fintech.payments.model.PaymentAutoProcessingResult.notProcessed;

/**
 * Auto process if corresponding payment's statement row has set suggested transaction sub type
 */
@Slf4j
@Component
public class SuggestedTxTypeProcessor implements PaymentAutoProcessor {

    @Autowired
    private StatementService statementService;

    @Autowired
    private LendingPaymentsService lendingPaymentsService;

    @Override
    public PaymentAutoProcessingResult autoProcessPayment(Payment payment, LocalDate when) {
        Optional<StatementRow> rowMaybe = statementService.findStatementRowByPayment(payment.getId());
        if (!rowMaybe.isPresent()) {
            return notProcessed();
        }
        StatementRow row = rowMaybe.get();
        if (StringUtils.isEmpty(row.getSuggestedTransactionSubType())) {
            return notProcessed();
        }
        lendingPaymentsService.addPaymentTransaction(
            AddPaymentTransactionCommand.builder()
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .comments("")
                .transactionSubType(row.getSuggestedTransactionSubType())
                .build()
        );
        return PaymentAutoProcessingResult.processed();
    }
}
