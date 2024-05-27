package fintech.bo.components.period;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.ACCOUNTING_VIEW, BackofficePermissions.PERIOD_CLOSE})
@SpringView(name = PeriodsView.NAME)
public class PeriodsView extends VerticalLayout implements View {

    public static final String NAME = "periods";

    @Autowired
    private PeriodsComponents periodsComponents;

    private Grid<Record> grid;

    private PeriodsDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Periods");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        layout.setRefreshAction(e -> refresh());
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = periodsComponents.dataProvider();
        grid = periodsComponents.grid(dataProvider);
        layout.setContent(grid);
    }

    private void refresh() {
        grid.getDataProvider().refreshAll();
    }

}
