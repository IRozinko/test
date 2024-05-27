package fintech.bo.components.payments.handlers;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.payments.OperateOverpaymentTransactionRequest;
import fintech.bo.components.Converters;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.loan.LoanDataProvider;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.payments.AddTransactionComponent;
import fintech.bo.components.payments.TransactionHandler;
import fintech.bo.components.utils.BigDecimalUtils;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import org.jooq.Record;
import retrofit2.Call;

import java.util.Optional;
import java.util.function.Function;

import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;

public class OverpaymentTransactionHandler extends FormLayout implements TransactionHandler {

    private final ClientComponents clientComponents;
    private final LoanComponents loanComponents;
    private final Function<OperateOverpaymentTransactionRequest, Call<IdResponse>> call;
    private OperateOverpaymentTransactionRequest request;
    private AddTransactionComponent parent;
    private ComboBox<Record> loansComboBox;

    public OverpaymentTransactionHandler(ClientComponents clientComponents, LoanComponents loanComponents, Function<OperateOverpaymentTransactionRequest, Call<IdResponse>> call) {
        this.clientComponents = clientComponents;
        this.loanComponents = loanComponents;
        this.call = call;
    }

    @Override
    public void init(PaymentRecord payment, AddTransactionComponent parent) {
        this.parent = parent;
        setMargin(false);

        this.request = new OperateOverpaymentTransactionRequest();
        this.request.setPaymentId(payment.getId());
        this.request.setAmount(payment.getPendingAmount());

        LoanDataProvider loanDataProvider = loanComponents.dataProvider();
        loansComboBox = loanComponents.loansComboBox(loanDataProvider);
        loansComboBox.setCaption("Loan");
        loansComboBox.setWidth(100, Unit.PERCENTAGE);
        loansComboBox.focus();
        loansComboBox.addValueChangeListener(e -> {
            Record loan = e.getValue();
            loanSelected(loan);
        });

        TextField amount = new TextField("Amount");
        amount.setWidth(100, Unit.PERCENTAGE);
        TextField comments = new TextField("Comments");
        comments.setWidth(100, Unit.PERCENTAGE);

        Binder<OperateOverpaymentTransactionRequest> binder = new Binder<>();
        binder.forField(amount)
            .withValidator(s -> BigDecimalUtils.loe(BigDecimalUtils.amount(s), payment.getPendingAmount()), "The amount is more than payment.")
            .withConverter(Converters.stringToBigDecimalInputConverter())
            .bind(OperateOverpaymentTransactionRequest::getAmount, OperateOverpaymentTransactionRequest::setAmount);

        binder.bind(comments, OperateOverpaymentTransactionRequest::getComments, OperateOverpaymentTransactionRequest::setComments);
        binder.setBean(this.request);

        addComponent(loansComboBox);
        addComponent(amount);
        addComponent(comments);
    }

    private void loanSelected(Record loan) {
        parent.resetInfoPanel();
        parent.getInfoTabSheet().addTab(clientComponents.clientInfoSimple(loan.get(LOAN.CLIENT_ID)).noTitle(), "Client");
        PropertyLayout loanInfo = loanComponents.loanInfoSimple(loan.get(LOAN.ID)).noTitle();

        TabSheet.Tab loanTab = parent.getInfoTabSheet().addTab(loanInfo, "Loan");
        parent.getInfoTabSheet().setSelectedTab(loanTab);
    }

    @Override
    public Optional<Call<?>> saveCall() {
        if (loansComboBox.getValue() == null) {
            Notifications.errorNotification("Loan not selected");
            return Optional.empty();
        }
        request.setClientId(loansComboBox.getValue().get(LOAN.CLIENT_ID));
        request.setLoanId(loansComboBox.getValue().get(LOAN.ID));

        return Optional.of(call.apply(request));
    }
}
