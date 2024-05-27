package fintech.spain.alfa.bo.api;

import fintech.spain.alfa.bo.api.services.LoanUpsellService;
import fintech.spain.alfa.bo.model.UpdateLoanUpsellAmountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoanUpsellApi {

    @Autowired
    private LoanUpsellService loanUpsellService;

    @PostMapping("api/bo/loan-application/update-loan-upsell-amount")
    public void updateLoanUpsellAmount(@RequestBody UpdateLoanUpsellAmountRequest request) {
        loanUpsellService.updateLoanUpsellAmount(request);
    }
}
