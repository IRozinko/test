package fintech.bo.spain.alfa.dc;

import com.vaadin.ui.CssLayout;
import fintech.bo.api.model.dc.LogDebtActionRequest;
import fintech.bo.components.dc.BulkActionComponent;
import fintech.bo.components.dc.DcSettingsJson;
import fintech.bo.components.dc.NewActionComponent;

public class EmptyBulkActionComponent extends CssLayout implements BulkActionComponent {

    private DcSettingsJson.BulkAction bulkActionSettings;

    @Override
    public void build(NewActionComponent actionPanel, DcSettingsJson.BulkAction bulkAction) {
        this.bulkActionSettings = bulkAction;
    }

    @Override
    public LogDebtActionRequest.BulkAction saveData() {
        LogDebtActionRequest.BulkAction bulkAction = new LogDebtActionRequest.BulkAction();
        bulkAction.setParams(bulkActionSettings.getParams());
        return bulkAction;
    }
}
