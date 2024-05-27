package fintech.bo.components.payments.disbursement;


import com.google.common.eventbus.Subscribe;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.PollingScheduler;
import fintech.bo.components.background.BackgroundOperations;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class DisbursementQueueComponent extends CustomComponent {

    private static final int REQUEST_INTERVAL = 5;

    private final Button exportBtn;
    private PollingScheduler pollingScheduler;
    private LocalDateTime lastRefreshed = LocalDateTime.now().minusSeconds(REQUEST_INTERVAL);
    private DisbursementQueueCache disbursementQueueCache;

    public DisbursementQueueComponent(UI ui, DisbursementQueueCache cache, PollingScheduler pollingScheduler) {
        this.pollingScheduler = pollingScheduler;
        this.disbursementQueueCache = cache;

        exportBtn = new Button("");
        exportBtn.addStyleName(ValoTheme.BUTTON_DANGER);
        exportBtn.addStyleName(ValoTheme.BUTTON_SMALL);
        exportBtn.setVisible(false);
        exportBtn.addClickListener(e -> ui.getNavigator().navigateTo(DisbursementExportView.NAME));


        HorizontalLayout layout = new HorizontalLayout();
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        layout.addComponent(exportBtn);
        setCompositionRoot(layout);
    }


    private void request() {
        BackgroundOperations.runSilent(feedback -> disbursementQueueCache.countPendingDisbursements(), count -> {
            exportBtn.setComponentError(null);
            exportBtn.setVisible(count != 0);
            if (count < -1) {
                exportBtn.setCaption("Disb. count failed");
            } else if (count > 0) {
                exportBtn.setCaption(String.format("Disburse (%s)", count));
            }
        }, error -> {
        });
    }

    private long lastRefreshedInSeconds() {
        return SECONDS.between(lastRefreshed, LocalDateTime.now());
    }

    @Override
    public void attach() {
        super.attach();
        pollingScheduler.subscribe(this);
    }

    @Override
    public void detach() {
        super.detach();
        pollingScheduler.unsubscribe(this);
    }

    @Subscribe
    public void onEvent(String tick) {
        if (lastRefreshedInSeconds() > REQUEST_INTERVAL) {
            lastRefreshed = LocalDateTime.now();
            getUI().access(this::request);
        }
    }
}
