package fintech.bo.spain.alfa.loan.discount;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.DateUtils;
import fintech.bo.components.Formats;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.spain.alfa.api.ExtensionDiscountApiClient;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.ExtensionDiscountRecord;
import fintech.spain.alfa.bo.model.CreateExtensionDiscountRequest;
import fintech.spain.alfa.bo.model.EditExtensionDiscountRequest;
import lombok.SneakyThrows;
import retrofit2.Call;

import java.time.LocalDate;

import static com.vaadin.data.ValidationResult.error;
import static com.vaadin.data.ValidationResult.ok;

public abstract class ExtensionDiscountDialog extends ActionDialog {


    private Binder<CreateExtensionDiscountRequest> binder;
    private Binder<EditExtensionDiscountRequest> editExtensionDiscountBinder;
    private Runnable callback;
    protected CreateExtensionDiscountRequest request;
    protected EditExtensionDiscountRequest updateRequest;
    protected ExtensionDiscountApiClient api;

    public static ExtensionDiscountDialog createExtensionDiscount(long loanId, ExtensionDiscountApiClient api, Runnable callback) {
        CreateExtensionDiscountRequest req = new CreateExtensionDiscountRequest();
        req.setLoanId(loanId);
        return new CreateExtensionDiscountDialog(req, api, callback);
    }

    public static ExtensionDiscountDialog updateExtensionDiscount(ExtensionDiscountRecord record, ExtensionDiscountApiClient api, Runnable callback) {
        return new UpdateExtensionDiscountDialog(toRequest(record), api, callback);
    }

    protected ExtensionDiscountDialog(CreateExtensionDiscountRequest request, ExtensionDiscountApiClient api, Runnable callback) {
        super("Extension Discount", "Save");
        this.api = api;
        this.callback = callback;
        setModal(true);
        setWidth(400, Unit.PIXELS);

        this.request = request;
        binder = new Binder<>();
        binder.setBean(request);
        setDialogContent(buildForm());
    }

    protected ExtensionDiscountDialog(EditExtensionDiscountRequest request, ExtensionDiscountApiClient api, Runnable callback) {
        super("Extension Discount", "Save");
        this.api = api;
        this.callback = callback;
        setModal(true);
        setWidth(400, Unit.PIXELS);

        this.updateRequest = request;
        binder = new Binder<>();
        editExtensionDiscountBinder.setBean(updateRequest);
        setDialogContent(buildForm());
    }

    private Component buildForm() {
        FormLayout form = new FormLayout();
        form.setWidthUndefined();

        DateField effectiveFrom = new DateField("Effective from");
        effectiveFrom.setDateFormat(Formats.DATE_FORMAT);
        effectiveFrom.setPlaceholder(Formats.DATE_FORMAT);
        binder.forField(effectiveFrom)
            .asRequired()
            .bind(CreateExtensionDiscountRequest::getEffectiveFrom, CreateExtensionDiscountRequest::setEffectiveFrom);
        form.addComponent(effectiveFrom);

        DateField effectiveTo = new DateField("Effective to");
        effectiveTo.setDateFormat(Formats.DATE_FORMAT);
        effectiveTo.setPlaceholder(Formats.DATE_FORMAT);
        binder.forField(effectiveTo)
            .asRequired()
            .withValidator((value, context) -> DateUtils.lt(value, LocalDate.now()) ? error("Can not be in the past") : ok())
            .bind(CreateExtensionDiscountRequest::getEffectiveTo, CreateExtensionDiscountRequest::setEffectiveTo);
        form.addComponent(effectiveTo);

        TextField discount = new TextField("Discount in percent");
        binder.forField(discount)
            .asRequired()
            .withNullRepresentation("")
            .withConverter(new StringToBigDecimalConverter("Invalid number"))
            .bind(CreateExtensionDiscountRequest::getRateInPercent, CreateExtensionDiscountRequest::setRateInPercent);
        form.addComponent(discount);

        form.forEach(component -> component.setWidth(100, Unit.PERCENTAGE));
        return form;
    }

    protected abstract Call<?> getCall();

    @Override
    @SneakyThrows
    protected void executeAction() {
        BinderValidationStatus<CreateExtensionDiscountRequest> result = binder.validate();
        if (result.isOk()) {
            BackgroundOperations.callApi("Saving extension discount", getCall(), t -> {
                Notifications.trayNotification("Discount saved");
                close();
                callback.run();
            }, Notifications::errorNotification);
        } else {
            result.notifyBindingValidationStatusHandlers();
        }
    }

    private static EditExtensionDiscountRequest toRequest(ExtensionDiscountRecord record) {
        return new EditExtensionDiscountRequest()
            .setEffectiveFrom(record.getEffectiveFrom())
            .setEffectiveTo(record.getEffectiveTo())
            .setLoanId(record.getLoanId())
            .setRateInPercent(record.getRateInPercent());
    }

    private static class CreateExtensionDiscountDialog extends ExtensionDiscountDialog {

        public CreateExtensionDiscountDialog(CreateExtensionDiscountRequest request, ExtensionDiscountApiClient api, Runnable callback) {
            super(request, api, callback);
        }

        @Override
        protected Call<?> getCall() {
            return api.createExtensionDiscount(request);
        }
    }

    private static class UpdateExtensionDiscountDialog extends ExtensionDiscountDialog {

        protected UpdateExtensionDiscountDialog(EditExtensionDiscountRequest request, ExtensionDiscountApiClient api, Runnable callback) {
            super(request, api, callback);
        }

        @Override
        protected Call<?> getCall() {
            return api.editExtensionDiscount(updateRequest);
        }


    }
}
