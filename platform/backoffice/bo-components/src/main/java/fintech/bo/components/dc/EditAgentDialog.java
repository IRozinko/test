package fintech.bo.components.dc;

import com.vaadin.data.Binder;
import com.vaadin.ui.*;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.dc.SaveAgentRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;

import java.util.List;
import java.util.stream.Collectors;

public class EditAgentDialog extends ActionDialog {

    private final DcApiClient apiClient;
    private final DcQueries queries;
    private final SaveAgentRequest request;
    private VerticalLayout layout;

    public EditAgentDialog(DcApiClient apiClient, DcQueries queries, SaveAgentRequest request) {
        super(StringUtils.isBlank(request.getAgent()) ? "Add agent" : "Edit agent", "Save");
        this.apiClient = apiClient;
        this.queries = queries;
        this.request = request;
        setWidth(600, Unit.PIXELS);
        build();
        setDialogContent(layout);
    }

    private void build() {
        FormLayout form = new FormLayout();
        Binder<SaveAgentRequest> binder = new Binder<>();
        binder.setBean(request);

        TextField name = new TextField("Name");
        name.setReadOnly(!StringUtils.isBlank(request.getAgent()));
        binder.forField(name).bind(SaveAgentRequest::getAgent, SaveAgentRequest::setAgent);
        name.focus();
        name.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(name);

        CheckBox disabled = new CheckBox("Disabled");
        binder.forField(disabled).bind(SaveAgentRequest::isDisabled, SaveAgentRequest::setDisabled);
        form.addComponent(disabled);

        List<String> availablePortfolios = queries.getSettings().getPortfolios().stream().map(DcSettingsJson.Portfolio::getName).collect(Collectors.toList());
        TwinColSelect<String> portfolios = new TwinColSelect<>("Portfolios");
        portfolios.setItems(availablePortfolios);
        portfolios.setRows(availablePortfolios.size());
        portfolios.setLeftColumnCaption("Available");
        portfolios.setRightColumnCaption("Selected");
        binder.forField(portfolios).bind(SaveAgentRequest::getPortfolios, SaveAgentRequest::setPortfolios);
        portfolios.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(portfolios);

        layout = new VerticalLayout();
        layout.addComponent(form);
    }

    @Override
    protected void executeAction() {
        Call<IdResponse> call = apiClient.saveAgent(request);
        BackgroundOperations.callApi("Saving agent", call, t -> {
            Notifications.trayNotification("Agent saved");
            close();
        }, Notifications::errorNotification);
    }
}
