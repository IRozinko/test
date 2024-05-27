package fintech.bo.components.agents;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.vaadin.data.Binder;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.AgentsApiClient;
import fintech.bo.api.model.agents.DisableAgentRequest;
import fintech.bo.api.model.agents.UpdateAgentRequest;
import fintech.bo.api.model.task.TaskTypesResponse;
import fintech.bo.components.Refreshable;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ConfirmDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.task.TaskQueries;
import fintech.bo.db.jooq.task.tables.records.AgentRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static fintech.bo.db.jooq.task.tables.Agent.AGENT;
import static java.util.Arrays.asList;


@Component
public class AgentsComponents {

    public static final String ALL_TASKS = "*";
    @Autowired
    private AgentsApiClient agentsApiClient;

    @Autowired
    private TaskQueries taskQueries;

    @Autowired
    private DSLContext db;

    public AgentDataProvider dataProvider() {
        return new AgentDataProvider(db);
    }

    public VerticalLayout editAgentForm(AgentRecord agent, Refreshable refreshable) {
        List<String> taskTypes = getAllTypes();
        agent.setTaskTypes(agent.getTaskTypes());

        Map<String, Boolean> agentTypesMap = Maps.newHashMap();
        taskTypes.forEach(p -> agentTypesMap.put(p, agent.getTaskTypes().contains(p)));

        Binder<Map<String, Boolean>> binder = new Binder<>();
        binder.setBean(agentTypesMap);

        VerticalLayout layout = new VerticalLayout();

        agent.setEmail(agent.getEmail());
        Label emailLabel = new Label(agent.getEmail());
        emailLabel.addStyleName(ValoTheme.LABEL_H3);
        emailLabel.addStyleName(ValoTheme.LABEL_BOLD);
        layout.addComponent(emailLabel);

        FormLayout taskTypesLayout = new FormLayout();
        CheckBox allTasksCheckBox = new CheckBox("All tasks");
        allTasksCheckBox.setValue(ALL_TASKS.equals(agent.getTaskTypes()));
        allTasksCheckBox.addValueChangeListener(e -> {
            if (e.getValue()) {
                taskTypes.forEach(p -> agentTypesMap.put(p, false));
                binder.setBean(agentTypesMap);
            }
        });
        taskTypesLayout.addComponent(allTasksCheckBox);

        taskTypes.forEach((type) -> {
            CheckBox checkBox = new CheckBox(type);
            checkBox.addValueChangeListener(e -> {
                if (e.getValue()) {
                    allTasksCheckBox.setValue(false);
                }
            });
            taskTypesLayout.addComponent(checkBox);
            binder.bind(checkBox,
                agentTypeMap -> agentTypeMap.get(type),
                (agentTypeMap, value) -> agentTypeMap.put(type, value));
        });
        Panel panel = new Panel("Select agent task types");
        panel.setContent(taskTypesLayout);
        layout.addComponent(panel);

        Button save = new Button("Save");
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener((e) -> saveAgent(refreshable, agent, allTasksCheckBox.getValue(), agentTypesMap));

        Button disable = new Button("Disable");
        disable.addStyleName(ValoTheme.BUTTON_DANGER);
        disable.addClickListener(e -> disableAgent(refreshable, agent));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(save);
        horizontalLayout.addComponent(disable);
        layout.addComponent(horizontalLayout);
        return layout;
    }


    private void disableAgent(Refreshable refreshable, AgentRecord agent) {
        ConfirmDialog dialog = new ConfirmDialog("Disable agent?", (e) -> {
            DisableAgentRequest disableAgentRequest = new DisableAgentRequest();
            disableAgentRequest.setEmail(agent.getEmail());
            BackgroundOperations.callApi("Disabling agent", agentsApiClient.disable(disableAgentRequest),
                t -> {
                    Notifications.trayNotification("Agent disabled");
                    if (refreshable != null) {
                        refreshable.refresh();
                    }
                },
                Notifications::errorNotification);
        });
        UI.getCurrent().addWindow(dialog);
    }

    private void saveAgent(Refreshable refreshable, AgentRecord agentRecord, boolean allTasks, Map<String, Boolean> taskTypes) {
        UpdateAgentRequest saveTaskRequest = new UpdateAgentRequest();
        saveTaskRequest.setEmail(agentRecord.getEmail());
        if (allTasks) {
            saveTaskRequest.setTaskTypes(ImmutableList.of(ALL_TASKS));
        } else {
            taskTypes.forEach((key, value) -> {
                if (value) {
                    saveTaskRequest.getTaskTypes().add(key);
                }
            });
        }
        BackgroundOperations.callApi("Saving agent", agentsApiClient.saveOrUpdate(saveTaskRequest),
            t -> {
                Notifications.trayNotification("Agent saved");
                if (refreshable != null) {
                    refreshable.refresh();
                }
            },
            Notifications::errorNotification);
    }

    public NewAgentDialog newAgentDialog() {
        List<String> restrictedEmails = db.selectFrom(AGENT)
            .where(AGENT.DISABLED.eq(false))
            .fetch(AGENT.EMAIL);
        List<String> allTypes = getAllTypes();
        return new NewAgentDialog(agentsApiClient, restrictedEmails, allTypes);
    }

    private List<String> getAllTypes() {
        TaskTypesResponse taskTypesResponse = new TaskTypesResponse();
        taskTypesResponse.setTaskTypes(taskQueries.getTaskTypes());
        return taskQueries.getTaskTypes();
    }
}
