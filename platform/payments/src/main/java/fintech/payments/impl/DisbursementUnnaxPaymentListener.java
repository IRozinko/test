package fintech.payments.impl;

import fintech.TimeMachine;
import fintech.payments.DisbursementService;
import fintech.payments.InstitutionService;
import fintech.payments.PaymentService;
import fintech.payments.commands.AddPaymentCommand;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementExportResult;
import fintech.payments.model.DisbursementStatusDetail;
import fintech.payments.model.InstitutionAccount;
import fintech.payments.model.PaymentType;
import fintech.spain.unnax.event.TransferAutoProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static fintech.payments.DisbursementService.DisbursementQuery.byReference;

@Component
@Slf4j
@RequiredArgsConstructor
public class DisbursementUnnaxPaymentListener {

    private final InstitutionService institutionService;
    private final DisbursementService disbursementService;
    private final PaymentService paymentService;

    @EventListener
    public void handlePaymentProcessed(TransferAutoProcessedEvent event) {
        Optional<Disbursement> disbursement = disbursementService.getOptional(byReference(event.getOrderId()));
        if (!disbursement.isPresent())
            return;

        if (event.isSuccess())
            handle(event, disbursement.get());
        else
            handleError(event, disbursement.get());
    }

    private void handle(TransferAutoProcessedEvent event, Disbursement disbursement) {
        InstitutionAccount account = institutionService.findAccountByNumber(event.getSourceAccount())
            .orElseThrow(() -> new IllegalArgumentException(
                "Can't find institution with account number: " + event.getSourceAccount()
            ));

        if (disbursement.getStatusDetail() == DisbursementStatusDetail.ERROR) {
            log.info("Disbursement id = [{}] was retried on the Unnax side, change status to EXPORTED", disbursement.getId());
            disbursementService.exported(disbursement.getId(), TimeMachine.now(), DisbursementExportResult.exported());
        }

        AddPaymentCommand command = AddPaymentCommand.fromUnnaxEvent(account.getId(), PaymentType.OUTGOING, event);
        Long paymentId = paymentService.addPayment(command);
        paymentService.autoProcess(paymentId, TimeMachine.today());
    }

    private void handleError(TransferAutoProcessedEvent event, Disbursement disbursement) {
        disbursementService.error(disbursement.getId(), event.errorDetails());
    }

}
