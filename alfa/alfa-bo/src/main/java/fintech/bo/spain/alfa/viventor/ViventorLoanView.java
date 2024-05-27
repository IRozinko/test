package fintech.bo.spain.alfa.viventor;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ConfirmDialog;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.ViventorLoanDataRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import static fintech.bo.spain.alfa.db.jooq.alfa.tables.ViventorLoanData.VIVENTOR_LOAN_DATA;
import static fintech.bo.spain.alfa.viventor.ViventorConstants.STATUS_OPEN;
import static java.lang.String.format;


@Slf4j
@SpringView(name = ViventorLoanView.NAME)
public class ViventorLoanView extends VerticalLayout implements View {

    public static final String NAME = "viventor-loan";

    private Long entityId;

    @Autowired
    private ViventorLoanQueries viventorLoanQueries;

    @Autowired
    private ViventorComponents viventorComponents;

    @Autowired
    private AlfaApiClient alfaApiClient;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        entityId = Long.parseLong(UrlUtils.getParam(event.getParameters(), UrlUtils.ID));
        refresh();
    }

    private void refresh() {
        removeAllComponents();
        setSpacing(false);
        setMargin(false);

        ViventorLoanDataRecord record = viventorLoanQueries.findById(entityId);
        if (record == null) {
            Notifications.errorNotification("Viventor loan not found");
            return;
        }
        setCaption(format("Viventor Loan %s", record.getViventorLoanId()));

        BusinessObjectLayout layout = new BusinessObjectLayout();
        layout.setTitle(record.getViventorLoanId());
        buildLeft(record, layout);
        buildTabs(record, layout);
        buildActions(record, layout);
        addComponentsAndExpand(layout);
    }

    private void buildLeft(ViventorLoanDataRecord record, BusinessObjectLayout layout) {
        PropertyLayout props = new PropertyLayout("Viventor Loan");
        props.add("Viventor Loan Id", record.get(VIVENTOR_LOAN_DATA.VIVENTOR_LOAN_ID));
        props.addLink("Test Loan Id", record.getLoanId(), LoanComponents.loanLink(record.getLoanId()));
        props.add("Status", record.get(VIVENTOR_LOAN_DATA.STATUS));
        props.add("Status detail", record.get(VIVENTOR_LOAN_DATA.STATUS_DETAIL));
        props.add("Start date", record.get(VIVENTOR_LOAN_DATA.START_DATE));
        props.add("Maturity date", record.get(VIVENTOR_LOAN_DATA.MATURITY_DATE));
        props.add("Principal", record.get(VIVENTOR_LOAN_DATA.PRINCIPAL));
        props.add("Interest rate", record.get(VIVENTOR_LOAN_DATA.INTEREST_RATE));
        props.add("Last synced at", record.get(VIVENTOR_LOAN_DATA.LAST_SYNCED_AT));
        props.add("Loan index", record.get(VIVENTOR_LOAN_DATA.VIVENTOR_LOAN_EXTENSION));
        TextArea viventorLoanDetails = new TextArea("", record.get(VIVENTOR_LOAN_DATA.VIVENTOR_LOAN));
        viventorLoanDetails.setWidth(100, Unit.PERCENTAGE);
        viventorLoanDetails.setHeight(500, Unit.PIXELS);
        props.add("Viventor data", viventorLoanDetails);
        layout.addLeftComponent(props);
    }

    private void buildTabs(ViventorLoanDataRecord record, BusinessObjectLayout layout) {
        layout.addTab("Log", () -> logGrid(record));
    }

    private Component logGrid(ViventorLoanDataRecord record) {
        ViventorLogDataProvider viventorLogDataProvider = viventorComponents.viventorLogDataProvider();
        viventorLogDataProvider.setViventorLoanId(record.getViventorLoanId());
        return viventorComponents.logGrid(viventorLogDataProvider);
    }

    private void buildActions(ViventorLoanDataRecord record, BusinessObjectLayout layout) {
        layout.setRefreshAction(this::refresh);
        if (LoginService.hasPermission(BackofficePermissions.VIVENTOR_LOAN_CLOSE)) {
            MenuBar.MenuItem item = layout.addActionMenuItem("Close in Viventor", e -> closeInViventor(record));
            item.setEnabled(StringUtils.equals(record.getStatus(), STATUS_OPEN));
        }
        if (LoginService.hasPermission(BackofficePermissions.VIVENTOR_LOAN_SYNC)) {
            MenuBar.MenuItem item = layout.addActionMenuItem("Sync with Viventor", e -> syncLoanWithViventor(record));
            item.setEnabled(StringUtils.equals(record.getStatus(), STATUS_OPEN));
        }
    }

    private void closeInViventor(ViventorLoanDataRecord record) {
        ConfirmDialog confirm = new ConfirmDialog("Confirm closing loan in Viventor!", (Button.ClickListener) event -> {
            Call<Void> call = alfaApiClient.closeViventorLoan(new IdRequest(record.getLoanId()));
            BackgroundOperations.callApi("Closing loan in Viventor", call, t -> {
                Notifications.trayNotification("Loan closed in Viventor");
                refresh();
            }, Notifications::errorNotification);
        });
        getUI().addWindow(confirm);
    }

    private void syncLoanWithViventor(ViventorLoanDataRecord record) {
        Call<Void> call =  alfaApiClient.syncViventorLoan(new IdRequest(record.getLoanId()));
        BackgroundOperations.callApi("Syncing loan with Viventor", call, t -> {
            Notifications.trayNotification("Loan synced with Viventor");
            refresh();
        }, Notifications::errorNotification);
    }

}
