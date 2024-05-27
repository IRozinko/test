package fintech.payments.impl;

import com.google.common.annotations.VisibleForTesting;
import fintech.payments.InstitutionService;
import fintech.payments.PaymentService;
import fintech.payments.commands.AddPaymentCommand;
import fintech.payments.model.InstitutionAccount;
import fintech.payments.model.PaymentType;
import fintech.spain.unnax.event.TransferAutoProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountingUnnaxPaymentListener {

    @VisibleForTesting
    protected static final String OUTGOING_PAYMENT_KEY_SUFFIX = "_outgoing";
    @VisibleForTesting
    protected static final String INCOMING_PAYMENT_KEY_SUFFIX = "_incoming";

    private final InstitutionService institutionService;
    private final PaymentService paymentService;

    @EventListener
    public void handlePaymentProcessed(TransferAutoProcessedEvent event) {
        if (!event.isSuccess())
            return;

        Optional<InstitutionAccount> sourceAccount = institutionService.findAccountByNumber(event.getSourceAccount());
        Optional<InstitutionAccount> destinationAccount = institutionService.findAccountByNumber(event.getCustomerAccount());

        // Not inner transfer
        if (!sourceAccount.isPresent() || !destinationAccount.isPresent())
            return;

        AddPaymentCommand outgoingCommand = AddPaymentCommand.fromUnnaxEvent(sourceAccount.get().getId(),
            PaymentType.OUTGOING, event, OUTGOING_PAYMENT_KEY_SUFFIX);
        outgoingCommand.setRequireManualStatus(true);

        AddPaymentCommand incomingCommand = AddPaymentCommand.fromUnnaxEvent(destinationAccount.get().getId(),
            PaymentType.INCOMING, event, INCOMING_PAYMENT_KEY_SUFFIX);
        incomingCommand.setRequireManualStatus(true);

        paymentService.addPayment(outgoingCommand);
        paymentService.addPayment(incomingCommand);
    }

}
