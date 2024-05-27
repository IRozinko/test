package fintech.viventor;

import fintech.viventor.model.ViventorLoan;
import lombok.AllArgsConstructor;
import lombok.Data;

public interface ViventorService {

    ViventorLog postLoan(PostLoanCommand command);

    ViventorLog postLoanPayment(PostLoanPaymentCommand command);

    ViventorLog postLoanPaid(PostLoanPaidCommand command);

    ViventorLog postLoanExtension(PostLoanExtensionCommand command);

    GetLoanResult getLoan(GetLoanCommand command);

    @Data
    @AllArgsConstructor
    class GetLoanResult {
        private ViventorLog response;
        private ViventorLoan viventorLoan;
    }

}
