package fintech.ga.db;


import fintech.db.BaseRepository;

import java.util.Optional;

public interface GAClientDataRepository extends BaseRepository<GAClientDataEntity, Long> {

    Optional<GAClientDataEntity> findByClientId(long clientId);
}
