package fintech.lending.core.snapshot;

import java.time.LocalDate;

public interface LoanDailySnapshotService {

    boolean makeSnapshot(Long loanId, LocalDate when);

    void makeSnapshotOfAllLoans(LocalDate when, boolean force);
}
