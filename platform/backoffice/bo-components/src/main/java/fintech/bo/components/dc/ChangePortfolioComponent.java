package fintech.bo.components.dc;

import com.vaadin.ui.ComboBox;
import fintech.Validate;
import fintech.bo.api.model.dc.LogDebtActionRequest;

import java.util.List;

public class ChangePortfolioComponent extends AbstractBulkActionComponent {

    private ComboBox<String> portfolioField;

    @Override
    public void doBuild(NewActionComponent actionPanel, DcSettingsJson.BulkAction bulkAction) {
        setMargin(true);
        List<String> portfolios = bulkAction.getRequiredParam("portfolios", List.class);
        portfolioField = new ComboBox<>("Select portfolio");
        portfolioField.setTextInputAllowed(false);
        portfolioField.setEmptySelectionAllowed(false);
        portfolioField.setItems(portfolios);
        portfolioField.setWidth(NewActionComponent.FIELD_WIDTH, Unit.PIXELS);
        if (portfolios.size() == 1) {
            portfolioField.setValue(portfolios.get(0));
        }
        addComponent(portfolioField);
    }

    @Override
    public LogDebtActionRequest.BulkAction doSaveData() {
        Validate.notBlank(portfolioField.getValue(), "Portfolio not selected");
        LogDebtActionRequest.BulkAction data = new LogDebtActionRequest.BulkAction();
        data.getParams().put("portfolio", portfolioField.getValue());
        return data;
    }
}
