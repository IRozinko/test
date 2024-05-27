package fintech.spain.alfa.product.loc.db;

import fintech.db.BaseRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocBatchRepository extends BaseRepository<LocBatchEntity, Long> {

    @Query(value = "select nextval('alfa.loc_batch_sequence')", nativeQuery = true)
    Long generateBatchNumber();
}
