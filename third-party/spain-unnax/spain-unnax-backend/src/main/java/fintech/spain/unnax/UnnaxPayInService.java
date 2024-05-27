package fintech.spain.unnax;

import fintech.spain.unnax.charge.model.ChargeClientCardRequest;
import fintech.spain.unnax.db.CardChargeStatus;
import fintech.spain.unnax.event.IncomingCardPaymentEvent;
import fintech.spain.unnax.event.IncomingTransferPaymentEvent;
import fintech.spain.unnax.event.PaymentWithCardEvent;
import fintech.spain.unnax.event.PaymentWithTransferAuthorizedEvent;
import fintech.spain.unnax.event.PaymentWithTransferCompletedEvent;

public interface UnnaxPayInService {

    void charge(Long clientId, ChargeClientCardRequest request);

    CardChargeStatus getChargeRequestStatus(Long clientId, String paymentOrderCode);

    IncomingCardPaymentEvent handlePaymentWithCardEvent(PaymentWithCardEvent event);

    void handlePaymentWithTransferAuthorizedEvent(PaymentWithTransferAuthorizedEvent event);

    IncomingTransferPaymentEvent handlePaymentWithTransferCompletedEvent(PaymentWithTransferCompletedEvent event);

}
