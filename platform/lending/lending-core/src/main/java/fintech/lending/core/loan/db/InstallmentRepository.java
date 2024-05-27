package fintech.lending.core.loan.db;


import fintech.db.BaseRepository;

import java.util.Optional;

public interface InstallmentRepository extends BaseRepository<InstallmentEntity, Long> {

    Optional<InstallmentEntity> findByLoanId(Long loandId);

}
