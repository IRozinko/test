package fintech.spain.alfa.product.lending;

import fintech.lending.core.application.LoanApplication;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpiredLoanApplicationNotificationService {
    List<LoanApplication> getExpiredLoanApplications(LocalDateTime when);

    void sendNotification(LoanApplication loanApplication);
}
