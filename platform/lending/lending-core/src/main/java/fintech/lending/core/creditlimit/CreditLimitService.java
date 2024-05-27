package fintech.lending.core.creditlimit;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CreditLimitService {

    void addLimit(AddCreditLimitCommand command);

    Optional<CreditLimit> getClientLimit(Long clientId, LocalDate when);

    Optional<CreditLimit> getClientLimit(Long clientId);

    List<CreditLimit> findAll(long clientId);
}
