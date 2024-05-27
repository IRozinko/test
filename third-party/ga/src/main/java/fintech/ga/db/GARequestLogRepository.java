package fintech.ga.db;

import fintech.db.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GARequestLogRepository extends BaseRepository<GARequestLogEntity, Long> {

    @Query("from GARequestLogEntity log where log.clientId = ?1 order by log.updatedAt")
    List<GARequestLogEntity> findByClientId(long clientId);
}
