package fintech.bo.spain.alfa.payment;

import com.vaadin.data.Binder;
import com.vaadin.ui.*;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.loan.GetExtensionPricesRequest;
import fintech.bo.api.model.loan.GetExtensionPricesResponse;
import fintech.bo.api.model.payments.AddExtensionTransactionRequest;
import fintech.bo.components.Converters;
import fintech.bo.components.Formats;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.ClientQueries;
import fintech.bo.components.dc.DcQueries;
import fintech.bo.components.dc.DcSettingsJson;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.loan.LoanConstants;
import fintech.bo.components.loan.LoanDataProvider;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.payments.AddTransactionComponent;
import fintech.bo.components.payments.TransactionHandler;
import fintech.bo.components.utils.BigDecimalUtils;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import fintech.retrofit.RetrofitHelper;
import org.jooq.Record;
import retrofit2.Call;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;

public class ExtensionTransactionHandler extends FormLayout implements TransactionHandler {

    private ClientComponents clientComponents;
    private final LoanComponents loanComponents;
    private LoanApiClient loanApiClient;
    private final ClientQueries clientQueries;
    private final PaymentApiClient paymentApiClient;
    private AddExtensionTransactionRequest request;
    private ComboBox<Record> loansComboBox;
    private Binder<AddExtensionTransactionRequest> binder;
    private TextField overpaymentAvailable;
    private TextField amount;
    private TextField overpayment;
    private AddTransactionComponent parent;
    private DateField valueDate;
    private final DcQueries dcQueries;

    public ExtensionTransactionHandler(ClientComponents clientComponents, LoanComponents loanComponents, ClientQueries clientQueries, PaymentApiClient paymentApiClient, LoanApiClient loanApiClient) {
        this.clientComponents = clientComponents;
        this.loanComponents = loanComponents;
        this.loanApiClient = loanApiClient;
        this.clientQueries = clientQueries;
        this.paymentApiClient = paymentApiClient;
        this.dcQueries = ApiAccessor.gI().get(DcQueries.class);
    }

    @Override
    public void init(PaymentRecord payment, AddTransactionComponent parent) {
        this.parent = parent;
        setMargin(false);

        this.request = new AddExtensionTransactionRequest();
        this.request.setPaymentId(payment.getId());
        this.request.setPaymentAmount(payment.getPendingAmount());
        this.request.setValueDate(payment.getValueDate());
        this.request.setOverpaymentAmount(BigDecimal.ZERO);

        LoanDataProvider loanDataProvider = loanComponents.dataProvider();
        loanDataProvider.setStatus(LoanConstants.STATUS_OPEN);
        loansComboBox = loanComponents.loansComboBox(loanDataProvider);
        loansComboBox.setCaption("Loan");
        loansComboBox.setWidth(100, Unit.PERCENTAGE);
        loansComboBox.focus();
//        loansComboBox.addValueChangeListener(e -> loanSelected(e.getValue(), payment.getValueDate()));

        amount = new TextField("Amount");
        amount.setWidth(100, Unit.PERCENTAGE);

        valueDate = new DateField();
        valueDate.setCaption("Value date");

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
        binder.forField(amount).withConverter(Converters.stringToBigDecimalInputConverter())
            .bind(AddExtensionTransactionRequest::getPaymentAmount, AddExtensionTransactionRequest::setPaymentAmount);
        binder.forField(overpayment).withConverter(Converters.stringToBigDecimalInputConverter())
            .bind(AddExtensionTransactionRequest::getOverpaymentAmount, AddExtensionTransactionRequest::setOverpaymentAmount);
        binder.bind(comments, AddExtensionTransactionRequest::getComments, AddExtensionTransactionRequest::setComments);
        binder.bind(valueDate, AddExtensionTransactionRequest::getValueDate, AddExtensionTransactionRequest::setValueDate);
        binder.setBean(this.request);

        addComponent(loansComboBox);
        addComponent(valueDate);
        addComponent(amount);
        addComponent(overpayment);
        addComponent(overpaymentAvailable);
        addComponent(comments);
    }

    private void loanSelected(Record loan, LocalDate paymentValueDate) {
        Long loanId = loan.get(LOAN.ID);
        request.setLoanId(loanId);
        DcSettingsJson.ExtensionSettings settings = dcQueries.getSettings().getExtensionSettings();
        BigDecimal overpaymentSum = clientQueries.getClientOverpaymentAvailable(loan.get(LOAN.CLIENT_ID));
        overpaymentAvailable.setValue(Formats.decimalFormat().format(overpaymentSum));
        overpaymentAvailable.setVisible(BigDecimalUtils.isPositive(overpaymentSum));
        overpayment.setVisible(BigDecimalUtils.isPositive(overpaymentSum));

        PropertyLayout loanInfo = loanComponents.loanInfoSimple(loan.get(LOAN.ID)).noTitle();
        VerticalLayout loanLayout = new VerticalLayout(loanInfo);
        loanLayout.setMargin(false);
        Call<GetExtensionPricesResponse> call = loanApiClient.getExtensionPrices(GetExtensionPricesRequest.builder().loanId(loanId).date(paymentValueDate).build());
        RetrofitHelper.syncCall(call).ifPresent(response -> {
            VerticalLayout extensionsLayout = new VerticalLayout();
            response.getExtensions().forEach(extension -> {
                int extensionDays = loan.get(LOAN.EXTENDED_BY_DAYS);
                if (extensionDays + extension.getPeriodCount() <= settings.getMaxPeriodDays()) {
                    if (extension.getPriceWithDiscount() != null && !extension.getPrice().equals(extension.getPriceWithDiscount())) {
                        String text = String.format("Extend %s %s: %s", extension.getPeriodCount(), extension.getPeriodUnit(), Formats.decimalFormat().format(extension.getPriceWithDiscount()));
                        Button extend = new Button(text);
                        extend.addClickListener(e -> {
                            request.setPaymentAmount(extension.getPriceWithDiscount());
                            binder.setBean(request);
                            amount.focus();
                        });
                        extend.setWidth(100, Unit.PERCENTAGE);
                        extensionsLayout.addComponent(extend);
                        extensionsLayout.setVisible(true);
                    } else {
                        String text = String.format("Extend %s %s: %s", extension.getPeriodCount(), extension.getPeriodUnit(), Formats.decimalFormat().format(extension.getPrice()));
                        Button extend = new Button(text);
                        extend.addClickListener(e -> {
                            request.setPaymentAmount(extension.getPrice());
                            binder.setBean(request);
                            amount.focus();
                        });
                        extend.setWidth(100, Unit.PERCENTAGE);
                        extensionsLayout.addComponent(extend);
                        extensionsLayout.setVisible(true);
                    }
                }
            });
            loanLayout.addComponent(extensionsLayout);
        });
        binder.setBean(this.request);
        amount.focus();

        parent.resetInfoPanel();
        parent.getInfoTabSheet().addTab(clientComponents.clientInfoSimple(loan.get(LOAN.CLIENT_ID)).noTitle(), "Client");
        TabSheet.Tab loanTab = parent.getInfoTabSheet().addTab(loanLayout, "Loan");
        parent.getInfoTabSheet().setSelectedTab(loanTab);
    }

    @Override
    public Optional<Call<?>> saveCall() {
        if (this.request.getLoanId() == null) {
            Notifications.errorNotification("Loan not selected");
            return Optional.empty();
        }
        request.setLoanId(loansComboBox.getValue().get(LOAN.ID));
        Call<IdResponse> call = paymentApiClient.addExtensionTransaction(request);
        return Optional.of(call);
    }
}
