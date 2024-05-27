package fintech.bo.components.views;

import com.vaadin.ui.Component;

public interface BoComponent extends Component {

    void setUp(BoComponentContext context);

    void refresh();
}
