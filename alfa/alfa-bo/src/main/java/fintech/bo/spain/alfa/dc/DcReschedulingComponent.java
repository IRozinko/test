package fintech.bo.spain.alfa.dc;

import com.vaadin.ui.VerticalLayout;
import fintech.JsonUtils;
import fintech.bo.api.model.dc.LogDebtActionRequest;
import fintech.bo.components.dc.BulkActionComponent;
import fintech.bo.components.dc.DcSettingsJson;
import fintech.bo.components.dc.NewActionComponent;
import fintech.bo.spain.alfa.loan.DebtReschedulingComponent;

import java.util.Optional;

public class DcReschedulingComponent extends VerticalLayout implements BulkActionComponent {

    private DebtReschedulingComponent debtReschedulingComponent;

    public DcReschedulingComponent() {
        setMargin(true);
    }

    @Override
    public void build(NewActionComponent actionPanel, DcSettingsJson.BulkAction bulkAction) {
        Long loanId = actionPanel.getDebt().getLoanId();
        debtReschedulingComponent = new DebtReschedulingComponent(loanId);
        addComponent(debtReschedulingComponent);
    }

    @Override
    public Optional<String> validate() {
        return debtReschedulingComponent.validate();
    }

    @Override
    public LogDebtActionRequest.BulkAction saveData() {
        LogDebtActionRequest.BulkAction data = new LogDebtActionRequest.BulkAction();
        data.getParams().put("schedule", JsonUtils.writeValueAsString(debtReschedulingComponent.getReschedulingPreview()));
        return data;
    }

}
