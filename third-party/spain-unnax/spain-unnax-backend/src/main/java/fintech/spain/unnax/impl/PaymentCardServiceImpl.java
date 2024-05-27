package fintech.spain.unnax.impl;

import fintech.PredicateBuilder;
import fintech.spain.unnax.PaymentCardService;
import fintech.spain.unnax.db.CreditCardEntity;
import fintech.spain.unnax.db.CreditCardRepository;
import fintech.spain.unnax.db.CreditCardStatus;
import fintech.spain.unnax.event.CreditCardInfoProvidedFailedEvent;
import fintech.spain.unnax.event.CreditCardInfoProvidedSuccessEvent;
import fintech.spain.unnax.event.CreditCardPreAuthorizeEvent;
import fintech.spain.unnax.model.CreditCard;
import fintech.spain.unnax.model.CreditCardQuery;
import fintech.spain.unnax.webhook.model.CreditCardState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static fintech.spain.unnax.db.Entities.creditCard;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class PaymentCardServiceImpl implements PaymentCardService {

    private final CreditCardRepository creditCardRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Optional<CreditCardEntity> findCreditCard(CreditCardQuery query) {
        return creditCardRepository.getOptional(toPredicate(query).allOf());
    }

    @Override
    public List<CreditCardEntity> findCreditCards(CreditCardQuery query) {
        return creditCardRepository.findAll(toPredicate(query).allOf());
    }

    private PredicateBuilder toPredicate(CreditCardQuery query) {
        return new PredicateBuilder()
            .addIfPresent(query.getId(), creditCard.id::eq)
            .addIfPresent(query.getEnableAutoRepayments(), creditCard.automaticPaymentEnabled::eq)
            .addIfPresent(query.getOrderCode(), creditCard.orderCode::eq)
            .addIfPresent(query.getExpireMonth(), creditCard.cardExpireMonth::eq)
            .addIfPresent(query.getExpireYear(), creditCard.cardExpireYear::eq)
            .addIfPresent(query.getClientNumber(), creditCard.clientNumber::eq);
    }

    @Override
    public void enableAutomaticPayments(String clientNumber) {
        CreditCardEntity cardEntity = findCreditCard(CreditCardQuery.byClientNumber(clientNumber))
            .orElseThrow(() -> new IllegalArgumentException("Client does not have a payment card registered"));
        cardEntity.setAutomaticPaymentEnabled(Boolean.TRUE);
        creditCardRepository.save(cardEntity);
    }

    @Override
    public void disableAutomaticPayments(String clientNumber) {
        CreditCardEntity cardEntity = findCreditCard(CreditCardQuery.byClientNumber(clientNumber))
            .orElseThrow(() -> new IllegalArgumentException("Client does not have a payment card registered"));
        cardEntity.setAutomaticPaymentEnabled(Boolean.FALSE);
        creditCardRepository.save(cardEntity);
    }

    @Override
    public boolean isAutoRepaymentEnabled(String clientNumber) { 
        return findCreditCards(CreditCardQuery.byClientNumber(clientNumber))
            .stream()
            .findFirst()
            .map(CreditCardEntity::getAutomaticPaymentEnabled)
            .orElse(false);
    }


    @EventListener
    public void handleCreditCardPreAuthorized(CreditCardPreAuthorizeEvent event) {
        CreditCardEntity creditCard = findCreditCard(CreditCardQuery.byOrderCode(event.getOrderCode()))
            .orElseGet(CreditCardEntity::new);
        String clientNumber = StringUtils.substringBefore(event.getOrderCode(), ".");
        List<CreditCardEntity> currentCards = findCreditCards(CreditCardQuery.byClientNumber(clientNumber));
        Optional<CreditCard> lastActiveMaybe = currentCards.stream()
            .filter(CreditCardEntity::isActive)
            .map(CreditCardEntity::toValueObject)
            .findFirst();
        creditCardRepository.delete(currentCards);
        if (event.getState().equals(CreditCardState.SUCCESS.getValue())) {
            creditCard.setStatus(CreditCardStatus.PROCESSED);
            creditCard.setActive(true);
            creditCard.setAutomaticPaymentEnabled(true);
        } else if (event.getState().equals(CreditCardState.ERROR.getValue())) {
            creditCard.setStatus(CreditCardStatus.ERROR);
            creditCard.setActive(false);
            creditCard.setAutomaticPaymentEnabled(false);
        } else {
            creditCard.setStatus(CreditCardStatus.CANCELED);
            creditCard.setActive(false);
            creditCard.setAutomaticPaymentEnabled(false);
        }
        creditCard.setCallbackTransactionId(event.getResponseId());
        creditCard.setCardBank(event.getCardBank());
        creditCard.setCardBrand(event.getCardBrand());
        creditCard.setCardHolderName(event.getCardHolder());
        creditCard.setCardExpireMonth(Long.valueOf(event.getExpireMonth()));
        creditCard.setCardExpireYear(Long.valueOf(event.getExpireYear()));
        creditCard.setCardToken(event.getToken());
        creditCard.setOrderCode(event.getOrderCode());
        creditCard.setBin(event.getBin());
        creditCard.setPan(event.getPan());

        creditCard.setClientNumber(clientNumber);
        creditCard.setErrorDetails(event.errorDetails());

        creditCardRepository.save(creditCard);

        if (event.getState().equals(CreditCardState.SUCCESS.getValue())) {
            eventPublisher.publishEvent(new CreditCardInfoProvidedSuccessEvent(lastActiveMaybe.orElse(null), creditCard.getClientNumber()));
        } else if (event.getState().equals(CreditCardState.ERROR.getValue())) {
            eventPublisher.publishEvent(new CreditCardInfoProvidedFailedEvent(creditCard.getClientNumber()));
        }
    }

}
