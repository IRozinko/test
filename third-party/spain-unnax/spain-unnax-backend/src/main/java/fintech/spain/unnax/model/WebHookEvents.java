package fintech.spain.unnax.model;

import fintech.spain.unnax.callback.model.CallbackRequest;
import fintech.spain.unnax.event.*;
import fintech.spain.unnax.webhook.model.WebHookType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum WebHookEvents {

    EVENT_PAYMENT_TRANSFER_AUTO_CREATED(
        WebHookType.CALLBACK,
        "event_payment_transfer_auto_created",
        TransferAutoCreatedEvent::new
    ),

    EVENT_PAYMENT_TRANSFER_AUTO_PROCESSED(
        WebHookType.CALLBACK,
        "event_payment_transfer_auto_processed",
        TransferAutoProcessedEvent::new
    ),

    EVENT_PAYMENT_CREDITCARD_PREAUTHORIZE(
        WebHookType.CALLBACK,
        "event_payment_creditcard_preauthorize",
        CreditCardPreAuthorizeEvent::new
    ),

    EVENT_PAYMENT_CREDITCARD_PAY(
        WebHookType.CALLBACK,
        "event_payment_creditcard_pay",
        PaymentWithCardEvent::new
    ),

    EVENT_PAYMENT_TRANSFER_LOCKSTEP_AUTHORIZED(
        WebHookType.CALLBACK,
        "event_payment_transfer_lockstep_authorized",
        PaymentWithTransferAuthorizedEvent::new
    ),

    EVENT_PAYMENT_TRANSFER_LOCKSTEP_COMPLETED(
        WebHookType.CALLBACK,
        "event_payment_transfer_lockstep_completed",
        PaymentWithTransferCompletedEvent::new
    ),

    EVENT_PDFS_UPLOADED_LINK(
        WebHookType.CALLBACK,
        "event_pdfs_uploaded_link",
        BankStatementsUploadedEvent::new
    );

    private final WebHookType type;
    private final String name;
    private final Function<CallbackRequest, CallbackEvent> eventProducer;

    public CallbackEvent produceEvent(CallbackRequest request) {
        return eventProducer.apply(request);
    }

    public static WebHookEvents findEvent(String event) {
        return Stream.of(WebHookEvents.values())
            .filter(e -> e.getName().equalsIgnoreCase(event))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Unnax Callback Event is not supported {%s}.", event)));
    }

}
