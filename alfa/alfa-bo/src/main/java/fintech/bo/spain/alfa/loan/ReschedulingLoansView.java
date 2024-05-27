package fintech.bo.spain.alfa.loan;

import com.vaadin.spring.annotation.SpringView;
import fintech.bo.components.loan.AbstractReschedulingView;

@SpringView(name = ReschedulingLoansView.NAME)
public class ReschedulingLoansView extends AbstractReschedulingView {

    protected ReschedulingLoansView(AlfaReschedulingComponents reschedulingComponents) {
        super(reschedulingComponents);
    }
}
