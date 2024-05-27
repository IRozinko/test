package fintech.bo.components.menu;

import com.vaadin.ui.MenuBar;
import lombok.Value;

@Value
public class MenuItem {

    private final String caption;
    private final MenuBar.Command command;
}
