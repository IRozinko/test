package fintech.bo.components.common;

import com.vaadin.ui.CustomLayout;

public class HtmlPreview extends CustomLayout {
    private final String html;

    public HtmlPreview(String html) {
        this.html = html;
    }

    @Override
    public void attach() {
        super.attach();
        setTemplateContents(html);
    }
}
