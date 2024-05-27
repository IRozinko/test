package fintech.bo.components.dowjones;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.dowjones.dto.SearchResultDTO;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardFeatures;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static fintech.bo.components.views.StandardScopes.SCOPE_CLIENT;
import static fintech.bo.db.jooq.dowjones.Tables.MATCH;
import static fintech.bo.db.jooq.dowjones.Tables.SEARCH_RESULT;

public class DowJonesMatchResultListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    @Autowired
    private SearchResultComponent searchResultComponent;

    private BoComponentContext context;
    private DowJonesMatchResultDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new DowJonesMatchResultDataProvider(this.db).setComponentContext(context);
        if (!context.requiresFeature(StandardFeatures.FEATURE_COMPACT_VIEW)) {
            addComponent(filter());
        }
        addComponentsAndExpand(grid());
    }

    @Override
    public void refresh() {
        this.dataProvider.refreshAll();
    }

    private Component filter() {
        HorizontalLayout layout = new HorizontalLayout();

        ComboBox<SearchResultDTO> searchResult = searchResultComponent.searchResultComboBox();
        searchResult.setWidth(250, Unit.PIXELS);
        searchResult.addValueChangeListener(event -> {
            context.withScope(SCOPE_CLIENT, Optional.ofNullable(event.getValue()).map(SearchResultDTO::getId).orElse(null));
            dataProvider.setComponentContext(context);
            refresh();
        });

        Button refresh = new Button("Refresh");
        refresh.addClickListener(e -> refresh());

        layout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        layout.addComponents(searchResult, refresh);
        return layout;
    }


    public Component grid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(SEARCH_RESULT.ID);
        if (!context.inScope(SCOPE_CLIENT)) {
            builder.addLinkColumn(MATCH.SEARCH_RESULT_ID, r -> DowJonesSearchResultListComponent.clientLink(r.get(MATCH.SEARCH_RESULT_ID)));
        }
        builder.addColumn(MATCH.PRIMARY_NAME);
        builder.addColumn(MATCH.GENDER);
        builder.addColumn(MATCH.COUNTRY_CODE);
        builder.addColumn(MATCH.RISK_INDICATOR);
        builder.addColumn(MATCH.SCORE);
        builder.addColumn(MATCH.DATE_OF_BIRTH_YEAR);
        builder.addColumn(MATCH.DATE_OF_BIRTH_MONTH);
        builder.addColumn(MATCH.DATE_OF_BIRTH_DAY);
        builder.addColumn(MATCH.FIRST_NAME);
        builder.addColumn(MATCH.LAST_NAME);
        builder.addColumn(MATCH.SECOND_FIRST_NAME);
        builder.addColumn(MATCH.SECOND_LAST_NAME);
        builder.addColumn(MATCH.MAIDEN_NAME);
        builder.addAuditColumns(MATCH);
        builder.sortDesc(MATCH.ID);
        return builder.build(dataProvider);
    }
}
