package fintech.bo.spain.alfa.activity;

import com.google.common.base.MoreObjects;
import com.vaadin.ui.*;
import fintech.Validate;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.model.activity.AddActivityRequest;
import fintech.bo.api.model.cms.GetNotificationRequest;
import fintech.bo.api.model.cms.GetNotificationResponse;
import fintech.bo.components.activity.ActivitySettingsJson;
import fintech.bo.components.activity.AddActivityComponent;
import fintech.bo.components.activity.BulkActionComponent;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import retrofit2.Call;

import java.util.List;

public class SendEmailComponent extends VerticalLayout implements BulkActionComponent {


    private final String type;
    private final CmsApiClient cmsApiClient;
    private TextField emailSubjectField;
    private RichTextArea emailBodyField;
    private ComboBox<String> template;
    private Long clientId;

    public SendEmailComponent(String type, CmsApiClient cmsApiClient) {
        this.type = type;
        this.cmsApiClient = cmsApiClient;
        setMargin(true);
    }

    @Override
    public void build(AddActivityComponent parent, ActivitySettingsJson.BulkAction bulkAction) {
        this.clientId = parent.getClientId();
        List<String> cmsKeys = bulkAction.getRequiredParam("cmsKeys", List.class);

        template = new ComboBox<>("Select template");
        template.setTextInputAllowed(false);
        template.setItems(cmsKeys);
        template.setWidth(AddActivityComponent.FIELD_WIDTH, Unit.PIXELS);
        template.addValueChangeListener(e -> renderEmail(e.getValue()));

        emailSubjectField = new TextField("Email subject");
        emailSubjectField.setWidth(100, Unit.PERCENTAGE);
        emailSubjectField.setVisible(false);

        emailBodyField = new RichTextArea();
        emailBodyField.setWidth(100, Unit.PERCENTAGE);
        emailBodyField.setHeight(300, Unit.PIXELS);
        emailBodyField.setVisible(false);

        FormLayout form = new FormLayout();
        form.addComponents(template, emailSubjectField);

        addComponents(form, emailBodyField);
    }

    private void renderEmail(String cmsKey) {
        if (cmsKey == null) {
            emailSubjectField.setVisible(false);
            emailSubjectField.setValue("");

            emailBodyField.setVisible(false);
            emailBodyField.setValue("");
        } else {
            emailSubjectField.setVisible(true);
            emailSubjectField.setValue("");

            emailBodyField.setVisible(true);
            emailBodyField.setValue("");

            GetNotificationRequest request = new GetNotificationRequest();
            request.setClientId(clientId);
            request.setKey(cmsKey);
            Call<GetNotificationResponse> call = cmsApiClient.getNotification(request);
            BackgroundOperations.callApi("Rendering notification", call, t -> {
                emailSubjectField.setValue(MoreObjects.firstNonNull(t.getEmailSubject(), ""));
                emailBodyField.setValue(MoreObjects.firstNonNull(t.getEmailBody(), ""));
            }, Notifications::errorNotification);
        }
    }


    @Override
    public AddActivityRequest.BulkAction saveData() {
        if (template.getValue() == null) {
            return null;
        }
        Validate.notBlank(emailSubjectField.getValue(), "Empty email subject");
        Validate.notBlank(emailBodyField.getValue(), "Empty email body");
        AddActivityRequest.BulkAction data = new AddActivityRequest.BulkAction();
        data.setType(this.type);
        data.getParams().put("subject", emailSubjectField.getValue());
        data.getParams().put("body", emailBodyField.getValue());
        data.getParams().put("cmsKey", template.getValue());
        return data;
    }
}
