package fintech.lending.core.payments;

import org.springframework.stereotype.Component;

@Component
public interface LendingPaymentsService {

    Long addPaymentTransaction(AddPaymentTransactionCommand command);
}
