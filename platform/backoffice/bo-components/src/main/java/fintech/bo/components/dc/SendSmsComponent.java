package fintech.bo.components.dc;

import com.google.common.base.MoreObjects;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.ValoTheme;
import fintech.Validate;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.model.cms.GetNotificationRequest;
import fintech.bo.api.model.cms.GetNotificationResponse;
import fintech.bo.api.model.dc.LogDebtActionRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;

import java.util.List;

public class SendSmsComponent extends FormLayout implements BulkActionComponent {

    private final CmsApiClient cmsApiClient;
    private TextArea smsField;
    private ComboBox<String> template;
    private Label smsLength;
    private Long debtId;
    private boolean renderTemplate;

    public SendSmsComponent(CmsApiClient cmsApiClient) {
        this.cmsApiClient = cmsApiClient;
        setMargin(true);
        this.renderTemplate = true;
    }

    public SendSmsComponent(CmsApiClient cmsApiClient, boolean renderTemplate) {
        this.cmsApiClient = cmsApiClient;
        setMargin(true);
        this.renderTemplate = renderTemplate;
    }

    @Override
    public void build(NewActionComponent actionPanel, DcSettingsJson.BulkAction bulkAction) {
        this.debtId = actionPanel.getDebt().getId();
        List<String> cmsKeys = bulkAction.getRequiredParam("cmsKeys", List.class);

        template = new ComboBox<>("Select template");
        template.setTextInputAllowed(false);
        template.setItems(cmsKeys);
        template.setWidth(NewActionComponent.FIELD_WIDTH, Unit.PIXELS);
        template.addValueChangeListener(e -> renderSms(e.getValue()));


        smsField = new TextArea("SMS");
        smsField.addStyleName(BackofficeTheme.TEXT_MONO);
        smsField.setWidth(NewActionComponent.FIELD_WIDTH, Unit.PIXELS);
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

    public void build(List<String> cmsKeys) {
        template = new ComboBox<>("Select template");
        template.setTextInputAllowed(false);
        template.setItems(cmsKeys);
        template.setWidth(NewActionComponent.FIELD_WIDTH, Unit.PIXELS);
        template.addValueChangeListener(e -> renderSms(e.getValue()));

        smsField = new TextArea("SMS");
        smsField.addStyleName(BackofficeTheme.TEXT_MONO);
        smsField.setWidth(NewActionComponent.FIELD_WIDTH, Unit.PIXELS);
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
            request.setDebtId(debtId);
            request.setKey(cmsKey);
            request.setRender(renderTemplate);

            Call<GetNotificationResponse> call = cmsApiClient.getNotification(request);
            BackgroundOperations.callApi("Rendering notification", call, t -> smsField.setValue(MoreObjects.firstNonNull(t.getSmsText(), "")),
                Notifications::errorNotification);
        }
    }

    @Override
    public LogDebtActionRequest.BulkAction saveData() {
        if (template.getValue() == null) {
            return null;
        }
        Validate.notBlank(smsField.getValue(), "Empty SMS text");
        LogDebtActionRequest.BulkAction data = new LogDebtActionRequest.BulkAction();
        data.getParams().put("text", smsField.getValue());
        data.getParams().put("cmsKey", template.getValue());
        return data;
    }

}
