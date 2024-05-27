package fintech.bo.components.quartz;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.QuartzApiClient;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import static fintech.bo.components.quartz.QuartzTriggersDataProvider.FIELD_NEXT_FIRE_TIME_DATE;
import static fintech.bo.components.quartz.QuartzTriggersDataProvider.FIELD_PAUSED_TRIGGER;
import static fintech.bo.components.quartz.QuartzTriggersDataProvider.FIELD_PREV_FIRE_TIME_DATE;
import static fintech.bo.db.jooq.quartz.tables.QrtzTriggers.QRTZ_TRIGGERS;


@Slf4j
@SecuredView({BackofficePermissions.ADMIN})
@SpringView(name = SchedulersView.NAME)
public class SchedulersView extends VerticalLayout implements View {

    public static final String NAME = "schedulers";

    @Autowired
    private DSLContext db;

    @Autowired
    private QuartzApiClient quartzApiClient;

    private QuartzTriggersDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Schedulers");

        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new QuartzTriggersDataProvider(db);
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(QRTZ_TRIGGERS.JOB_NAME).setWidth(300);
        builder.addColumn(QRTZ_TRIGGERS.TRIGGER_STATE);
        builder.addColumn(FIELD_PAUSED_TRIGGER);
        builder.addColumn(FIELD_NEXT_FIRE_TIME_DATE);
        builder.addColumn(FIELD_PREV_FIRE_TIME_DATE);
        builder.addColumn(QRTZ_TRIGGERS.TRIGGER_TYPE);
        builder.addActionColumn("Pause", this::pauseJob, this::isPaused);
        builder.addActionColumn("Resume", this::resumeJob, r -> !this.isPaused(r));
        builder.addActionColumn("Delete", this::deleteJob);
        builder.sortAsc(QRTZ_TRIGGERS.JOB_NAME);
        layout.setContent(builder.build(dataProvider));
    }

    private void buildTop(GridViewLayout layout) {
        layout.setRefreshAction((e) -> refresh());
        layout.addActionMenuItem("Pause scheduler", event -> pauseScheduler());
        layout.addActionMenuItem("Resume scheduler", event -> resumeScheduler());
    }

    private void refresh() {
        dataProvider.refreshAll();
    }

    private boolean isPaused(Record record) {
        return record.get(QRTZ_TRIGGERS.TRIGGER_STATE).equals("PAUSED")
            || record.get(QRTZ_TRIGGERS.TRIGGER_STATE).equals("PAUSED_BLOCKED")
            || record.get(FIELD_PAUSED_TRIGGER);
    }

    private void pauseJob(Record record) {
        String jobName = record.get(QRTZ_TRIGGERS.JOB_NAME);
        Dialogs.confirm("Pause job '" + jobName + "' ?", e -> {
            Call<Void> call = quartzApiClient.pauseJob(jobName);
            BackgroundOperations.callApi("Pausing job", call, v -> {
                Notifications.trayNotification(jobName + " paused");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void resumeJob(Record record) {
        String jobName = record.get(QRTZ_TRIGGERS.JOB_NAME);
        Dialogs.confirm("Resume job '" + jobName + "' ?", e -> {
            Call<Void> call = quartzApiClient.resumeJob(jobName);
            BackgroundOperations.callApi("Resuming job", call, v -> {
                Notifications.trayNotification(jobName + " resumed");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void deleteJob(Record record) {
        String jobName = record.get(QRTZ_TRIGGERS.JOB_NAME);
        Dialogs.confirm("Delete job '" + jobName + "' ?", e -> {
            Call<Void> call = quartzApiClient.deleteJob(jobName);
            BackgroundOperations.callApi("Deleting job", call, v -> {
                Notifications.trayNotification(jobName + " deleted");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void pauseScheduler() {
        Dialogs.confirm("Pause scheduler?", e -> {
            Call<Void> call = quartzApiClient.pauseScheduler();
            BackgroundOperations.callApi("Pausing scheduler", call, v -> {
                Notifications.trayNotification("Scheduler paused");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void resumeScheduler() {
        Dialogs.confirm("Resume scheduler?", e -> {
            Call<Void> call = quartzApiClient.resumeScheduler();
            BackgroundOperations.callApi("Resuming scheduler", call, v -> {
                Notifications.trayNotification("Scheduler resumed");
                refresh();
            }, Notifications::errorNotification);
        });
    }
}
