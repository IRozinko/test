package fintech.bo.components.dc;

import com.vaadin.data.Binder;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.TimeMachine;
import fintech.bo.api.client.CalendarApiClient;
import fintech.bo.api.model.calendar.BusinessDaysRequest;
import fintech.bo.api.model.calendar.BusinessDaysResponse;
import fintech.bo.api.model.dc.LogDebtActionRequest;
import fintech.bo.components.Converters;
import fintech.bo.components.Formats;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import lombok.Data;
import retrofit2.Call;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

public class PromisedToPayComponent extends FormLayout implements BulkActionComponent {

    private Model model;
    private NewActionComponent actionPanel;
    private TextField promiseAmount;
    private DateField promiseDate;
    private final CalendarApiClient calendarApiClient;

    public PromisedToPayComponent(CalendarApiClient calendarApiClient) {
        this.calendarApiClient = calendarApiClient;
    }

    @Data
    public static class Model {
        private LocalDate dueDate;
        private BigDecimal amount;
    }

    @Override
    public void build(NewActionComponent actionPanel, DcSettingsJson.BulkAction bulkAction) {
        setMargin(true);
        this.actionPanel = actionPanel;
        this.model = new Model();
        this.model.setAmount(actionPanel.getDebt().getTotalDue());
        Integer promiseDueDateInDays = bulkAction.getRequiredParam("promiseDueDateInDays", Integer.class);

        Binder<Model> binder = new Binder<>(Model.class);
        binder.setBean(this.model);

        promiseDate = new DateField("Promise date");
        promiseDate.setWidth(NewActionComponent.FIELD_WIDTH, Unit.PIXELS);
        promiseDate.setDateFormat(Formats.LONG_DATE_FORMAT);
        promiseDate.setRangeStart(TimeMachine.today());
        binder.forField(promiseDate).bind(Model::getDueDate, Model::setDueDate);

        // Resolving dueDate for promiseDate through WEB API
        addComponent(promiseDate);
        resolveBusinessTime(promiseDueDateInDays, time -> {
            LocalDate dueDate = time.toLocalDate();
            binder.getBean().setDueDate(dueDate);
            promiseDate.setValue(dueDate);
            promiseDate.setRangeEnd(dueDate);
        });
        promiseAmount = new TextField("Promise amount");
        promiseAmount.setWidth(NewActionComponent.FIELD_WIDTH, Unit.PIXELS);
        binder.forField(promiseAmount).withConverter(Converters.stringToBigDecimalInputConverter())
            .bind(Model::getAmount, Model::setAmount);
        addComponent(promiseAmount);

        promiseDate.addValueChangeListener(e -> updateNextActionAt());
    }

    private void resolveBusinessTime(int daysToAdd, Consumer<LocalDateTime> timeConsumer) {
        BusinessDaysRequest request = new BusinessDaysRequest()
            .setOrigin(TimeMachine.now()).setAmountToAdd(daysToAdd).setUnit(ChronoUnit.DAYS);

        Call<BusinessDaysResponse> call = calendarApiClient.resolveBusinessTime(request);
        BackgroundOperations.callApiSilent(call, r -> timeConsumer.accept(r.getBusinessTime()), Notifications::errorNotification);
    }

    @Override
    public LogDebtActionRequest.BulkAction saveData() {
        LogDebtActionRequest.BulkAction data = new LogDebtActionRequest.BulkAction();
        data.getParams().put("dueDate", this.model.getDueDate());
        data.getParams().put("amount", this.model.getAmount());
        return data;
    }

    private void updateNextActionAt() {
        if (this.promiseDate.getValue() != null) {
            actionPanel.getNextActionAtField().setRangeEnd(this.promiseDate.getValue().atStartOfDay().plusDays(1));
            actionPanel.getNextActionAtField().setValue(this.promiseDate.getValue().atStartOfDay().withHour(9));
        }
    }
}
