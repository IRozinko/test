package fintech.bo.components.emails;


import com.vaadin.server.Sizeable;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fintech.bo.components.common.HtmlPreview;
import org.apache.commons.lang3.StringUtils;


public class EmailPreviewDialog extends Window {

    public EmailPreviewDialog(String subject, String body) {
        super("Email");
        TextField subjectField = new TextField("Subject");
        subjectField.setValue(subject);
        subjectField.setWidth(100, Unit.PERCENTAGE);
        subjectField.setReadOnly(true);

        // do not allow to click href links, in case operators can accept loan agreement, for example
        body = StringUtils.replace(body, "href", "hrf");

        //remove call to tracking stats api
        body = body.replaceAll("<img.+id=\"pixel\".*?>", "");

        HtmlPreview html = new HtmlPreview(body);
        html.setSizeUndefined();
        Panel panel = new Panel(html);
        panel.setWidth(100, Sizeable.Unit.PERCENTAGE);
        panel.setHeight(600, Sizeable.Unit.PIXELS);

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(subjectField);
        layout.addComponent(panel);

        setContent(layout);
        center();
        setWidth(600, Unit.PIXELS);
    }
}
