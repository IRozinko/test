package fintech.bo.spain.alfa.task;

import com.vaadin.data.Binder;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.Converters;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.task.CommonTaskView;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;
import fintech.bo.spain.alfa.api.LoanApplicationApiClient;
import fintech.spain.alfa.bo.model.UpdateLoanUpsellAmountRequest;
import org.apache.commons.lang3.text.WordUtils;

public class UpsellOfferCall extends CommonTaskView {

    private final LoanApplicationApiClient apiClient;

    public UpsellOfferCall() {
        this.apiClient = ApiAccessor.gI().get(LoanApplicationApiClient.class);
    }

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(getHelper().callClientComponent(getTask().getClientId()));
        layout.addComponent(updateLoanUpsellAmount(baseLayout, getTask()));
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));
        return layout;
    }

    private FormLayout updateLoanUpsellAmount(BusinessObjectLayout baseView, TaskRecord task) {
        LoanApplicationRecord loanApplication = getHelper().getLoanApplicationQueries().findById(task.getApplicationId());

        FormLayout form = new FormLayout();
        form.setMargin(false);

        Label titleLabel = new Label(WordUtils.capitalizeFully("Update loan upsell amount"));
        titleLabel.addStyleName(ValoTheme.LABEL_H4);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.addStyleName(ValoTheme.LABEL_COLORED);
        form.addComponent(titleLabel);

        TextField amount = new TextField("Amount");
        amount.setWidth(100, Sizeable.Unit.PERCENTAGE);
        form.addComponent(amount);

        UpdateLoanUpsellAmountRequest request = new UpdateLoanUpsellAmountRequest()
            .setLoanApplicationId(loanApplication.getId())
            .setAmount(loanApplication.getOfferedPrincipal())
            .setDiscountId(loanApplication.getDiscountId());

        Binder<UpdateLoanUpsellAmountRequest> binder = new Binder<>(UpdateLoanUpsellAmountRequest.class);
        binder.setBean(request);
        binder.forField(amount)
            .withConverter(Converters.stringToBigDecimalInputConverter())
            .bind(UpdateLoanUpsellAmountRequest::getAmount, UpdateLoanUpsellAmountRequest::setAmount);

        Button update = new Button("Update");
        update.addStyleName(ValoTheme.BUTTON_PRIMARY);
        update.addClickListener(e -> BackgroundOperations.callApi("Updating loan upsell amount", apiClient.updateLoanUpsellAmount(request), t -> {
            Notifications.trayNotification("Loan upsell amount updated");

            baseView.refresh();
        }, Notifications::errorNotification));
        form.addComponent(update);

        return form;
    }

}
