package fintech.spain.alfa.product.dc.impl;

import fintech.lending.core.loan.events.LoanDerivedValuesUpdated;
import fintech.spain.alfa.product.dc.DcFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DcEventListeners {

    @Autowired
    private DcFacade dcFacade;

    @EventListener
    public void onLoanEvent(LoanDerivedValuesUpdated event) {
        Long loanId = event.getLoan().getId();
        dcFacade.postLoan(loanId, event.getState(), event.getStatus(), false);
    }

}
