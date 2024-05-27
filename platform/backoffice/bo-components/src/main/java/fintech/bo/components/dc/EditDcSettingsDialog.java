package fintech.bo.components.dc;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.model.dc.SaveDcSettingsRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import retrofit2.Call;

public class EditDcSettingsDialog extends ActionDialog {

    private final DcApiClient apiClient;
    private final DcQueries queries;
    private VerticalLayout layout;
    private TextArea jsonEditor;

    public EditDcSettingsDialog(DcApiClient apiClient, DcQueries queries) {
        super("Edit DC settings", "Save");
        this.apiClient = apiClient;
        this.queries = queries;
        setWidth(80, Unit.PERCENTAGE);
        build();
        setDialogContent(layout);
    }

    private void build() {
        jsonEditor = new TextArea();
        jsonEditor.setValue(queries.getRawSettings());
        jsonEditor.setSizeFull();
        jsonEditor.setReadOnly(false);
        jsonEditor.addStyleName(BackofficeTheme.TEXT_MONO);
        jsonEditor.setHeight(600, Unit.PIXELS);

        layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(jsonEditor);
    }

    @Override
    protected void executeAction() {
        SaveDcSettingsRequest request = new SaveDcSettingsRequest();
        request.setJson(jsonEditor.getValue());
        Call<Void> call = apiClient.saveSettings(request);
        BackgroundOperations.callApi("Saving settings", call, t -> {
            Notifications.trayNotification("Settings saved");
            queries.flushSettingsCache();
            close();
        }, Notifications::errorNotification);
    }
}
