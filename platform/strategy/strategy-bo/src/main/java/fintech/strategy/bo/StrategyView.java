package fintech.strategy.bo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.db.jooq.strategy.Tables;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringView(name = StrategyView.NAME)
public class StrategyView extends VerticalLayout implements View {

    public static final String NAME = "strategy";

    private long strategyId;
    private CalculationStrategyRecord strategy;

    @Autowired
    private StrategiesComponents strategiesComponents;

    @Autowired(required = false)
    private List<StrategyPropertiesRenderer> propertiesRenderers = new ArrayList<>();

    @Autowired
    private List<StrategyViewTab> tabs = new ArrayList<>();

    @Autowired
    private DSLContext db;

    protected void refresh() {
        removeAllComponents();
        setSpacing(false);
        setMargin(false);

        this.strategy = db.selectFrom(Tables.CALCULATION_STRATEGY)
            .where(Tables.CALCULATION_STRATEGY.ID.eq(strategyId))
            .fetchOne();
        if (strategy == null) {
            Notifications.errorNotification("Strategy not found");
            return;
        }
        String name = String.format("%s%s%s", strategy.getStrategyType(), strategy.getCalculationType(), strategy.getVersion());
        setCaption(String.format("Strategy %s", name));

        BusinessObjectLayout layout = new BusinessObjectLayout();
        layout.setTitle(name);
        buildLeft(strategy, layout);
        buildTabs(strategy, layout);
        buildActions(strategy, layout);
        addComponentsAndExpand(layout);
    }

    private void buildLeft(CalculationStrategyRecord strategy, BusinessObjectLayout layout) {
        layout.addLeftComponent(strategiesComponents.strategyInfo(strategy));
    }

    private void buildTabs(CalculationStrategyRecord strategy, BusinessObjectLayout layout) {
        layout.addTab("Properties", () -> properties(strategy));

        tabs.forEach(t -> {
            layout.addTab(t.getCaption(), () -> t.component(strategy));
        });
    }

    private Component properties(CalculationStrategyRecord strategy) {
        return propertiesRenderers
            .stream()
            .filter(r -> r.canRender(strategy))
            .findFirst()
            .map(r -> r.render(strategy))
            .orElseGet(() -> new Label("Strategy properties can't be rendered"));
    }

    private void buildActions(CalculationStrategyRecord strategy, BusinessObjectLayout layout) {
        if (LoginService.hasPermission(BackofficePermissions.STRATEGIES_EDIT)) {
            layout.addActionMenuItem("Edit", e -> editStrategy());
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        strategyId = Long.parseLong(UrlUtils.getParam(event.getParameters(), UrlUtils.ID));
        refresh();
    }


    private void editStrategy() {
        EditStrategyDialog dialog = new EditStrategyDialog(strategyId);
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }
}
