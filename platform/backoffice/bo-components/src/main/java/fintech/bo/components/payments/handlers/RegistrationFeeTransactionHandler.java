package fintech.bo.components.payments.handlers;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.payments.AddFeeTransactionRequest;
import fintech.bo.components.Converters;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.payments.AddTransactionComponent;
import fintech.bo.components.payments.TransactionHandler;
import fintech.bo.components.utils.BigDecimalUtils;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import retrofit2.Call;

import java.math.BigDecimal;
import java.util.Optional;

public class RegistrationFeeTransactionHandler extends FormLayout implements TransactionHandler {

    private String feeType;
    private BigDecimal suggestedAmount;
    private ClientComponents clientComponents;
    private final PaymentApiClient paymentApiClient;
    private AddFeeTransactionRequest request;
    private ComboBox<ClientDTO> clientsComboBox;

    public RegistrationFeeTransactionHandler(String feeType, BigDecimal suggestedAmount, ClientComponents clientComponents, PaymentApiClient paymentApiClient) {
        this.feeType = feeType;
        this.suggestedAmount = suggestedAmount;
        this.clientComponents = clientComponents;
        this.paymentApiClient = paymentApiClient;
    }

    @Override
    public void init(PaymentRecord payment, AddTransactionComponent parent) {
        setMargin(false);

        this.request = new AddFeeTransactionRequest();
        this.request.setPaymentId(payment.getId());
        this.request.setAmount(suggestedAmount);
        this.request.setFeeType(this.feeType);

        clientsComboBox = clientComponents.clientsComboBox();
        clientsComboBox.setCaption("Client");
        clientsComboBox.setWidth(100, Unit.PERCENTAGE);
        clientsComboBox.focus();

        TextField amount = new TextField("Amount");
        amount.setWidth(100, Unit.PERCENTAGE);

        Binder<AddFeeTransactionRequest> binder = new Binder<>();
        binder.forField(amount)
            .withValidator(s -> BigDecimalUtils.loe(BigDecimalUtils.amount(s), payment.getPendingAmount()), "The amount is more than payment.")
            .withConverter(Converters.stringToBigDecimalInputConverter())
            .bind(AddFeeTransactionRequest::getAmount, AddFeeTransactionRequest::setAmount);

        binder.setBean(this.request);

        addComponent(clientsComboBox);
        addComponent(amount);
    }

    @Override
    public Optional<Call<?>> saveCall() {
        if (clientsComboBox.getValue() == null) {
            Notifications.errorNotification("Client not selected");
            return Optional.empty();
        }
        request.setClientId(clientsComboBox.getValue().getId());
        Call<IdResponse> call = paymentApiClient.addFeeTransaction(request);
        return Optional.of(call);
    }
}
