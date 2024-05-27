package fintech.bo.components.agents;


import com.vaadin.data.Binder;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.api.client.AgentsApiClient;
import fintech.bo.api.model.agents.UpdateAgentRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;

import java.util.List;

import static com.vaadin.data.ValidationResult.error;
import static com.vaadin.data.ValidationResult.ok;

public class NewAgentDialog extends ActionDialog {

    private Binder<UpdateAgentRequest> binder;
    private AgentsApiClient agentsApiClient;
    private List<String> restrictedEmails;
    private List<String> types;
    private UpdateAgentRequest request;

    NewAgentDialog(AgentsApiClient agentsApiClient, List<String> restrictedEmails, List<String> types) {
        super("New Agent", "Save");
        this.agentsApiClient = agentsApiClient;
        this.restrictedEmails = restrictedEmails;
        this.types = types;
        setModal(true);
        setWidth(400, Unit.PIXELS);
        setDialogContent(buildForm());
    }

    private Component buildForm() {
        request = new UpdateAgentRequest();
        binder = new Binder<>();
        binder.setBean(request);

        FormLayout form = new FormLayout();
        form.setWidthUndefined();
        form.addComponent(getInput());
//        form.addComponent(getTypes());
        form.setSizeFull();
        return form;
    }

    private Component getInput() {
        TextField input = new TextField("Email");
        input.setWidth(100, Unit.PERCENTAGE);
        input.focus();

        binder.forField(input)
            .asRequired("Email should be specified")
            .withValidator((value, context) -> restrictedEmails.contains(value) ? error("Agent exists with such email") : ok())
            .bind(UpdateAgentRequest::getEmail, UpdateAgentRequest::setEmail);

        return input;
    }

//    private Component getTypes() {
//        VerticalLayout form = new VerticalLayout();

//        CheckBox checkBoxStar = new CheckBox();
//        form.addComponent(checkBoxStar);
//        checkBoxStar.setCaption("All task types");

//        types.forEach((type) -> {
//            CheckBox checkBox = new CheckBox(type);
//            form.addComponent(checkBox);
//            binder.bind(checkBox,
//                request -> request.getTaskTypes().contains(type),
//                (request, value) -> {
//                    if (value) {
//                        request.getTaskTypes().add(type);
//                    } else {
//                        request.getTaskTypes().remove(type);
//                    }
//                });
//        });
//        return form;
//    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            BackgroundOperations.callApi("Saving agent", agentsApiClient.saveOrUpdate(request), t -> {
                Notifications.trayNotification("Agent save");
                close();
            }, Notifications::errorNotification);
        }
    }
}
