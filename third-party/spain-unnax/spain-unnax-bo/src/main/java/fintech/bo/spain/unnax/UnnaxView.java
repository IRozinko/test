package fintech.bo.spain.unnax;

import com.vaadin.addon.daterangefield.DateRange;
import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.Refreshable;
import fintech.bo.components.common.Fields;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SpringView(name = UnnaxView.NAME)
@SecuredView(BackofficePermissions.ADMIN)
public class UnnaxView extends VerticalLayout implements View, Refreshable {

    public static final String NAME = "unnax";

    @Autowired
    private UnnaxComponents unnaxComponents;

    @Autowired
    private DSLContext db;

    private UnnaxLogDataProvider dataProvider;
    private Binder<UnnaxLogDataProvider> binder;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Unnax");

        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        buildGrid(layout);
        buildTop(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        TextField searchQuery = new TextField("Search");
        searchQuery.setWidth(300, Unit.PIXELS);
        binder.forField(searchQuery)
            .bind(UnnaxLogDataProvider::getSearchQuery, UnnaxLogDataProvider::setSearchQuery);
        layout.addTopComponent(searchQuery);

        ComboBox<String> callbackFilter = unnaxComponents.callbackFilter();
        callbackFilter.setWidth(350, Unit.PIXELS);
        binder.forField(callbackFilter)
            .bind(UnnaxLogDataProvider::getCallbackType, UnnaxLogDataProvider::setCallbackType);
        layout.addTopComponent(callbackFilter);

        DateRangeField from = Fields.dateRangeField("Date range");
        binder.forField(from)
            .bind(dp -> DateRange.between(dp.getFrom(), dp.getTo()),
                (dp, dateRange) -> {
                    dp.setFrom(dateRange.getBeginDate());
                    dp.setTo(dateRange.getEndDate());
                });
        layout.addTopComponent(from);

        layout.setRefreshAction(e -> refresh());
    }


    private void buildGrid(GridViewLayout layout) {
        dataProvider = new UnnaxLogDataProvider(db);
        binder = new Binder<>();
        binder.setBean(dataProvider);
        binder.addValueChangeListener(e -> refresh());
        layout.setContent(unnaxComponents.grid(dataProvider, this));
    }

    @Override
    public void refresh() {
        dataProvider.refreshAll();
    }

}
