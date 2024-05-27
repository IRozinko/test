package fintech.spain.alfa.product.payments.events;

import fintech.payments.events.PaymentProcessedEvent;
import fintech.payments.model.Payment;
import fintech.payments.model.PaymentType;
import fintech.spain.alfa.product.transaction.InvoiceAttachmentGenerator;
import fintech.spain.alfa.product.cms.ClientIncomingPaymentModel;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionQuery;
import fintech.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.isPositive;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
public class SendNotificationOnPaymentProcessed {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    @Autowired
    private InvoiceAttachmentGenerator invoiceAttachmentGenerator;

    @Autowired
    private AlfaCmsModels cmsModels;

    @EventListener
    public void onProcessed(PaymentProcessedEvent event) {
        Payment payment = event.getPayment();
        if (payment.getPaymentType() != PaymentType.INCOMING) {
            return;
        }
        List<Transaction> transactions = transactionService.findTransactions(TransactionQuery.builder().paymentId(payment.getId()).voided(false).build());
        Map<Long, List<Transaction>> transactionsByClient = transactions.stream()
            .filter(tx -> tx.getClientId() != null)
            .filter(tx -> isPositive(tx.getCashIn()))
            .collect(groupingBy(Transaction::getClientId));

        transactionsByClient.forEach((client, clientTransactions) -> {
            List<Long> fileIds = clientTransactions.stream()
                .map(transaction -> invoiceAttachmentGenerator.generate(transaction.getId()))
                .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
                .collect(toList());
            sendNotification(payment, client, clientTransactions, fileIds);
        });
    }

    private void sendNotification(Payment payment, Long clientId, List<Transaction> transactions, List<Long> fileIds) {
        BigDecimal totalCashIn = transactions.stream().map(Transaction::getCashIn).reduce(amount(0), BigDecimal::add);
        ClientIncomingPaymentModel model = new ClientIncomingPaymentModel();
        model.setValueDate(payment.getValueDate());
        model.setAmount(totalCashIn);
        Map<String, Object> cmsContext = cmsModels.clientIncomingPaymentContext(clientId, model);
        notificationFactory.fromCustomerService(clientId)
            .render(CmsSetup.CLIENT_PAYMENT_RECEIVED_NOTIFICATION, cmsContext)
            .emailAttachmentFileIds(fileIds)
            .send();
    }
}
