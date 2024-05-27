package fintech.payments.impl;

import fintech.TimeMachine;
import fintech.payments.InstitutionService;
import fintech.payments.PaymentService;
import fintech.payments.commands.AddPaymentCommand;
import fintech.payments.model.PaymentType;
import fintech.spain.unnax.db.PaymentWithCardEntity;
import fintech.spain.unnax.db.PaymentWithCardRepository;
import fintech.spain.unnax.db.PaymentWithTransferCompletedEntity;
import fintech.spain.unnax.db.PaymentWithTransferCompletedRepository;
import fintech.spain.unnax.event.IncomingCardPaymentEvent;
import fintech.spain.unnax.event.IncomingTransferPaymentEvent;
import fintech.spain.unnax.webhook.model.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PayInUnnaxPaymentListener {

    private static final String UNNAX = "Unnax";

    @Autowired
    private PaymentWithCardRepository paymentWithCardRepository;
    @Autowired
    private PaymentWithTransferCompletedRepository paymentWithTransferCompletedRepository;
    @Autowired
    private InstitutionService institutionService;
    @Autowired
    private PaymentService paymentService;

    @EventListener
    public void handleCardPayment(IncomingCardPaymentEvent event) {
        PaymentWithCardEntity paymentEvent = paymentWithCardRepository.getRequired(event.getId());
        paymentEvent.setProcessedAt(TimeMachine.now());

        if (paymentEvent.getState() == PaymentState.COMPLETED.getValue()) {
            paymentService.addPayment(new AddPaymentCommand()
                .setAccountId(institutionService.getInstitution(UNNAX).getPrimaryAccount().getId())
                .setPaymentType(PaymentType.INCOMING)
                .setValueDate(paymentEvent.getDate().toLocalDate())
                .setPostedAt(paymentEvent.getDate())
                .setAmount(paymentEvent.getAmount())
                .setDetails(paymentEvent.getConcept())
                .setReference(paymentEvent.getConcept())
                .setBankOrderCode(paymentEvent.getOrderCode())
                .setKey(paymentEvent.getOrderCode())
            );
        } else {
            log.info("Skipped processing of unsuccessful Unnax card payment {}", paymentEvent.getOrderCode());
        }
    }

    @EventListener
    public void handleDirectBankTransferCompletion(IncomingTransferPaymentEvent event) {
        PaymentWithTransferCompletedEntity paymentEvent = paymentWithTransferCompletedRepository.getRequired(event.getId());
        paymentEvent.setProcessedAt(TimeMachine.now());

        if (Boolean.TRUE.equals(paymentEvent.getSuccess())) {
            paymentService.addPayment(new AddPaymentCommand()
                .setAccountId(institutionService.getInstitution(UNNAX).getPrimaryAccount().getId())
                .setPaymentType(PaymentType.INCOMING)
                .setValueDate(paymentEvent.getDate().toLocalDate())
                .setPostedAt(paymentEvent.getDate())
                .setAmount(paymentEvent.getAmount())
                .setBankOrderCode(paymentEvent.getBankOrderCode())
                .setCounterpartyAccount(paymentEvent.getAccountNumber())
                .setKey(paymentEvent.getOrderCode())
            );
        } else {
            log.info("Skipped processing of unsuccessful Unnax bank transfer payment {}", paymentEvent.getOrderCode());
        }
    }

}
