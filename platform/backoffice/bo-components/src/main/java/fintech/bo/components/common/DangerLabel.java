package fintech.bo.components.common;

import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.BackofficeTheme;

public class DangerLabel extends Label {

    public DangerLabel(String title) {
        super(title);
        addStyleName(BackofficeTheme.TEXT_DANGER);
        addStyleName(ValoTheme.LABEL_BOLD);
        addStyleName(ValoTheme.LABEL_SMALL);
    }
}
