package fintech.bo.components.common;

import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.text.WordUtils;

public class TitleLabel extends Label {

    public TitleLabel(String title) {
        super(WordUtils.capitalizeFully(title));
        addStyleName(ValoTheme.LABEL_H4);
        addStyleName(ValoTheme.LABEL_BOLD);
        addStyleName(ValoTheme.LABEL_COLORED);
    }
}
