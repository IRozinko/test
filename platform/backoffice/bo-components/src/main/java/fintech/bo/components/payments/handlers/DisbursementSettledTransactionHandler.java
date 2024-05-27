package fintech.bo.components.payments.handlers;

import com.google.common.base.MoreObjects;
import com.vaadin.data.Binder;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.payments.AddDisbursementSettledTransactionRequest;
import fintech.bo.components.Converters;
import fintech.bo.components.payments.AddTransactionComponent;
import fintech.bo.components.payments.TransactionHandler;
import fintech.bo.components.payments.disbursement.DisbursementComponents;
import fintech.bo.components.payments.disbursement.DisbursementConstants;
import fintech.bo.components.payments.disbursement.SelectDisbursementDialog;
import fintech.bo.components.utils.BigDecimalUtils;
import fintech.bo.components.utils.SelectionComponent;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import org.jooq.Record;
import retrofit2.Call;

import java.math.BigDecimal;
import java.util.Optional;

import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.payment.tables.Disbursement.DISBURSEMENT;

public class DisbursementSettledTransactionHandler extends FormLayout implements TransactionHandler {

    private final PaymentApiClient paymentApiClient;
    private DisbursementComponents disbursementComponents;
    private AddDisbursementSettledTransactionRequest request;
    private Record disbursement;
    private SelectionComponent disbursementSelection;
    private Binder<AddDisbursementSettledTransactionRequest> binder;
    private AddTransactionComponent parent;
    private PaymentRecord payment;

    public DisbursementSettledTransactionHandler(PaymentApiClient paymentApiClient, DisbursementComponents disbursementComponents) {
        this.paymentApiClient = paymentApiClient;
        this.disbursementComponents = disbursementComponents;
    }

    @Override
    public void init(PaymentRecord payment, AddTransactionComponent parent) {
        this.payment = payment;
        this.parent = parent;
        this.request = new AddDisbursementSettledTransactionRequest();
        this.request.setPaymentId(payment.getId());
        this.request.setPaymentAmount(payment.getPendingAmount());
        setMargin(false);

        disbursementSelection = new SelectionComponent("Disbursement");
        disbursementSelection.getTextField().setPlaceholder("Select disbursement...");
        disbursementSelection.getButton().addClickListener(e -> selectDisbursement());
        disbursementSelection.setWidth(100, Unit.PERCENTAGE);
        addComponent(disbursementSelection);

        TextField amount = new TextField("Amount");
        amount.setWidth(100, Unit.PERCENTAGE);
        amount.setReadOnly(true);
        addComponent(amount);

        TextField comments = new TextField("Comments");
        comments.setWidth(100, Unit.PERCENTAGE);
        addComponent(comments);

        binder = new Binder<>();
        binder.forField(amount)
            .withValidator(s -> BigDecimalUtils.loe(BigDecimalUtils.amount(s), payment.getPendingAmount()), "The amount is more than payment.")
            .withConverter(Converters.stringToBigDecimalInputConverter())
            .bind(AddDisbursementSettledTransactionRequest::getPaymentAmount, AddDisbursementSettledTransactionRequest::setPaymentAmount);

        binder.bind(comments, AddDisbursementSettledTransactionRequest::getComments, AddDisbursementSettledTransactionRequest::setComments);
        binder.setBean(this.request);
    }

    private void selectDisbursement() {
        SelectDisbursementDialog dialog = disbursementComponents.selectDisbursementDialog();
        dialog.getProvider().setStatusDetail(DisbursementConstants.STATUS_DETAIL_EXPORTED);
        dialog.getProvider().setAccountId(this.payment.getAccountId());
        dialog.getProvider().setExportedAtTo(this.payment.getValueDate().plusDays(1).atStartOfDay());
        dialog.getProvider().setAmountLessOrEqual(this.payment.getPendingAmount());
        dialog.setModal(true);
        dialog.setAction(() -> {
            Optional<Record> selected = dialog.getSelected();
            if (selected.isPresent()) {
                disbursement = selected.get();
                BigDecimal disbursementAmount = disbursement.get(DISBURSEMENT.AMOUNT);

                request.setDisbursementId(disbursement.get(DISBURSEMENT.ID));
                request.setPaymentAmount(disbursementAmount);
                binder.setBean(this.request);

                disbursementSelection.getTextField().setValue(String.format("%s | %s | %s",
                    disbursement.get(DISBURSEMENT.ID),
                    disbursementAmount,
                    MoreObjects.firstNonNull(disbursement.get(LOAN.LOAN_NUMBER), "")));

                parent.resetInfoPanel();
                TabSheet.Tab tab = parent.getInfoTabSheet().addTab(disbursementComponents.disbursementInfo(this.disbursement).noTitle(), "Disbursement");
                parent.getInfoTabSheet().setSelectedTab(tab);
            }
            dialog.close();
        });
        getUI().addWindow(dialog);
    }

    @Override
    public Optional<Call<?>> saveCall() {
        Call<IdResponse> call = paymentApiClient.addDisbursementSettledTransaction(request);
        return Optional.of(call);
    }
}
