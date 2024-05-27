package fintech.spain.unnax.impl;

import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.spain.unnax.UnnaxPayInService;
import fintech.spain.unnax.charge.ChargeSavedCardUnnaxClient;
import fintech.spain.unnax.charge.model.ChargeClientCardRequest;
import fintech.spain.unnax.db.*;
import fintech.spain.unnax.event.IncomingCardPaymentEvent;
import fintech.spain.unnax.event.IncomingTransferPaymentEvent;
import fintech.spain.unnax.event.PaymentWithCardEvent;
import fintech.spain.unnax.event.PaymentWithTransferAuthorizedEvent;
import fintech.spain.unnax.event.PaymentWithTransferCompletedEvent;
import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.webhook.model.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static fintech.spain.unnax.db.Entities.cardChargeRequest;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UnnaxPayInServiceImpl implements UnnaxPayInService {

    @Autowired
    private PaymentWithTransferAuthorizedRepository paymentWithTransferAuthorizedRepository;

    @Autowired
    private PaymentWithTransferCompletedRepository paymentWithTransferCompletedRepository;

    @Autowired
    private PaymentWithCardRepository paymentWithCardRepository;

    @Autowired
    private ChargeSavedCardUnnaxClient chargeSavedCardUnnaxClient;

    @Autowired
    private CardChargeRequestRepository cardChargeRequestRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void charge(Long clientId, ChargeClientCardRequest request) {
        CardChargeRequestEntity entity = transactionTemplate.execute(action -> {
            CardChargeRequestEntity requestEntity = new CardChargeRequestEntity();
            requestEntity.setClientId(clientId);
            requestEntity.setOrderCode(request.getOrderCode());
            requestEntity.setAmount(request.getAmount());
            requestEntity.setConcept(request.getConcept());
            requestEntity.setCardHash(request.getCardHash());
            requestEntity.setCardHashReference(request.getCardHashReference());
            requestEntity.setStatus(CardChargeStatus.NEW);
            return cardChargeRequestRepository.save(requestEntity);
        });
        UnnaxResponse<Void> unnaxResponse = chargeSavedCardUnnaxClient.charge(request);
        if (unnaxResponse.isError()) {
            entity.setStatus(CardChargeStatus.ERROR);
            entity.setError(JsonUtils.writeValueAsString((unnaxResponse.getErrorResponse())));
            cardChargeRequestRepository.save(entity);
            log.error("Error while charge from payment card through Unnax: {}", unnaxResponse.getErrorResponse());
        }
    }

    @Override
    public CardChargeStatus getChargeRequestStatus(Long clientId, String paymentOrderCode) {
        return cardChargeRequestRepository.getOptional(
            cardChargeRequest.orderCode.eq(paymentOrderCode).and(cardChargeRequest.clientId.eq(clientId)))
            .map(CardChargeRequestEntity::getStatus)
            .orElseThrow(() -> new RuntimeException("Charge request not found"));
    }

    @Override
    @EventListener
    public IncomingCardPaymentEvent handlePaymentWithCardEvent(PaymentWithCardEvent event) {
        PaymentWithCardEntity payment = new PaymentWithCardEntity();
        payment.setPan(event.getPan());
        payment.setBin(event.getBin());
        payment.setCurrency(event.getCurrency());
        payment.setTransactionType(event.getTransactionType());
        payment.setExpirationDate(event.getExpirationDate());
        payment.setExpireMonth(event.getExpireMonth());
        payment.setExpireYear(event.getExpireYear());
        payment.setCardHolder(event.getCardHolder());
        payment.setCardBrand(event.getCardBrand());
        payment.setCardType(event.getCardType());
        payment.setCardCountry(event.getCardCountry());
        payment.setCardBank(event.getCardBank());
        payment.setOrderCode(event.getOrderCode());
        payment.setToken(event.getToken());
        payment.setDate(event.getDate());
        payment.setAmount(event.getAmount());
        payment.setConcept(event.getConcept());
        payment.setState(event.getState());
        Long id = paymentWithCardRepository.save(payment).getId();

        cardChargeRequestRepository.getOptional(cardChargeRequest.orderCode.eq(payment.getOrderCode()))
            .ifPresent(cardCharge -> {
                if (payment.getState() == PaymentState.COMPLETED.getValue()) {
                    cardCharge.setStatus(CardChargeStatus.SUCCESS);
                } else if (payment.getState() == PaymentState.ERROR.getValue()) {
                    cardCharge.setStatus(CardChargeStatus.ERROR);
                }
            });

        return new IncomingCardPaymentEvent(id);
    }

    @Override
    @EventListener
    public void handlePaymentWithTransferAuthorizedEvent(PaymentWithTransferAuthorizedEvent event) {
        PaymentWithTransferAuthorizedEntity entity = new PaymentWithTransferAuthorizedEntity();
        entity.setOrderCode(event.getOrderCode());
        entity.setBankOrderCode(event.getBankOrderCode());
        entity.setAmount(event.getAmount());
        entity.setCurrency(event.getCurrency());
        entity.setCustomerCode(event.getCustomerCode());
        entity.setCustomerNames(event.getCustomerNames());
        entity.setService(event.getService());
        entity.setStatus(event.getStatus());
        entity.setSuccess(event.isSuccess());
        entity.setErrorMessages(event.getErrorMessages());
        entity.setDate(event.getDate());
        entity.setProcessedAt(TimeMachine.now());
        paymentWithTransferAuthorizedRepository.save(entity);
    }

    @Override
    @EventListener
    public IncomingTransferPaymentEvent handlePaymentWithTransferCompletedEvent(PaymentWithTransferCompletedEvent event) {
        PaymentWithTransferCompletedEntity entity = new PaymentWithTransferCompletedEntity();
        entity.setCustomerCode(event.getCustomerCode());
        entity.setOrderCode(event.getOrderCode());
        entity.setBankOrderCode(event.getBankOrderCode());
        entity.setAmount(event.getAmount());
        entity.setDate(event.getDate());
        entity.setSuccess(event.getSuccess());
        entity.setSignature(event.getSignature());
        entity.setResult(event.getResult());
        entity.setAccountNumber(event.getAccountNumber());
        entity.setStatus(event.getStatus());
        entity.setService(event.getService());
        Long id = paymentWithTransferCompletedRepository.save(entity).getId();
        return new IncomingTransferPaymentEvent(id);
    }

}
