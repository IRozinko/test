package fintech.payxpert.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.payxpert.connect2pay.client.containers.CreditCardPaymentMeanInfo;
import com.payxpert.connect2pay.client.containers.TransactionAttempt;
import com.payxpert.connect2pay.client.requests.PaymentRequest;
import com.payxpert.connect2pay.client.response.PaymentResponse;
import com.payxpert.connect2pay.client.response.PaymentStatusResponse;
import com.payxpert.connect2pay.constants.PaymentMode;
import com.payxpert.connect2pay.constants.PaymentStatusValue;
import com.payxpert.connect2pay.constants.PaymentType;
import com.payxpert.connect2pay.constants.ShippingType;
import com.payxpert.connect2pay.constants.TransactionOperation;
import fintech.DateUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.payxpert.CardAuthorizationRequestCommand;
import fintech.payxpert.PaymentRequestStatus;
import fintech.payxpert.PayxpertCreditCard;
import fintech.payxpert.PayxpertPaymentRequest;
import fintech.payxpert.PayxpertRebill;
import fintech.payxpert.PayxpertService;
import fintech.payxpert.RebillCommand;
import fintech.payxpert.RebillStatus;
import fintech.payxpert.RemoveCreditCardCommand;
import fintech.payxpert.db.Entities;
import fintech.payxpert.db.PayxpertCreditCardEntity;
import fintech.payxpert.db.PayxpertCreditCardRepository;
import fintech.payxpert.db.PayxpertPaymentRequestEntity;
import fintech.payxpert.db.PayxpertPaymentRequestRepository;
import fintech.payxpert.db.PayxpertRebillEntity;
import fintech.payxpert.db.PayxpertRebillRepository;
import fintech.payxpert.events.PayxpertResponseFailed;
import fintech.payxpert.events.PayxpertResponseProcessed;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.payxpert.PayxpertConstants.SUCCESS_CODE;

@Slf4j
@Component
@Transactional
public class PayxpertServiceBean implements PayxpertService {

    public static final int PAYMENT_REQUEST_EXPIRES_IN_MINUTES = 30;

    @Resource(name = "${payxpert.provider:" + MockPayxpertProviderBean.NAME + "}")
    private PayxpertProvider provider;

    @Autowired
    private PayxpertPaymentRequestRepository requestRepository;

    @Autowired
    private PayxpertCreditCardRepository creditCardRepository;

    @Autowired
    private PayxpertRebillRepository rebillRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @SneakyThrows
    @Override
    public PayxpertPaymentRequest cardAuthorizationRequest(CardAuthorizationRequestCommand command) {
        log.info("New card authorization request: [{}]", command);

        Validate.notBlank(command.getCallbackUrl(), "Empty callback url");
        Validate.isGoe(command.getAmount(), amount(0.01), "Invalid amount");
        Validate.notBlank(command.getCurrency(), "Empty currency");
        Validate.notNull(command.getClientId(), "Null client id");

        PaymentRequest request = new PaymentRequest();
        request
            .setOrderId(command.getOrderId())
            .setOrderDescription(command.getOrderDescription())
            .setCurrency(command.getCurrency())
            .setAmount(command.getAmount().multiply(amount(100)).intValue())
            .setShopperFirstName(command.getClientFirstName())
            .setShopperLastName(command.getClientLastName())
            .setShopperBirthDate(command.getClientDateOfBirth() == null ? null : DateUtils.toDate(command.getClientDateOfBirth()))
            .setShopperEmail(command.getClientEmail())
            .setShopperPhone(command.getClientPhone())
            .setShopperIDNumber(command.getClientIdNumber());

        request.setShippingType(ShippingType.VIRTUAL);
        request.setPaymentMode(PaymentMode.SINGLE);
        request.setPaymentType(PaymentType.CREDIT_CARD);
        request.setOperation(TransactionOperation.AUTHORIZE);
        request.setCtrlRedirectURL(command.getRedirectUrl());
        request.setCtrlCallbackURL(command.getCallbackUrl());
        request.validate();

        PaymentResponse response = provider.preparePayment(request);

        if (!response.isSuccessful()) {
            throw new IllegalStateException("Failed to prepare credit card authorization payment for client id " + command.getClientId() + ", message: " + response.getMessage());
        }

        PayxpertPaymentRequestEntity entity = new PayxpertPaymentRequestEntity();
        entity.setClientId(command.getClientId());
        entity.setPaymentType(request.getPaymentType());
        entity.setOperation(request.getOperation());
        entity.setAmount(command.getAmount());
        entity.setStatus(PaymentRequestStatus.PENDING);
        entity.setCustomerRedirectUrl(response.getCustomerRedirectURL(true));
        entity.setCurrency(request.getCurrency());
        entity.setOrderId(request.getOrderId());
        entity.setMerchantToken(response.getMerchantToken());
        entity.setCustomerToken(response.getCustomerToken());
        entity.setStatusCheckAttempts(0L);
        entity.setCtrlCallbackUrl(request.getCtrlCallbackURL());
        entity.setCtrlRedirectUrl(request.getCtrlRedirectURL());
        entity.setEnableRecurringPayments(true);
        entity.setSaveCreditCard(true);

        requestRepository.saveAndFlush(entity);

        return entity.toValueObject();
    }

