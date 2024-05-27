package fintech.bo.spain.alfa.workflow;

import com.vaadin.shared.ui.grid.renderers.TextRendererState;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.TextRenderer;
import elemental.json.Json;
import elemental.json.JsonValue;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.model.StringRequest;
import fintech.bo.api.model.cms.RenderNotificationResponse;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.cms.CmsConstants;
import fintech.bo.components.cms.CmsQueries;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.workflow.ActivityListenersViewAbs;
import fintech.bo.db.jooq.workflow.tables.ActivityListener;
import fintech.bo.db.jooq.workflow.tables.records.ActivityListenerRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.util.List;
import java.util.stream.Collectors;

import static fintech.bo.components.cms.CmsComponents.emailPreviewPanel;
import static fintech.bo.components.cms.CmsComponents.smsPreviewPanel;

@SpringView(name = NotificationsActivityListenersView.NAME)
public class NotificationsActivityListenersView extends ActivityListenersViewAbs {

    public static final String NAME = "activity-custom-notifications";

    @Autowired
    private CmsApiClient cmsApiClient;

    @Autowired
    private CmsQueries cmsQueries;

    public NotificationsActivityListenersView() {
        super("Custom notifications");
    }

    @Override
    protected void customGridActions(JooqGridBuilder<ActivityListenerRecord> builder) {
        builder
            .addColumn(ActivityListener.ACTIVITY_LISTENER.PARAMS)
            .setRenderer(new ArrayFirstElementRenderer())
            .setWidth(200)
            .setCaption("Template");

        builder.addActionColumn("Preview", this::previewNotification);
    }

    private void previewNotification(ActivityListenerRecord record) {
        Call<RenderNotificationResponse> call = cmsApiClient.renderNotification(new StringRequest(record.getParams()[0]));
        BackgroundOperations.callApi("Rendering notification", call, renderResponse -> {
            VerticalLayout layout = new VerticalLayout();
            if (!StringUtils.isBlank(renderResponse.getSmsText())) {
                layout.addComponent(smsPreviewPanel(renderResponse.getSmsText()));
            }
            if (!StringUtils.isBlank(renderResponse.getEmailSubject()) || !StringUtils.isBlank(renderResponse.getEmailBody())) {
                layout.addComponent(emailPreviewPanel(renderResponse.getEmailSubject(), renderResponse.getEmailBody()));
            }
            Window previewWindow = new Window(record.getParams()[0]);
            previewWindow.setContent(layout);
            previewWindow.center();
            previewWindow.setModal(true);
            getUI().addWindow(previewWindow);
        }, Notifications::errorNotification);
    }

    @Override
    protected String getTriggerName() {
        return "SendNotificationDynamic";
    }

    @Override
    protected List<String[]> getParams() {
        return cmsQueries.findByType(CmsConstants.TYPE_NOTIFICATION).stream()
            .map(i -> new String[]{i.getItemKey()})
            .collect(Collectors.toList());
    }

    private static class ArrayFirstElementRenderer extends TextRenderer {

        @Override
        public JsonValue encode(Object value) {
            if (value == null) {
                return super.encode(null);
            } else if (value instanceof String[]) {
                return Json.create(((String[]) value)[0]);
            }
            return Json.create(value.toString());
        }

        @Override
        public String getNullRepresentation() {
            return super.getNullRepresentation();
        }

        @Override
        protected TextRendererState getState() {
            return super.getState();
        }

        @Override
        protected TextRendererState getState(boolean markAsDirty) {
            return super.getState(markAsDirty);
        }
    }
}
