package fintech.spain.unnax;

import fintech.spain.unnax.db.CreditCardEntity;
import fintech.spain.unnax.model.CreditCardQuery;

import java.util.List;
import java.util.Optional;

public interface PaymentCardService {

    Optional<CreditCardEntity> findCreditCard(CreditCardQuery query);

    List<CreditCardEntity> findCreditCards(CreditCardQuery query);

    void enableAutomaticPayments(String clientNumber);

    void disableAutomaticPayments(String clientNumber);

    boolean isAutoRepaymentEnabled(String clientNumber);
}
