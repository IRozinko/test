package fintech.bo.components.loan.promocodes;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import fintech.DateUtils;
import fintech.bo.api.client.PromoCodeApiClient;
import fintech.bo.api.model.loan.EditPromoCodeRequest;
import fintech.bo.components.Formats;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;

import java.time.LocalDate;

import static com.vaadin.data.ValidationResult.error;
import static com.vaadin.data.ValidationResult.ok;

public class EditPromoCodeDialog extends ActionDialog {

    private final PromoCodeApiClient promoCodeApi;
    private final PromoCodesComponents promoCodesComponents;
    private final Binder<EditPromoCodeRequest> binder;

    EditPromoCodeDialog(PromoCodeApiClient promoCodeApi,
                        PromoCodesComponents promoCodesComponents,
                        EditPromoCodeRequest request) {
        super("Edit Promo code", "Save");
        this.promoCodeApi = promoCodeApi;
        this.promoCodesComponents = promoCodesComponents;

        binder = new Binder<>();
        binder.setBean(request);

        setDialogContent(createForm());
        setWidth(600, Unit.PIXELS);
    }

    private Component createForm() {
        FormLayout form = new FormLayout();
        form.setMargin(true);

        TextArea description = new TextArea("Description");
        binder.forField(description)
            .bind(EditPromoCodeRequest::getDescription, EditPromoCodeRequest::setDescription);
        form.addComponent(description);

        DateField effectiveFrom = new DateField("Effective from");
        effectiveFrom.setDateFormat(Formats.DATE_FORMAT);
        effectiveFrom.setPlaceholder(Formats.DATE_FORMAT);
        binder.forField(effectiveFrom)
            .asRequired()
            .bind(EditPromoCodeRequest::getEffectiveFrom, EditPromoCodeRequest::setEffectiveFrom);
        form.addComponent(effectiveFrom);

        DateField effectiveTo = new DateField("Effective to");
        effectiveTo.setDateFormat(Formats.DATE_FORMAT);
        effectiveTo.setPlaceholder(Formats.DATE_FORMAT);
        binder.forField(effectiveTo)
            .asRequired()
            .withValidator((value, context) -> DateUtils.lt(value, LocalDate.now()) ? error("Can not be in the past") : ok())
            .bind(EditPromoCodeRequest::getEffectiveTo, EditPromoCodeRequest::setEffectiveTo);
        form.addComponent(effectiveTo);

        TextField discount = new TextField("Discount in percent");
        binder.forField(discount)
            .asRequired()
            .withNullRepresentation("")
            .withConverter(new StringToBigDecimalConverter("Invalid number"))
            .bind(EditPromoCodeRequest::getRateInPercent, EditPromoCodeRequest::setRateInPercent);
        form.addComponent(discount);

        TextField maxTimesToUse = new TextField("Max times to use");
        binder.forField(maxTimesToUse)
            .asRequired()
            .withNullRepresentation("")
            .withConverter(new StringToLongConverter("Invalid number"))
            .bind(EditPromoCodeRequest::getMaxTimesToApply, EditPromoCodeRequest::setMaxTimesToApply);
        form.addComponent(maxTimesToUse);

        TwinColSelect<String> affiliates = promoCodesComponents.sourceSelector();
        binder.forField(affiliates)
            .bind(EditPromoCodeRequest::getSources, EditPromoCodeRequest::setSources);
        form.addComponent(affiliates);

        form.forEach(component -> component.setWidth(100, Unit.PERCENTAGE));
        return form;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            EditPromoCodeRequest request = binder.getBean();
            BackgroundOperations.callApi("Updating promo code", promoCodeApi.editPromoCode(request), t -> {
                Notifications.trayNotification("Updated");
                close();
            }, Notifications::errorNotification);
        }
    }

}
