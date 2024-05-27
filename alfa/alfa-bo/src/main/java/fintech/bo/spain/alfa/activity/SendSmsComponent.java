package fintech.bo.spain.alfa.activity;

import com.google.common.base.MoreObjects;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.ValoTheme;
import fintech.Validate;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.model.activity.AddActivityRequest;
import fintech.bo.api.model.cms.GetNotificationRequest;
import fintech.bo.api.model.cms.GetNotificationResponse;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.activity.ActivitySettingsJson;
import fintech.bo.components.activity.AddActivityComponent;
import fintech.bo.components.activity.BulkActionComponent;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;

import java.util.List;

public class SendSmsComponent extends FormLayout implements BulkActionComponent {

    private final String type;
    private final CmsApiClient cmsApiClient;
    private TextArea smsField;
    private ComboBox<String> template;
    private Label smsLength;
    private Long clientId;

    public SendSmsComponent(String type, CmsApiClient cmsApiClient) {
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
        template.addValueChangeListener(e -> renderSms(e.getValue()));


        smsField = new TextArea("SMS");
        smsField.addStyleName(BackofficeTheme.TEXT_MONO);
        smsField.setWidth(AddActivityComponent.FIELD_WIDTH, Unit.PIXELS);
        smsField.setRows(5);
        smsField.setVisible(false);
        smsField.setMaxLength(160);

        smsLength = new Label();
        smsLength.addStyleName(ValoTheme.LABEL_SMALL);
        smsLength.setVisible(false);
        smsField.addValueChangeListener(e -> {
            int length = StringUtils.length(e.getValue());
            smsLength.setValue(String.format("%s characters", length));
        });

        addComponents(template, smsField, smsLength);
    }

    private void renderSms(String cmsKey) {
        if (cmsKey == null) {
            smsField.setVisible(false);
            smsField.setValue("");
            smsLength.setVisible(false);
        } else {
            smsField.setValue("");
            smsField.setVisible(true);
            smsLength.setVisible(true);

            GetNotificationRequest request = new GetNotificationRequest();
            request.setClientId(clientId);
            request.setKey(cmsKey);
            Call<GetNotificationResponse> call = cmsApiClient.getNotification(request);
            BackgroundOperations.callApi("Rendering notification", call, t -> {
                smsField.setValue(MoreObjects.firstNonNull(t.getSmsText(), ""));
            }, Notifications::errorNotification);
        }
    }


    @Override
    public AddActivityRequest.BulkAction saveData() {
        if (template.getValue() == null) {
            return null;
        }
        Validate.notBlank(smsField.getValue(), "Empty SMS text");
        AddActivityRequest.BulkAction data = new AddActivityRequest.BulkAction();
        data.setType(this.type);
        data.getParams().put("text", smsField.getValue());
        data.getParams().put("cmsKey", template.getValue());
        return data;
    }
}
