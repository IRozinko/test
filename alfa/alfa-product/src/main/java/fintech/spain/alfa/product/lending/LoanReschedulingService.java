package fintech.spain.alfa.product.lending;

import fintech.spain.dc.command.RescheduleCommand;
import fintech.spain.alfa.product.db.LoanReschedulingEntity;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Validated
public interface LoanReschedulingService {

    void createLoanRescheduling(RescheduleCommand command);

    void cancel(Long loanId, LocalDate cancelDate);

    void updateExpireDate(Long reschedulingLoanId, LocalDate date);

    Optional<LoanRescheduling> findLoanRescheduling(LoanReschedulingQuery query);

    List<LoanReschedulingEntity> findRescheduledLoans(LoanReschedulingStatus status);

    void close(Long reschedulingLoanId);

    void pending(Long reschedulingLoanId, LocalDate date);
}
