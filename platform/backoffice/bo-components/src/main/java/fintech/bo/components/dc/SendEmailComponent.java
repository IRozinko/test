package fintech.bo.components.dc;

import com.google.common.base.MoreObjects;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.Validate;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.model.cms.GetNotificationRequest;
import fintech.bo.api.model.cms.GetNotificationResponse;
import fintech.bo.api.model.dc.LogDebtActionRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import retrofit2.Call;

import java.util.List;

public class SendEmailComponent extends VerticalLayout implements BulkActionComponent {

    private final CmsApiClient cmsApiClient;
    private TextField emailSubjectField;
    private RichTextArea emailBodyField;
    private ComboBox<String> template;
    private Long debtId;
    private boolean renderTemplate;

    public SendEmailComponent(CmsApiClient cmsApiClient) {
        this.cmsApiClient = cmsApiClient;
        setMargin(true);
    }

    public SendEmailComponent(CmsApiClient cmsApiClient, boolean renderTemplate) {
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
        template.setWidth(NewActionComponent.FIELD_WIDTH, Sizeable.Unit.PIXELS);
        template.addValueChangeListener(e -> renderEmail(e.getValue()));

        emailSubjectField = new TextField("Email subject");
        emailSubjectField.setWidth(100, Sizeable.Unit.PERCENTAGE);
        emailSubjectField.setVisible(false);

        emailBodyField = new RichTextArea();
        emailBodyField.setWidth(100, Sizeable.Unit.PERCENTAGE);
        emailBodyField.setHeight(300, Sizeable.Unit.PIXELS);
        emailBodyField.setVisible(false);

        FormLayout form = new FormLayout();
        form.addComponents(template, emailSubjectField);

        addComponents(form, emailBodyField);
    }

    public void build(List<String> templates) {
        template = new ComboBox<>("Select template");
        template.setTextInputAllowed(false);
        template.setItems(templates);
        template.setWidth(NewActionComponent.FIELD_WIDTH, Sizeable.Unit.PIXELS);
        template.addValueChangeListener(e -> renderEmail(e.getValue()));

        emailSubjectField = new TextField("Email subject");
        emailSubjectField.setWidth(100, Sizeable.Unit.PERCENTAGE);
        emailSubjectField.setVisible(false);

        emailBodyField = new RichTextArea();
        emailBodyField.setWidth(100, Sizeable.Unit.PERCENTAGE);
        emailBodyField.setHeight(300, Sizeable.Unit.PIXELS);
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
            request.setDebtId(debtId);
            request.setKey(cmsKey);
            request.setRender(renderTemplate);
            Call<GetNotificationResponse> call = cmsApiClient.getNotification(request);
            BackgroundOperations.callApi("Rendering notification", call, t -> {
                emailSubjectField.setValue(MoreObjects.firstNonNull(t.getEmailSubject(), ""));
                emailBodyField.setValue(MoreObjects.firstNonNull(t.getEmailBody(), ""));
            }, Notifications::errorNotification);
        }
    }

    @Override
    public LogDebtActionRequest.BulkAction saveData() {
        if (template.getValue() == null) {
            return null;
        }
        Validate.notBlank(emailSubjectField.getValue(), "Empty email subject");
        Validate.notBlank(emailBodyField.getValue(), "Empty email body");
        LogDebtActionRequest.BulkAction data = new LogDebtActionRequest.BulkAction();
        data.getParams().put("subject", emailSubjectField.getValue());
        data.getParams().put("body", emailBodyField.getValue());
        data.getParams().put("cmsKey", template.getValue());
        return data;
    }
}
