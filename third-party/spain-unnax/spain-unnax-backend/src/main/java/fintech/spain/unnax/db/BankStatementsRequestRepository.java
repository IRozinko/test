package fintech.spain.unnax.db;

import fintech.db.BaseRepository;

import java.util.Optional;

public interface BankStatementsRequestRepository extends BaseRepository<BankStatementsRequestEntity, Long> {

    Optional<BankStatementsRequestEntity> findByRequestCode(String code);
}
