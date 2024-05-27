package fintech.bo.spain.alfa.task;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.Validate;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.dowjones.MatchResultDataProvider;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.task.CommonTaskView;
import fintech.bo.db.jooq.workflow.tables.records.WorkflowAttributeRecord;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.bo.spain.alfa.client.EditClientDataDialog;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.util.Optional;

import static fintech.bo.db.jooq.dowjones.Tables.MATCH;
import static fintech.bo.db.jooq.dowjones.tables.Request.REQUEST;

public class DowJonesCheckTask extends CommonTaskView {

    private final ClientRepository clientRepository;
    private ClientDTO client;
    private final AlfaApiClient alfaApiClient;

    private final DSLContext db;

    public DowJonesCheckTask() {
        this.clientRepository = ApiAccessor.gI().get(ClientRepository.class);
        this.alfaApiClient = ApiAccessor.gI().get(AlfaApiClient.class);
        this.db = ApiAccessor.gI().get(DSLContext.class);
    }

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        this.client = clientRepository.getRequired(getTask().getClientId());
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(clientComponent());
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));
        customActions(baseLayout);

        Optional<WorkflowAttributeRecord> dowjonesIdMaybe = getHelper().getWorkflowQueries().findAttributeByKey(getTask().getWorkflowId(), "DowJonesResponseId");
        Validate.isTrue(dowjonesIdMaybe.isPresent(), "No DowJonesResponseId attribute available in workflow");
        Long dowjonesRequestId = Long.valueOf(dowjonesIdMaybe.get().getValue());

        baseLayout.addTab("DowJones response", () -> dowJonesTab(getTask().getAgent(), dowjonesRequestId));
        return layout;
    }

    private void customActions(BusinessObjectLayout baseTaskView) {
        baseTaskView.addActionMenuItem("Edit personal data", (event) -> {
            ClientDTO client = clientRepository.getRequired(getTask().getClientId());
            EditClientDataDialog dialog = new EditClientDataDialog(client, alfaApiClient);
            dialog.addCloseListener((e) -> baseTaskView.refresh());
            UI.getCurrent().addWindow(dialog);
        });
    }

    private Component dowJonesTab(String taskAgent, Long dowjonesId) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(matchResultTab(dowjonesId));
        boolean assignedToMe = StringUtils.equalsIgnoreCase(taskAgent, LoginService.getLoginData().getUser());
        verticalLayout.setEnabled(assignedToMe);
        return verticalLayout;
    }

    private Grid<Record> matchResultTab(Long dowjonesId) {
        MatchResultDataProvider dataProvider = new MatchResultDataProvider(db);
        dataProvider.setDowjonesRequestId(dowjonesId);
        return attachmentGrid(dataProvider);
    }

    public Grid<Record> attachmentGrid(MatchResultDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addLinkColumn(REQUEST.CLIENT_ID, r -> ClientComponents.clientLink(r.get(REQUEST.CLIENT_ID)));
        builder.addColumn(MATCH.PRIMARY_NAME);
        builder.addColumn(MATCH.GENDER);
        builder.addColumn(MATCH.SCORE);
        builder.addColumn(MATCH.RISK_INDICATOR);
        builder.addColumn(MATCH.COUNTRY_CODE);
        builder.addColumn(MATCH.FIRST_NAME);
        builder.addColumn(MATCH.LAST_NAME);
        builder.addColumn(MATCH.SECOND_LAST_NAME);
        builder.addColumn(MATCH.SECOND_FIRST_NAME);
        builder.addColumn(MATCH.MAIDEN_NAME);
        builder.addColumn(MATCH.DATE_OF_BIRTH_YEAR);
        builder.addColumn(MATCH.DATE_OF_BIRTH_MONTH);
        builder.addColumn(MATCH.DATE_OF_BIRTH_DAY);
        builder.addColumn(MATCH.ID);
        builder.sortDesc(MATCH.ID);
        return builder.build(dataProvider);
    }

    private Component clientComponent() {
        PropertyLayout layout = new PropertyLayout("Client");
        layout.add("First name", client.getFirstName());
        layout.add("Last name", client.getLastName());
        layout.add("Second last name", client.getSecondLastName());
        layout.add("Second first name", client.getSecondFirstName());
        layout.add("Maiden name", client.getMaidenName());
        layout.add("Document number", client.getDocumentNumber());
        layout.add("Date of birth", client.getDateOfBirth());
        layout.add("Gender", client.getGender());
        layout.setMargin(false);
        return layout;
    }
}
