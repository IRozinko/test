package fintech.bo.components.payments.handlers;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.payments.AddRepaymentTransactionRequest;
import fintech.bo.components.Converters;
import fintech.bo.components.Formats;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.ClientQueries;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.loan.LoanConstants;
import fintech.bo.components.loan.LoanDataProvider;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.payments.AddTransactionComponent;
import fintech.bo.components.payments.TransactionHandler;
import fintech.bo.components.utils.BigDecimalUtils;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import org.jooq.Record;
import retrofit2.Call;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;

public class RepaymentTransactionHandler extends FormLayout implements TransactionHandler {

    private ClientComponents clientComponents;
    private final LoanComponents loanComponents;
    private final ClientQueries clientQueries;
    private final PaymentApiClient paymentApiClient;
    private LoanApiClient loanApiClient;
    private AddRepaymentTransactionRequest request;
    private ComboBox<Record> loansComboBox;
    private Binder<AddRepaymentTransactionRequest> binder;
    private TextField overpaymentAvailable;
    private TextField overpayment;
    private TextField amount;
    private AddTransactionComponent parent;
    private DateField valueDate;

    public RepaymentTransactionHandler(ClientComponents clientComponents, LoanComponents loanComponents, ClientQueries clientQueries, PaymentApiClient paymentApiClient, LoanApiClient loanApiClient) {
        this.clientComponents = clientComponents;
        this.loanComponents = loanComponents;
        this.clientQueries = clientQueries;
        this.paymentApiClient = paymentApiClient;
        this.loanApiClient = loanApiClient;
    }

    @Override
    public void init(PaymentRecord payment, AddTransactionComponent parent) {
        this.parent = parent;
        setMargin(false);

        this.request = new AddRepaymentTransactionRequest();
        this.request.setPaymentId(payment.getId());
        this.request.setPaymentAmount(payment.getPendingAmount());
        this.request.setOverpaymentAmount(BigDecimal.ZERO);
        this.request.setValueDate(payment.getValueDate());

        LoanDataProvider loanDataProvider = loanComponents.dataProvider();
        loanDataProvider.setStatus(LoanConstants.STATUS_OPEN);
        loansComboBox = loanComponents.loansComboBox(loanDataProvider);
        loansComboBox.setCaption("Loan");
        loansComboBox.setWidth(100, Unit.PERCENTAGE);
        loansComboBox.focus();
        loansComboBox.addValueChangeListener(e -> {
            Record loan = e.getValue();
            loanSelected(payment, loan);
        });

        valueDate = new DateField();
        valueDate.setCaption("Value date");

        amount = new TextField("Amount");
        amount.setWidth(100, Unit.PERCENTAGE);

        overpayment = new TextField("Apply overpayment");
        overpayment.setWidth(100, Unit.PERCENTAGE);
        overpayment.setVisible(false);

        overpaymentAvailable = new TextField("Overpayment available");
        overpaymentAvailable.setWidth(100, Unit.PERCENTAGE);
        overpaymentAvailable.setReadOnly(true);
        overpaymentAvailable.setVisible(false);

        TextField comments = new TextField("Comments");
        comments.setWidth(100, Unit.PERCENTAGE);

        binder = new Binder<>();
        binder.forField(amount)
            .withValidator(s -> BigDecimalUtils.loe(BigDecimalUtils.amount(s), payment.getPendingAmount()), "The amount is more than payment.")
            .withConverter(Converters.stringToBigDecimalInputConverter())
            .bind(AddRepaymentTransactionRequest::getPaymentAmount, AddRepaymentTransactionRequest::setPaymentAmount);

        binder.forField(overpayment)
            .withConverter(Converters.stringToBigDecimalInputConverter())
            .bind(AddRepaymentTransactionRequest::getOverpaymentAmount, AddRepaymentTransactionRequest::setOverpaymentAmount);

        binder.bind(comments, AddRepaymentTransactionRequest::getComments, AddRepaymentTransactionRequest::setComments);
        binder.bind(valueDate, AddRepaymentTransactionRequest::getValueDate, AddRepaymentTransactionRequest::setValueDate);
        binder.setBean(this.request);

        addComponent(loansComboBox);
        addComponent(valueDate);
        addComponent(amount);
        addComponent(overpaymentAvailable);
        addComponent(overpayment);
        addComponent(comments);
    }

    private void loanSelected(PaymentRecord payment, Record loan) {
        BigDecimal overpaymentSum = clientQueries.getClientOverpaymentAvailable(loan.get(LOAN.CLIENT_ID));
        overpaymentAvailable.setValue(Formats.decimalFormat().format(overpaymentSum));
        overpaymentAvailable.setVisible(BigDecimalUtils.isPositive(overpaymentSum));
        overpayment.setValue(Formats.decimalFormat().format(overpaymentSum));
        overpayment.setVisible(BigDecimalUtils.isPositive(overpaymentSum));

        BigDecimal amount = BigDecimalUtils.min(loan.get(LOAN.TOTAL_DUE), payment.getPendingAmount());
        this.request.setPaymentAmount(amount);
        this.request.setLoanId(loan.get(LOAN.ID));
        binder.setBean(this.request);
        this.amount.focus();

        parent.resetInfoPanel();
        parent.getInfoTabSheet().addTab(clientComponents.clientInfoSimple(loan.get(LOAN.CLIENT_ID)).noTitle(), "Client");
        PropertyLayout loanInfo = loanComponents.loanInfoSimple(loan.get(LOAN.ID)).noTitle();

        TabSheet.Tab loanTab = parent.getInfoTabSheet().addTab(loanInfo, "Loan");
        parent.getInfoTabSheet().setSelectedTab(loanTab);
    }

    @Override
    public Optional<Call<?>> saveCall() {
        if (this.request.getLoanId() == null) {
            Notifications.errorNotification("Loan not selected");
            return Optional.empty();
        }
        request.setLoanId(loansComboBox.getValue().get(LOAN.ID));
        Call<List<IdResponse>> call = paymentApiClient.addRepaymentTransaction(request);
        return Optional.of(call);
    }
}
