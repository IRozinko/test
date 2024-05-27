package fintech.bo.components.dc;

import com.vaadin.ui.FormLayout;
import fintech.bo.api.model.dc.LogDebtActionRequest;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBulkActionComponent extends FormLayout implements BulkActionComponent {

    private Map<String, Object> params = new HashMap<>();

    @Override
    public void build(NewActionComponent actionPanel, DcSettingsJson.BulkAction bulkAction) {
        params.putAll(bulkAction.getParams());
        doBuild(actionPanel, bulkAction);
    }

    @Override
    public LogDebtActionRequest.BulkAction saveData() {
        LogDebtActionRequest.BulkAction bulkAction = doSaveData();
        bulkAction.getParams().putAll(params);
        return bulkAction;
    }

    protected abstract LogDebtActionRequest.BulkAction doSaveData();

    protected abstract void doBuild(NewActionComponent actionPanel, DcSettingsJson.BulkAction bulkAction);

    protected Map<String, Object> getParams() {
        return params;
    }
}
