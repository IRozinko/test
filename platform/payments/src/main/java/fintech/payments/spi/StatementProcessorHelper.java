package fintech.payments.spi;

import com.google.common.annotations.VisibleForTesting;
import fintech.payments.db.Entities;
import fintech.payments.db.PaymentEntity;
import fintech.payments.db.PaymentRepository;
import fintech.payments.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.google.common.base.Strings.emptyToNull;

@Component
@RequiredArgsConstructor
public class StatementProcessorHelper {

    private final PaymentRepository paymentRepository;

    public Optional<Payment> paymentByUnnaxDetails(String reference) {
        return extractBankOrderCode(reference)
            .flatMap(paymentRepository::findByBankOrderCode)
            .map(PaymentEntity::toValueObject);
    }

    public boolean paymentByKeyExists(String key) {
        return paymentRepository.exists(
            Entities.payment.key.eq(key)
        );
    }

    /**
     * Example of details for Unnax outgoing payment: 'Transferencia emitida Jon Doe UNX0373 bankorder1 Unnax test'
     * 'bankorder1' - is bank_order_code value
     */
    @VisibleForTesting
    protected Optional<String> extractBankOrderCode(String details) {
        return Optional.ofNullable(emptyToNull(details))
            .map(str -> {
                int unx = str.indexOf("UNX");
                String[] s = new String[0];
                if (unx > -1)
                    s = str.substring(unx).split("\\W+");

                if (s.length > 1)
                    return s[1];

                return null;
            });
    }

}
