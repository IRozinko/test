package fintech.bo.components.dc;


import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import fintech.bo.api.model.dc.LogDebtActionRequest;

import java.util.Optional;

public interface BulkActionComponent extends Component, ComponentContainer {

    void build(NewActionComponent actionPanel, DcSettingsJson.BulkAction bulkAction);

    LogDebtActionRequest.BulkAction saveData();

    /** return error string if bulk action is not in valid state */
    default Optional<String> validate() {
        return Optional.empty();
    }
}
