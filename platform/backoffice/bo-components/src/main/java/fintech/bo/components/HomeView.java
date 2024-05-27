package fintech.bo.components;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.activity.ActivityComponents;
import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.components.application.LoanApplicationQueries;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.AbstractClientView;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.ClientQueries;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.dc.DcComponents;
import fintech.bo.components.dc.DcQueries;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.db.jooq.dc.tables.records.DebtRecord;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = "")
public class HomeView extends VerticalLayout implements View {

    @Autowired
    private ClientComponents clientComponents;

    @Autowired
    private LoanApplicationComponents loanApplicationComponents;

    @Autowired
    private DcComponents dcComponents;

    @Autowired
    private LoanComponents loanComponents;

    @Autowired
    private LoanQueries loanQueries;

    @Autowired
    private LoanApplicationQueries loanApplicationQueries;

    @Autowired
    private DcQueries dcQueries;

    @Autowired
    private ClientQueries clientQueries;

    @Autowired
    private ActivityComponents activityComponents;

    private AbstractOrderedLayout clientLayout;
    private VerticalLayout activityLayout;


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Home");
        setSizeFull();
        setSpacing(true);
        setMargin(true);

        Label title = new Label("Welcome to Backoffice!");
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.setSizeUndefined();

        clientLayout = new VerticalLayout();
        clientLayout.setMargin(true);
        clientLayout.setSpacing(false);

        activityLayout = new VerticalLayout();

        Panel clientPanel = new Panel();
        clientPanel.addStyleName(BackofficeTheme.BACKGROUND_GRAY);
        clientPanel.setSizeFull();
        clientPanel.setContent(clientLayout);

        Panel activityPanel = new Panel();
        activityPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        activityPanel.setSizeFull();
        activityPanel.setContent(activityLayout);
        activityPanel.setSizeFull();

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(600, Unit.PIXELS);
        splitPanel.addComponent(clientPanel);
        splitPanel.addComponent(activityPanel);

        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);
        root.setWidthUndefined();
        root.addComponent(clientsComboBox());
        root.addComponentsAndExpand(splitPanel);
        root.setSizeFull();

        addComponent(root);

        tryClientSearchFromUrlParams(event);
    }

    private void tryClientSearchFromUrlParams(ViewChangeListener.ViewChangeEvent event) {
        String phoneNumber = UrlUtils.getParam(event.getParameters(), UrlUtils.TEL);
        if (phoneNumber == null) {
            return;
        }

        BackgroundOperations.run("Searching client...",
            operation -> clientQueries.findByPhone(phoneNumber),
            maybeClientId -> {
                if (maybeClientId.isPresent()) {
                    String clientViewLink = AbstractClientView.NAME + "/" + maybeClientId.get();
                    UI.getCurrent().getNavigator().navigateTo(clientViewLink);
                } else {
                    Notifications.trayNotification("Client not found", "Phone= " + phoneNumber);
                }
            },
            Notifications::errorNotification
        );
    }

    private ComboBox<ClientDTO> clientsComboBox() {
        ComboBox<ClientDTO> clientsComboBox = clientComponents.clientsComboBox();
        clientsComboBox.setWidth(600, Unit.PIXELS);
        clientsComboBox.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                return;
            }
            displayClient(event.getValue());
            ((ComboBox) event.getComponent()).clear();
        });
        clientsComboBox.setCaption("Search clients");
        clientsComboBox.setPlaceholder("Search...");
        clientsComboBox.focus();

        return clientsComboBox;
    }

    private void displayClient(ClientDTO client) {
        clientLayout.removeAllComponents();
        Result<LoanRecord> openLoans = loanQueries.findOpen(client.getId());
        clientLayout.addComponent(clientComponents.clientInfoSimple(client, false));
        openLoans.forEach(loan -> {
            clientLayout.addComponent(loanComponents.loanInfoSimple(loan));
        });
        Result<DebtRecord> debts = dcQueries.findDebtsByClientId(client.getId());
        debts.forEach(debt -> {
            clientLayout.addComponent(dcComponents.debtInfoSimple(debt));
        });
        LoanApplicationRecord application = loanApplicationQueries.findLatestByClientId(client.getId());
        if (application != null) {
            clientLayout.addComponent(loanApplicationComponents.loanApplicationInfoSimple(application).setTitle("Last application"));
        }


        activityLayout.removeAllComponents();
        activityLayout.addComponent(activityComponents.latestActivities(client.getId()));
    }
}
