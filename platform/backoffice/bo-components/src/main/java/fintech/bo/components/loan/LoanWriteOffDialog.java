package fintech.bo.components.loan;

import com.vaadin.data.Binder;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.BigDecimalUtils;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.model.loan.WriteOffLoanAmountRequest;
import fintech.bo.components.Converters;
import fintech.bo.components.Formats;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import retrofit2.Call;

import java.time.LocalDate;

public class LoanWriteOffDialog extends ActionDialog {

    private WriteOffLoanAmountRequest request;
    private LoanRecord loanRecord;
    private LoanApiClient loanApiClient;
    private Binder<WriteOffLoanAmountRequest> binder;

    public LoanWriteOffDialog(LoanRecord loanRecord, LoanApiClient loanApiClient) {
        super("Write off", "Save");
        this.loanRecord = loanRecord;
        this.loanApiClient = loanApiClient;
        this.request = new WriteOffLoanAmountRequest();
        this.request.setWhen(LocalDate.now());
        this.request.setLoanId(loanRecord.getId());
        this.request.setInterest(loanRecord.getInterestOutstanding());
        this.request.setPenalty(loanRecord.getPenaltyOutstanding());
        this.binder = new Binder<>();
        this.binder.setBean(this.request);
        setDialogContent(content());
        setModal(true);
        setWidth(400, Sizeable.Unit.PIXELS);
    }

    private Component content() {
        VerticalLayout layout = new VerticalLayout();

        DateField date = new DateField("Transaction date");
        date.setDateFormat(Formats.DATE_FORMAT);
        date.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(date);
        binder.forField(date).bind(WriteOffLoanAmountRequest::getWhen, WriteOffLoanAmountRequest::setWhen);

        TextField principal = new TextField("Principal");
        principal.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(principal);
        binder.forField(principal).withConverter(Converters.stringToBigDecimalInput4Converter())
            .bind(WriteOffLoanAmountRequest::getPrincipal, WriteOffLoanAmountRequest::setPrincipal);

        TextField interest = new TextField("Interest");
        interest.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(interest);
        binder.forField(interest).withConverter(Converters.stringToBigDecimalInput4Converter())
            .bind(WriteOffLoanAmountRequest::getInterest, WriteOffLoanAmountRequest::setInterest);

        TextField penalty = new TextField("Penalty");
        penalty.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(penalty);
        binder.forField(penalty).withConverter(Converters.stringToBigDecimalInput4Converter())
            .bind(WriteOffLoanAmountRequest::getPenalty, WriteOffLoanAmountRequest::setPenalty);

        TextField fee = new TextField("Fee");
        fee.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(fee);
        binder.forField(fee).withConverter(Converters.stringToBigDecimalInput4Converter())
            .bind(WriteOffLoanAmountRequest::getFee, WriteOffLoanAmountRequest::setFee);

        TextField comment = new TextField("Comment");
        comment.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(comment);
        binder.forField(comment).bind(WriteOffLoanAmountRequest::getComment, WriteOffLoanAmountRequest::setComment);

        return layout;
    }

    @Override
    protected void executeAction() {
        if (!BigDecimalUtils.isPositive(request.getInterest())
            && !BigDecimalUtils.isPositive(request.getPenalty())
            && !BigDecimalUtils.isPositive(request.getPrincipal())
            && !BigDecimalUtils.isPositive(request.getFee())) {
            Notifications.errorNotification("Can not create transaction with 0 amount");
            return;
        }
        Call<Void> call = loanApiClient.writeOff(request);
        BackgroundOperations.callApi("Saving write-off transaction", call, t -> {
            Notifications.trayNotification("Write-off transaction saved");
            close();
        }, Notifications::errorNotification);
    }
}
