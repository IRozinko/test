package fintech.bo.spain.alfa.applications;

import com.vaadin.spring.annotation.SpringView;
import fintech.TimeMachine;
import fintech.bo.components.application.AbstractLoanApplicationsView;
import fintech.bo.components.layouts.GridViewLayout;

@SpringView(name = LoanApplicationsView.NAME)
public class LoanApplicationsView extends AbstractLoanApplicationsView {

    protected LoanApplicationsView(AlfaLoanApplicationComponents applicationComponents) {
        super(applicationComponents);
    }

    @Override
    protected void buildTop(GridViewLayout layout) {
        super.buildTop(layout);
        submitDate.setBeginDate(TimeMachine.today().minusMonths(1));
        submitDate.setEndDate(TimeMachine.today());
    }

    @Override
    protected void buildGrid(GridViewLayout layout) {
        grid = ((AlfaLoanApplicationComponents) applicationComponents).mainGrid(dataProvider);
        layout.setContent(grid);
    }

}
