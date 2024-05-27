package fintech.payments.db;


import com.querydsl.core.types.Predicate;
import fintech.db.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends BaseRepository<PaymentEntity, Long> {

    List<PaymentEntity> findByAccountId(Long institutionAccountId);

    Optional<PaymentEntity> findByBankOrderCode(String bankOrderCode);
}
