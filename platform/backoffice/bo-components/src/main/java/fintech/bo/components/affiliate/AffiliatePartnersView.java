package fintech.bo.components.affiliate;

import com.vaadin.data.Binder;
import com.vaadin.data.ValueProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Setter;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.AffiliateApiClient;
import fintech.bo.api.model.affiliate.SavePartnerRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.util.UUID;

import static fintech.bo.db.jooq.affiliate.Tables.PARTNER;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.AFFILIATE_EDIT})
@SpringView(name = AffiliatePartnersView.NAME)
public class AffiliatePartnersView extends VerticalLayout implements View {

    public static final String NAME = "affiliate-partners";

    @Autowired
    private DSLContext db;

    @Autowired
    private AffiliateApiClient affiliateApiClient;

    private AffiliatePartnerDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Affiliate partners");

        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new AffiliatePartnerDataProvider(db);
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Edit", this::edit);
        builder.addColumn(PARTNER.NAME);
        builder.addColumn(PARTNER.ACTIVE);
        builder.addColumn(PARTNER.LEAD_REPORT_URL);
        builder.addColumn(PARTNER.REPEATED_CLIENT_LEAD_REPORT_URL);
        builder.addColumn(PARTNER.ACTION_REPORT_URL);
        builder.addColumn(PARTNER.REPEATED_CLIENT_ACTION_REPORT_URL);
        builder.addColumn(PARTNER.LEAD_CONDITION_WORKFLOW_ACTIVITY_NAME);
        builder.addColumn(PARTNER.LEAD_CONDITION_WORKFLOW_ACTIVITY_RESOLUTION);
        builder.addAuditColumns(PARTNER);
        builder.addColumn(PARTNER.ID);
        layout.setContent(builder.build(dataProvider));
    }

    private void edit(Record record) {
        SavePartnerRequest request = new SavePartnerRequest();
        if (record != null) {
            request.setName(record.get(PARTNER.NAME));
            request.setActive(record.get(PARTNER.ACTIVE));
            request.setLeadReportUrl(record.get(PARTNER.LEAD_REPORT_URL));
            request.setRepeatedClientLeadReportUrl(record.get(PARTNER.REPEATED_CLIENT_LEAD_REPORT_URL));
            request.setActionReportUrl(record.get(PARTNER.ACTION_REPORT_URL));
            request.setRepeatedClientActionReportUrl(record.get(PARTNER.REPEATED_CLIENT_ACTION_REPORT_URL));
            request.setLeadConditionWorkflowActivityName(record.get(PARTNER.LEAD_CONDITION_WORKFLOW_ACTIVITY_NAME));
            request.setLeadConditionWorkflowActivityResolution(record.get(PARTNER.LEAD_CONDITION_WORKFLOW_ACTIVITY_RESOLUTION));
            request.setApiKey(record.get(PARTNER.API_KEY));

        }
        EditDialog dialog = new EditDialog(affiliateApiClient, request);
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void buildTop(GridViewLayout layout) {
        layout.setRefreshAction((e) -> refresh());
        layout.addActionMenuItem("Add new partner", e -> edit(null));
    }

    private void refresh() {
        dataProvider.refreshAll();
    }

    public static class EditDialog extends ActionDialog {


        private AffiliateApiClient apiClient;
        private SavePartnerRequest request;
        private Binder<SavePartnerRequest> binder;
        private VerticalLayout layout;

        public EditDialog(AffiliateApiClient apiClient, SavePartnerRequest request) {
            super("Edit", "Save");
            this.apiClient = apiClient;
            this.request = request;
            setWidth(600, Unit.PIXELS);
            buildForm();
            setDialogContent(layout);
        }

        private void buildForm() {
            binder = new Binder<>();
            binder.setBean(request);

            layout = new VerticalLayout();
            layout.setWidthUndefined();

            TextField name = addTextField(new TextField("Name"), SavePartnerRequest::getName, SavePartnerRequest::setName);
            name.setReadOnly(!StringUtils.isBlank(request.getName()));
            name.focus();

            CheckBox active = new CheckBox("Active");
            binder.forField(active).bind(SavePartnerRequest::isActive, SavePartnerRequest::setActive);
            layout.addComponent(active);

            TextArea leadUrl = addTextField(new TextArea("Lead report URL"), SavePartnerRequest::getLeadReportUrl, SavePartnerRequest::setLeadReportUrl);
            leadUrl.setRows(5);
            leadUrl.addStyleName(BackofficeTheme.TEXT_MONO);
            leadUrl.setWordWrap(true);

            TextArea repeatedClientLeadUrl = addTextField(new TextArea("Lead report URL for repeated clients"), SavePartnerRequest::getRepeatedClientLeadReportUrl, SavePartnerRequest::setRepeatedClientLeadReportUrl);
            repeatedClientLeadUrl.setRows(5);
            repeatedClientLeadUrl.addStyleName(BackofficeTheme.TEXT_MONO);
            repeatedClientLeadUrl.setWordWrap(true);

            addTextField(new TextField("Lead condition - workflow activity"), SavePartnerRequest::getLeadConditionWorkflowActivityName, SavePartnerRequest::setLeadConditionWorkflowActivityName);
            addTextField(new TextField("Lead condition - activity resolution"), SavePartnerRequest::getLeadConditionWorkflowActivityResolution, SavePartnerRequest::setLeadConditionWorkflowActivityResolution);

            TextArea actionUrl = addTextField(new TextArea("Action report URL"), SavePartnerRequest::getActionReportUrl, SavePartnerRequest::setActionReportUrl);
            actionUrl.setRows(5);
            actionUrl.addStyleName(BackofficeTheme.TEXT_MONO);
            actionUrl.setWordWrap(true);

            TextArea repeatedClientActionUrl = addTextField(new TextArea("Action report URL for repeated clients"), SavePartnerRequest::getRepeatedClientActionReportUrl, SavePartnerRequest::setRepeatedClientActionReportUrl);
            repeatedClientActionUrl.setRows(5);
            repeatedClientActionUrl.addStyleName(BackofficeTheme.TEXT_MONO);
            repeatedClientActionUrl.setWordWrap(true);

            TextField apiKey = addTextField(new TextField("API key"), SavePartnerRequest::getApiKey, SavePartnerRequest::setApiKey);

            Button generateApiKey = new Button("Generate API key");
            generateApiKey.addClickListener(e -> {
                apiKey.setValue(UUID.randomUUID().toString());
            });
            layout.addComponent(generateApiKey);
        }

        private <T extends AbstractTextField> T addTextField(T field, ValueProvider<SavePartnerRequest, String> getter, Setter<SavePartnerRequest, String> setter) {
            field.setWidth(100, Unit.PERCENTAGE);
            binder.forField(field).bind(getter, setter);
            layout.addComponent(field);
            return field;
        }

        @Override
        protected void executeAction() {
            Call<Void> call = apiClient.savePartner(request);
            BackgroundOperations.callApi("Saving partner", call, t -> {
                Notifications.trayNotification("Partner saved");
                close();
            }, Notifications::errorNotification);
        }
    }
}
