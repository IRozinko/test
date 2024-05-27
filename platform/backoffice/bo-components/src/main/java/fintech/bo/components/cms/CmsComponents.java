package fintech.bo.components.cms;

import com.google.common.base.MoreObjects;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.common.HtmlPreview;

public class CmsComponents {

    public static Panel emailPreviewPanel(String emailSubject, String emailBody) {
        TextField subject = new TextField("Email subject");
        subject.setValue(MoreObjects.firstNonNull(emailSubject, "null"));
        subject.setWidth(400, Sizeable.Unit.PIXELS);
        subject.setReadOnly(true);

        VerticalLayout content = new VerticalLayout();
        content.addComponent(subject);
        content.addComponent(new Panel("Email body", new VerticalLayout(new HtmlPreview(MoreObjects.firstNonNull(emailBody, "null")))));
        content.setSizeUndefined();

        Panel panel = new Panel();
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        panel.setWidth(100, Sizeable.Unit.PERCENTAGE);
        panel.setHeightUndefined();
        panel.setContent(content);
        return panel;
    }

    public static Panel smsPreviewPanel(String text) {
        TextArea sms = new TextArea("SMS text");
        sms.setValue(text);
        sms.setRows(8);
        sms.setReadOnly(true);
        sms.setWidth(400, Sizeable.Unit.PIXELS);
        sms.addStyleName(BackofficeTheme.TEXT_MONO);

        Panel panel = new Panel();
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(sms);
        Label charCount = new Label(String.format("%d characters", text.length()));
        charCount.addStyleName(ValoTheme.LABEL_SMALL);
        layout.addComponent(charCount);
        panel.setContent(layout);
        return panel;
    }
}
