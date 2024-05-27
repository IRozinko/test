package fintech.bo.components.payments.handlers;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.payments.AddOtherTransactionRequest;
import fintech.bo.components.Converters;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.payments.AddTransactionComponent;
import fintech.bo.components.payments.TransactionHandler;
import fintech.bo.components.utils.BigDecimalUtils;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import retrofit2.Call;

import java.util.Optional;

public class PaymentTransactionHandler extends FormLayout implements TransactionHandler {

    private final PaymentApiClient paymentApiClient;
    private ClientComponents clientComponents;
    private OtherTransactionConfig config;
    private AddOtherTransactionRequest request;

    public PaymentTransactionHandler(PaymentApiClient paymentApiClient, ClientComponents clientComponents, OtherTransactionConfig config) {
        this.paymentApiClient = paymentApiClient;
        this.clientComponents = clientComponents;
        this.config = config;
    }

    @Override
    public void init(PaymentRecord payment, AddTransactionComponent parent) {
        setMargin(false);

        this.request = new AddOtherTransactionRequest();
        this.request.setPaymentId(payment.getId());
        this.request.setAmount(payment.getPendingAmount());
        this.request.setTransactionSubType(config.getTransactionSubType());

        TextField amount = new TextField("Amount");
        amount.setWidth(100, Unit.PERCENTAGE);
        amount.focus();
        addComponent(amount);

        if (config.isShowClientSelection()) {
            ComboBox<ClientDTO> clientComboBox = clientComponents.clientsComboBox();
            clientComboBox.addValueChangeListener(e -> this.request.setClientId(e.getValue().getId()));
            clientComboBox.setCaption("Client (Optional)");
            clientComboBox.setWidth(100, Unit.PERCENTAGE);
            addComponent(clientComboBox);
        }

        TextField comments = new TextField("Comments");
        comments.setWidth(100, Unit.PERCENTAGE);
        addComponent(comments);

        Binder<AddOtherTransactionRequest> binder = new Binder<>();
        binder.forField(amount)
            .withValidator(s -> BigDecimalUtils.loe(BigDecimalUtils.amount(s), payment.getPendingAmount()), "The amount is more than payment.")
            .withConverter(Converters.stringToBigDecimalInputConverter())
            .bind(AddOtherTransactionRequest::getAmount, AddOtherTransactionRequest::setAmount);

        binder.bind(comments, AddOtherTransactionRequest::getComments, AddOtherTransactionRequest::setComments);
        binder.setBean(this.request);
    }

    @Override
    public Optional<Call<?>> saveCall() {
        Call<IdResponse> call = paymentApiClient.addOtherTransaction(request);
        return Optional.of(call);
    }
}
