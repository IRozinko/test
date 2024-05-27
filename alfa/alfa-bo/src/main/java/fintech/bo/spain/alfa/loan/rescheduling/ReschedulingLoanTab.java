package fintech.bo.spain.alfa.loan.rescheduling;

import com.vaadin.ui.VerticalLayout;
import org.jooq.DSLContext;

public class ReschedulingLoanTab extends VerticalLayout {

    private final long loanId;
    private final DSLContext db;

    public ReschedulingLoanTab(long loanId, DSLContext db) {
        super();
        this.loanId = loanId;
        this.db = db;
        render();
    }

    private void render() {
        removeAllComponents();
        addComponent(new ReschedulingLoanGrid(new ReschedulingLoanGridDataProvider(loanId, db)));
    }

}
