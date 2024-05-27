package fintech.bo.components.loan;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.JooqClientDataService;
import org.jooq.DSLContext;
import org.jooq.Record;

import static fintech.bo.components.loan.LoanReschedulingConstants.STATUS_CANCELLED;
import static fintech.bo.components.loan.LoanReschedulingConstants.STATUS_PENDING;
import static fintech.bo.components.loan.LoanReschedulingConstants.STATUS_RESCHEDULED;
import static fintech.bo.components.loan.LoanReschedulingConstants.STATUS_RESCHEDULED_PAID;
import static fintech.bo.db.jooq.alfa.Tables.LOAN_RESCHEDULING;

public abstract class ReschedulingComponents {

    private final DSLContext db;
    private final ReschedulingQueries reschedulingQueries;
    private final JooqClientDataService jooqClientDataService;

    protected ReschedulingComponents(DSLContext db, ReschedulingQueries reschedulingQueries, JooqClientDataService jooqClientDataService) {
        this.db = db;
        this.reschedulingQueries = reschedulingQueries;
        this.jooqClientDataService = jooqClientDataService;
    }

    public ComboBox<String> statusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Rescheduling status");
        comboBox.setItems(reschedulingQueries.findStatuses());
        comboBox.setTextInputAllowed(false);
        comboBox.setWidth(200, Sizeable.Unit.PIXELS);
        return comboBox;
    }

    public ReschedulingDataProvider dataProvider() {
        return new ReschedulingDataProvider(db, jooqClientDataService);
    }

    public Grid<Record> grid(ReschedulingDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        addGridColumns(builder);
        builder.sortDesc(LOAN_RESCHEDULING.ID);
        return builder.build(dataProvider);
    }

    public static StyleGenerator<Record> reschedulingStatusStyle() {
        return item -> {
            String status = item.get(LOAN_RESCHEDULING.STATUS);
            if (STATUS_RESCHEDULED_PAID.equals(status)) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else if (STATUS_CANCELLED.equals(status)) {
                return BackofficeTheme.TEXT_GRAY;
            } else if (STATUS_PENDING.equals(status)) {
                return BackofficeTheme.TEXT_DANGER;
            } else if (STATUS_RESCHEDULED.equals(status)) {
                return BackofficeTheme.TEXT_ACTIVE;
            } else {
                return "";
            }
        };
    }

    protected abstract void addGridColumns(JooqGridBuilder<Record> builder);

}
