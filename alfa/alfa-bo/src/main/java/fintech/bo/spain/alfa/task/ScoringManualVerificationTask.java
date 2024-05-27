package fintech.bo.spain.alfa.task;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.task.CommonTaskView;
import fintech.bo.db.jooq.task.tables.records.TaskAttributeRecord;
import fintech.bo.spain.alfa.scoring.ScoringDataProvider;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.util.Optional;

import static fintech.bo.db.jooq.decision_engine.DecisionEngine.DECISION_ENGINE;

public class ScoringManualVerificationTask  extends CommonTaskView {

    private final DSLContext db;
    private final ClientRepository clientRepository;
    private ClientDTO client;

    public ScoringManualVerificationTask() {
        this.clientRepository = ApiAccessor.gI().get(ClientRepository.class);
        this.db = ApiAccessor.gI().get(DSLContext.class);
    }

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        this.client = clientRepository.getRequired(getTask().getClientId());

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(clientComponent());
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));
        Optional<TaskAttributeRecord> scoringDecisionEngineRequestId = getHelper().getTaskQueries().findAttributeByKey(getTask().getId(), "scoring_decision_engine_request_id");
        Validate.isTrue(scoringDecisionEngineRequestId.isPresent(), "No scoring DE attribute available in workflow");
        Long scoringDERequestId = Long.valueOf(scoringDecisionEngineRequestId.get().getValue());
        baseLayout.addTab("Scoring response", () -> scoringTab(getTask().getAgent(), scoringDERequestId));

        return layout;
    }

    private Component scoringTab(String taskAgent, Long scoringDERequestlId) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(decisionEngineResponseTab(scoringDERequestlId));
        boolean assignedToMe = StringUtils.equalsIgnoreCase(taskAgent, LoginService.getLoginData().getUser());
        verticalLayout.setEnabled(assignedToMe);
        return verticalLayout;
    }

    private Grid<Record> decisionEngineResponseTab(Long scoringModelId) {
        ScoringDataProvider dataProvider = new ScoringDataProvider(db);
        dataProvider.setScoringDERequestId(scoringModelId);
        return attachmentGrid(dataProvider);
    }

    public Grid<Record> attachmentGrid(ScoringDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Response", this::showResponseDialog);
        builder.addLinkColumn(DECISION_ENGINE.REQUEST.CLIENT_ID, r -> ClientComponents.clientLink(r.get(DECISION_ENGINE.REQUEST.CLIENT_ID)));
        builder.addColumn(DECISION_ENGINE.REQUEST.CREATED_AT);
        builder.addColumn(DECISION_ENGINE.REQUEST.DECISION);
        builder.addColumn(DECISION_ENGINE.REQUEST.RATING);
        builder.addColumn(DECISION_ENGINE.REQUEST.SCORE);
        return builder.build(dataProvider);
    }

    private Component clientComponent() {
        PropertyLayout layout = new PropertyLayout("Client");
        layout.add("First name", client.getFirstName());
        layout.add("Last name", client.getLastName());
        layout.add("Second last name", client.getSecondLastName());
        layout.add("Document number", client.getDocumentNumber());
        layout.add("Account number", client.getAccountNumber());
        layout.setMargin(false);
        return layout;
    }

    private void showResponseDialog(Record item) {
        String json = item.get(DECISION_ENGINE.REQUEST.RESPONSE);
        if ("OK".equals(item.get(DECISION_ENGINE.REQUEST.STATUS)) && JsonUtils.isJsonValid(json)) {
            json = JsonUtils.formatJson(json);
        }
        Dialogs.showText("Response", json);
    }


}
