package fintech.bo.components.period;

import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.PeriodsApiClient;
import fintech.bo.components.GridHelper;
import fintech.bo.components.payments.PaymentQueries;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static fintech.bo.components.Formats.formatDateTime;
import static fintech.bo.db.jooq.lending.tables.Period.PERIOD;

@Component
public class PeriodsComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private PeriodsApiClient periodsApiClient;

    @Autowired
    private PeriodQueries periodQueries;

    @Autowired
    private PaymentQueries paymentQueries;

    public PeriodsDataProvider dataProvider() {
        return new PeriodsDataProvider(db);
    }

    public Grid<Record> grid(PeriodsDataProvider dataProvider) {
        Grid<Record> grid = new Grid<>();

        grid.addColumn((record) -> closePeriodButton(record, grid), new ComponentRenderer()).setSortable(false).setWidth(100);
        Grid.Column<Record, LocalDate> date = grid.addColumn(record -> record.get(PERIOD.PERIOD_DATE)).setCaption("Period date").setId(PERIOD.PERIOD_DATE.getName()).setWidth(150);
        grid.addColumn(record -> record.get(PERIOD.CLOSE_DATE)).setCaption("Close date").setId(PERIOD.CLOSE_DATE.getName()).setWidth(150);
        grid.addColumn(record -> record.get(PERIOD.STATUS)).setCaption("Status").setId(PERIOD.STATUS.getName()).setWidth(150);
        grid.addColumn(record -> record.get(PERIOD.STATUS_DETAIL)).setCaption("Status detail").setId(PERIOD.STATUS_DETAIL.getName()).setWidth(150);
        grid.addColumn(record -> formatDateTime(record.get(PERIOD.CLOSING_STARTED_AT))).setCaption("Closing started at").setId(PERIOD.CLOSING_STARTED_AT.getName()).setWidth(170);
        grid.addColumn(record -> formatDateTime(record.get(PERIOD.CLOSING_ENDED_AT))).setCaption("Closing ended at").setId(PERIOD.CLOSING_ENDED_AT.getName()).setWidth(170);
        grid.addColumn(record -> record.get(PERIOD.RESULT_LOG)).setCaption("Result log").setId(PERIOD.RESULT_LOG.getName());

        grid.setSortOrder(new GridSortOrderBuilder<Record>().thenDesc(date));
        grid.setSizeFull();
        grid.setFrozenColumnCount(1);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        GridHelper.addTotalCountAsCaption(grid, dataProvider);
        grid.setDataProvider(dataProvider);
        return grid;
    }

    private com.vaadin.ui.Component closePeriodButton(Record record, Grid<Record> grid) {
        LocalDate periodDate = record.get(PERIOD.PERIOD_DATE);
        boolean isNextPeriod = periodDate.isEqual(periodQueries.nextClosingPeriod());
        boolean isClosing = record.get(PERIOD.STATUS_DETAIL).equalsIgnoreCase(PeriodConstants.STATUS_DETAIL_CLOSING);
        if (!isNextPeriod || isClosing) {
            return new Label("");
        }

        boolean isOpen = record.get(PERIOD.STATUS).equalsIgnoreCase(PeriodConstants.STATUS_OPEN);

        Button closeButton = isOpen ? new Button("Close") : new Button("Retry");
        closeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        closeButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        closeButton.addClickListener(event -> {
            ClosePeriodDialog closePeriodDialog = new ClosePeriodDialog(periodDate, periodsApiClient, paymentQueries);
            closePeriodDialog.addCloseListener((e) -> grid.getDataProvider().refreshAll());
            grid.getUI().addWindow(closePeriodDialog);
        });
        return closeButton;
    }


}
