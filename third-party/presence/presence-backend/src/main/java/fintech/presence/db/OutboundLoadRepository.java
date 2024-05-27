package fintech.presence.db;

import fintech.db.BaseRepository;

import java.util.Optional;

public interface OutboundLoadRepository extends BaseRepository<OutboundLoadEntity, Long> {

    Optional<OutboundLoadEntity> getFirstByServiceIdAndLoadId(Integer serviceId, Integer loadId);
}
