package fintech.payxpert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PayxpertService {

    PayxpertPaymentRequest cardAuthorizationRequest(CardAuthorizationRequestCommand command);

    PayxpertPaymentRequest handleCallback(String callbackJson);

    PayxpertPaymentRequest checkRequestStatus(Long requestId, LocalDateTime when);

    PayxpertPaymentRequest getRequest(Long requestId);

    List<PayxpertPaymentRequest> findPendingPaymentRequests(Long clientId);

    Optional<PayxpertPaymentRequest> findLastPaymentRequest(Long clientId);

    void updateStatusCheckAttempts(Long requestId, LocalDateTime when);

    Optional<PayxpertCreditCard> findActiveCreditCard(Long clientId);

    void removeCreditCard(RemoveCreditCardCommand command);

    PayxpertRebill rebill(RebillCommand command);

    List<PayxpertRebill> findRebillsByInvoiceId(Long invoiceId);

    List<PayxpertRebill> findRebillsByCardId(Long cardId);

    List<PayxpertRebill> findRebillsByCardAndErrorCodes(Long cardId, String... errorCodes);

    Optional<PayxpertRebill> findLatestRebillByInvoiceId(Long invoiceId);

    void paymentCreatedForRebill(Long rebillId, Long paymentId);
}
