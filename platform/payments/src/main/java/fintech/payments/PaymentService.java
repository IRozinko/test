package fintech.payments;

import fintech.payments.commands.AddPaymentCommand;
import fintech.payments.model.Payment;
import fintech.payments.model.PaymentType;
import fintech.payments.model.UpdatePaymentCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Long addPayment(AddPaymentCommand command);

    Long updatePayment(UpdatePaymentCommand command);

    Payment getPayment(Long id);

    void voidPayment(Long id);

    void unvoidPayment(Long id);

    void autoProcess(Long id, LocalDate when);

    void requireManualProcessing(Long id);

    List<Payment> findPayments(PaymentQuery query);

    Optional<Payment> getOptional(PaymentQuery query);

    @Data
    @AllArgsConstructor
    @Builder
    class PaymentQuery {
        private PaymentType paymentType;
        private String bankOrderCode;
    }
}
