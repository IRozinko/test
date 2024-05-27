package fintech.bo.spain.alfa.loan;

import com.vaadin.spring.annotation.SpringView;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.loan.AbstractLoansView;

@SpringView(name = LoansView.NAME)
public class LoansView extends AbstractLoansView {

    protected LoansView(AlfaLoanComponents loanComponents) {
        super(loanComponents);
    }

    @Override
    protected void buildTop(GridViewLayout layout) {
        super.buildTop(layout);
    }

    protected void buildGrid(GridViewLayout layout) {
        grid = ((AlfaLoanComponents)loanComponents).mainGrid(dataProvider);
        layout.setContent(grid);
    }
}
