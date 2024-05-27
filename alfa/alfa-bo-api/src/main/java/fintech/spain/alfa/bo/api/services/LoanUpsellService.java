package fintech.spain.alfa.bo.api.services;

import fintech.TimeMachine;
import fintech.spain.alfa.bo.model.UpdateLoanUpsellAmountRequest;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class LoanUpsellService {

    @Autowired
    private UnderwritingFacade underwritingFacade;

    public void updateLoanUpsellAmount(@RequestBody UpdateLoanUpsellAmountRequest request) {
        underwritingFacade.prepareOffer(request.getLoanApplicationId(), TimeMachine.today(), request.getAmount());
    }
}
