package fintech.bo.components.activity;


import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import fintech.bo.api.model.activity.AddActivityRequest;

public interface BulkActionComponent extends Component, ComponentContainer {

    void build(AddActivityComponent parent, ActivitySettingsJson.BulkAction bulkAction);

    AddActivityRequest.BulkAction saveData();
}
