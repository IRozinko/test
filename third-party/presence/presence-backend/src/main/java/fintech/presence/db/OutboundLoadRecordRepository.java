package fintech.presence.db;

import fintech.db.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OutboundLoadRecordRepository extends BaseRepository<OutboundLoadRecordEntity, Long> {

    Optional<OutboundLoadRecordEntity> getFirstByOutboundLoadAndSourceId(OutboundLoadEntity outboundLoad, Integer sourceId);

    @Query(value = "SELECT nextval('" + Entities.SCHEMA + ".source_id_seq')", nativeQuery = true)
    Integer getNextSourceId();

    @Transactional
    @Modifying
    @Query(value = "ALTER SEQUENCE " + Entities.SCHEMA + ".source_id_seq RESTART;", nativeQuery = true)
    void restartSourceIdSequence();
}