    @Override
    public PayxpertPaymentRequest handleCallback(String callbackJson) {
        PaymentStatusResponse response = provider.handleCallback(callbackJson);
        return processStatusResponse(response);
    }

    @Override
    public PayxpertPaymentRequest checkRequestStatus(Long requestId, LocalDateTime when) {
        log.info("Checking payment request [{}] status", requestId);
        PayxpertPaymentRequestEntity request = requestRepository.getRequired(requestId);
        if (request.getStatus() == PaymentRequestStatus.PENDING && ChronoUnit.MINUTES.between(request.getCreatedAt(), when) > PAYMENT_REQUEST_EXPIRES_IN_MINUTES) {
            request.setStatus(PaymentRequestStatus.EXPIRED);
            return request.toValueObject();
        } else {
            PaymentStatusResponse response = provider.checkRequestStatus(request.toValueObject());
            return processStatusResponse(response);
        }
    }

    @Override
    public void updateStatusCheckAttempts(Long requestId, LocalDateTime when) {
        PayxpertPaymentRequestEntity request = requestRepository.getRequired(requestId);
        request.setStatusCheckAttempts(request.getStatusCheckAttempts() + 1);
        request.setLastStatusCheckAt(when);
        requestRepository.saveAndFlush(request);
    }

    @Override
    public PayxpertPaymentRequest getRequest(Long requestId) {
        return requestRepository.getRequired(requestId).toValueObject();
    }

    @Override
    public Optional<PayxpertCreditCard> findActiveCreditCard(Long clientId) {
        List<PayxpertCreditCardEntity> activeCards = creditCardRepository.findAll(Entities.creditCard.clientId.eq(clientId).and(Entities.creditCard.active.isTrue()));
        return activeCards.stream().findFirst().map(PayxpertCreditCardEntity::toValueObject);
    }

