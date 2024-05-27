package fintech.bo.spain.alfa.loan;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fintech.TimeMachine;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.model.loan.GetExtensionPricesRequest;
import fintech.bo.api.model.loan.GetExtensionPricesResponse;
import fintech.bo.components.Formats;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dc.DcQueries;
import fintech.bo.components.dc.DcSettingsJson;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.spain.alfa.bo.model.CalculatePenaltyRequest;
import fintech.spain.alfa.bo.model.CalculatePenaltyResponse;
import fintech.spain.alfa.bo.model.CalculatePrepaymentRequest;
import fintech.spain.alfa.bo.model.CalculatePrepaymentResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.ui.themes.ValoTheme.PANEL_BORDERLESS;
import static fintech.bo.components.BackofficeTheme.MIN_WIDTH_800;

public class PaymentSimulationComponent extends Panel implements BoComponent {

    @Autowired
    private AlfaApiClient alfaApiClient;

    @Autowired
    private LoanApiClient loanApiClient;

    @Autowired
    private DcQueries dcQueries;

    @Autowired
    private LoanQueries loanQueries;

    private Long loanId;
    private HorizontalLayout parentLayout;
    private DateField date;

    @Override
    public void setUp(BoComponentContext context) {


        loanId = context.scope(StandardScopes.SCOPE_LOAN).orElseThrow(() -> new IllegalStateException("No loan provided"));
        date = new DateField("Payment simulation date", TimeMachine.today());
        date.setDateFormat(Formats.DATE_FORMAT);
        date.addValueChangeListener(e -> refresh());
        date.setTextFieldEnabled(false);

        parentLayout = new HorizontalLayout();
        parentLayout.setMargin(new MarginInfo(true, false, false, false));

        VerticalLayout content = new VerticalLayout();
        content.addComponent(date);
        content.addComponent(parentLayout);
        content.setSizeUndefined();
        content.setWidth(100, PERCENTAGE);
        content.addStyleName(MIN_WIDTH_800);

        setSizeFull();
        addStyleName(PANEL_BORDERLESS);
        setContent(content);

    }

    @Override
    public void refresh() {
        parentLayout.removeAllComponents();
        BackgroundOperations.callApiSilent(loanApiClient.getExtensionPrices(new GetExtensionPricesRequest(loanId, date.getValue())), extensionPricesResponse -> {
            extensionOffers(parentLayout, extensionPricesResponse);
            BackgroundOperations.callApiSilent(alfaApiClient.calculatePrepayment(new CalculatePrepaymentRequest(loanId, date.getValue())), calculatePrepaymentResponse -> {
                prepaymentOffer(parentLayout, calculatePrepaymentResponse);
                BackgroundOperations.callApiSilent(alfaApiClient.calculatePenalty(new CalculatePenaltyRequest(loanId, date.getValue())), calculatePenaltyResponse ->
                    penaltyInfo(parentLayout, calculatePenaltyResponse), Notifications::errorNotification);
            }, Notifications::errorNotification);
        }, Notifications::errorNotification);

    }

    private void penaltyInfo(HorizontalLayout parentLayout, CalculatePenaltyResponse response) {
        PropertyLayout layout = new PropertyLayout("Penalty simulation");
        layout.setMargin(false);

        if (!response.isPenaltyApplicable()) {
            layout.addWarning("Penalties not applicable to a date with no DPD");
        } else {
            layout.add("Principal due", response.getPrincipalDue());
            layout.add("Interest due", response.getInterestDue());
            layout.add("Fee due", response.getFeeDue());
            layout.add("Current penalty due", response.getPenaltyDue());
            layout.add("New penalty", response.getNewPenalty());
            layout.add("Total penalty due", response.getTotalPenaltyDue());
            layout.add("Total due", response.getTotalDue());
        }

        parentLayout.addComponentsAndExpand(layout);
    }

    private void extensionOffers(HorizontalLayout parentLayout, GetExtensionPricesResponse response) {
        DcSettingsJson.ExtensionSettings settings = dcQueries.getSettings().getExtensionSettings();
        LoanRecord loanRecord = loanQueries.findById(loanId);
        int extensionDays = loanRecord.getExtendedByDays();
        PropertyLayout layout = new PropertyLayout("Extension offers");
        layout.setMargin(false);

        if (response.getExtensions().isEmpty()) {
            layout.addWarning("No extension offers available");
        }
        response.getExtensions().forEach(extension -> {
            if (extensionDays + extension.getPeriodCount() <= settings.getMaxPeriodDays()) {
                if (!extension.getPrice().equals(extension.getPriceWithDiscount())) {
                    layout.add(String.format("%s %s", extension.getPeriodCount(), StringUtils.lowerCase(extension.getPeriodUnit())), Formats.decimalFormat().format(extension.getPriceWithDiscount()));
                } else {
                    layout.add(String.format("%s %s", extension.getPeriodCount(), StringUtils.lowerCase(extension.getPeriodUnit())), extension.getPrice());
                }
            }
        });
        layout.addSpacer();
        parentLayout.addComponentsAndExpand(layout);
    }

    private void prepaymentOffer(HorizontalLayout parentLayout, CalculatePrepaymentResponse response) {
        PropertyLayout layout = new PropertyLayout("Prepayment offer");
        layout.setMargin(false);

        if (!response.isPrepaymentAvailable()) {
            layout.addWarning("No prepayment offer available");
        } else {
            layout.add("Principal to pay", response.getPrincipalToPay());
            layout.add("Interest to pay", response.getInterestToPay());
            layout.add("Interest to write off", response.getInterestToWriteOff());
            layout.add("Prepayment fee to pay", response.getPrepaymentFeeToPay());
            layout.add("Total to pay", response.getTotalToPay());
        }

        parentLayout.addComponentsAndExpand(layout);
    }
}