    @Override
    public List<PayxpertPaymentRequest> findPendingPaymentRequests(Long clientId) {
        return requestRepository.findAll(Entities.paymentRequest.clientId.eq(clientId)
            .and(Entities.paymentRequest.status.eq(PaymentRequestStatus.PENDING))
            .and(Entities.paymentRequest.saveCreditCard.eq(true)))
            .stream()
            .map(PayxpertPaymentRequestEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<PayxpertPaymentRequest> findLastPaymentRequest(Long clientId) {
        return requestRepository.findAll(Entities.paymentRequest.clientId.eq(clientId).and(Entities.paymentRequest.saveCreditCard.eq(true)),
            new QPageRequest(0, 1, Entities.paymentRequest.id.desc()))
            .getContent().stream().findFirst().map(PayxpertPaymentRequestEntity::toValueObject);
    }

    @Override
    public void removeCreditCard(RemoveCreditCardCommand command) {
        log.info("Removing credit card: [{}]", command);
        Long creditCardId = command.getCreditCardId();
        Long clientId = command.getClientId();
        PayxpertCreditCardEntity card = getActiveCard(creditCardId, clientId);
        card.setActive(false);
        card.setRecurringPaymentsEnabled(false);
    }

    @Override
    public PayxpertRebill rebill(RebillCommand command) {
        PayxpertCreditCardEntity card = getActiveCard(command.getCreditCardId(), command.getClientId());

        PayxpertRebillEntity entity = new PayxpertRebillEntity();
        entity.setCreditCard(card);
        entity.setAmount(command.getAmount());
        entity.setClientId(command.getClientId());
        entity.setInvoiceId(command.getInvoiceId());
        entity.setLoanId(command.getLoanId());
        entity.setCurrency(command.getCurrency());
        entity.setStatus(RebillStatus.PENDING);

        try {
            RebillResponse response = provider.rebill(new RebillRequest()
                .setAmount(command.getAmount().multiply(amount(100)).longValue())
                .setTransactionID(card.getCallbackTransactionId())
            );
            entity.setErrorMessage(response.getErrorMessage());
            entity.setErrorCode(response.getErrorCode());
            entity.setResponseStatementDescriptor(response.getStatementDescriptor());
            entity.setResponseTransactionId(response.getTransactionID());
            if (SUCCESS_CODE.equals(response.getErrorCode())) {
                entity.setStatus(RebillStatus.SUCCESS);
            } else {
                entity.setStatus(RebillStatus.ERROR);
            }
        } catch (Exception e) {
            log.error("Rebill request failed: [ " + command + "]", e);
            entity.setStatus(RebillStatus.ERROR);
            entity.setErrorCode("-1");
            entity.setErrorMessage(Throwables.getRootCause(e).getMessage());
        }

        rebillRepository.saveAndFlush(entity);
        return entity.toValueObject();
    }

    @Override
    public List<PayxpertRebill> findRebillsByInvoiceId(Long invoiceId) {
        return rebillRepository.findAll(Entities.rebill.invoiceId.eq(invoiceId), Entities.rebill.id.asc())
            .stream().map(PayxpertRebillEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public List<PayxpertRebill> findRebillsByCardId(Long cardId) {
        return rebillRepository.findAll(Entities.rebill.creditCard.id.eq(cardId), Entities.rebill.id.desc())
            .stream().map(PayxpertRebillEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public List<PayxpertRebill> findRebillsByCardAndErrorCodes(Long cardId, String... errorCodes) {
        return rebillRepository.findAll(Entities.rebill.creditCard.id.eq(cardId).and(Entities.rebill.errorCode.in(errorCodes)), Entities.rebill.id.asc())
            .stream().map(PayxpertRebillEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<PayxpertRebill> findLatestRebillByInvoiceId(Long invoiceId) {
        return rebillRepository.findAll(Entities.rebill.invoiceId.eq(invoiceId), new QPageRequest(0, 1, Entities.rebill.createdAt.desc())).getContent()
            .stream().map(PayxpertRebillEntity::toValueObject).findFirst();
    }

    @Override
    public void paymentCreatedForRebill(Long rebillId, Long paymentId) {
        PayxpertRebillEntity rebill = rebillRepository.getRequired(rebillId);
        rebill.setPaymentId(paymentId);
        rebill.setPaymentCreatedAt(TimeMachine.now());
        rebillRepository.saveAndFlush(rebill);
    }

    private PayxpertCreditCardEntity getActiveCard(Long creditCardId, Long clientId) {
        PayxpertCreditCardEntity card = creditCardRepository.findOne(
            Entities.creditCard.clientId.eq(clientId)
                .and(Entities.creditCard.active.isTrue())
                .and(Entities.creditCard.id.eq(creditCardId)));
        Validate.notNull(card, "Active card not found for client id [%s], card id [%s]", clientId, creditCardId);
        return card;
    }

    private PayxpertPaymentRequest processStatusResponse(PaymentStatusResponse response) {
        String orderId = response.getOrderId();
        log.info("Processing payment status response, order id [{}], status [{}], error code [{}], error message [{}]", response.getOrderId(), response.getStatus(), response.getErrorCode(), response.getErrorMessage());

        PayxpertPaymentRequestEntity entity = requestRepository.findOne(Entities.paymentRequest.orderId.eq(orderId));
        if (entity == null) {
            log.warn("Payment request not found by order id [{}]", orderId);
            return null;
        }
        if (entity.getStatus() == PaymentRequestStatus.SUCCESS) {
            eventPublisher.publishEvent(new PayxpertResponseProcessed(entity.getId(), entity.getClientId(), response));
            log.warn("Payment request [{}] already successfully handled, order id [{}]", entity.getId(), orderId);
            return entity.toValueObject();
        }

        if (response.getStatus() == PaymentStatusValue.AUTHORIZED) {
            entity.setStatus(PaymentRequestStatus.SUCCESS);
        } else if (response.getStatus() != null && !ImmutableSet.of(PaymentStatusValue.PENDING, PaymentStatusValue.NOT_PROCESSED, PaymentStatusValue.UNDEFINED).contains(response.getStatus())) {
            entity.setStatus(PaymentRequestStatus.ERROR);
        }
        entity.setStatusDetail(response.getStatus().name());
        entity.setErrorCode(response.getErrorCode());
        entity.setErrorMessage(response.getErrorMessage());

        TransactionAttempt lastAttempt = response.getLastTransactionAttempt();
        if (lastAttempt != null) {
            entity.setCallbackReceivedAt(TimeMachine.now());
            if (lastAttempt.getPaymentType() == PaymentType.CREDIT_CARD) {
                CreditCardPaymentMeanInfo creditCard = lastAttempt.getPaymentMeanInfo(CreditCardPaymentMeanInfo.class);
                entity.setCardBrand(creditCard.getCardBrand());
                entity.setCardExpireMonth(Long.parseLong(creditCard.getCardExpireMonth()));
                entity.setCardExpireYear(Long.parseLong(creditCard.getCardExpireYear()));
                entity.setCardHolderName(creditCard.getCardHolderName());
                entity.setCardIs3DSecure(creditCard.getIs3DSecure());
                entity.setCardNumber(creditCard.getCardNumber());
            } else {
                eventPublisher.publishEvent(new PayxpertResponseFailed(entity.getId(), entity.getClientId()));
                throw new IllegalStateException(String.format("Payment type [%s] not supported, order id [%s]", lastAttempt.getPaymentType(), response.getOrderId()));
            }
        }

        if (response.getStatus() == PaymentStatusValue.AUTHORIZED) {
            Validate.notNull(lastAttempt, "Callback has no last attempt, order id [%s]", response.getOrderId());
            Validate.notNull(lastAttempt.getTransactionId(), "Callback has no last transaction id, order id [%s]", response.getOrderId());
            entity.setCallbackTransactionId(lastAttempt.getTransactionId());
            if (entity.isSaveCreditCard()) {
                saveCreditCard(entity);
            }
        }
        log.info("Payment request updated, id [{}], client id [{}], order id [{}], status [{}], error code [{}]", entity.getId(), entity.getClientId(), response.getOrderId(), response.getStatus(), response.getErrorCode());
        requestRepository.saveAndFlush(entity);
        if (entity.getStatus() == PaymentRequestStatus.SUCCESS) {
            eventPublisher.publishEvent(new PayxpertResponseProcessed(entity.getId(), entity.getClientId(), response));
        } else if (entity.getStatus() == PaymentRequestStatus.ERROR) {
            eventPublisher.publishEvent(new PayxpertResponseFailed(entity.getId(), entity.getClientId()));
        }
        return entity.toValueObject();
    }

    private void saveCreditCard(PayxpertPaymentRequestEntity paymentRequest) {
        List<PayxpertCreditCardEntity> activeCards = creditCardRepository.findAll(Entities.creditCard.clientId.eq(paymentRequest.getClientId()).and(Entities.creditCard.active.isTrue()));
        activeCards.forEach(card -> card.setActive(false));

        PayxpertCreditCardEntity creditCard = new PayxpertCreditCardEntity();
        creditCard.setClientId(paymentRequest.getClientId());
        creditCard.setRequest(paymentRequest);
        creditCard.setCallbackTransactionId(paymentRequest.getCallbackTransactionId());
        creditCard.setActive(true);
        creditCard.setRecurringPaymentsEnabled(paymentRequest.isEnableRecurringPayments());
        creditCard.setCardNumber(paymentRequest.getCardNumber());
        creditCard.setCardExpireYear(paymentRequest.getCardExpireYear());
        creditCard.setCardExpireMonth(paymentRequest.getCardExpireMonth());
        creditCard.setCardHolderName(paymentRequest.getCardHolderName());
        creditCard.setCardBrand(paymentRequest.getCardBrand());
        creditCard.setCardIs3DSecure(paymentRequest.getCardIs3DSecure());
        creditCardRepository.saveAndFlush(creditCard);
    }
}
