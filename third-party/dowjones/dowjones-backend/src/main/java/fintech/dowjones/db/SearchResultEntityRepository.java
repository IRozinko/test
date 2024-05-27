package fintech.dowjones.db;

import fintech.db.BaseRepository;

import java.util.Optional;

public interface SearchResultEntityRepository extends BaseRepository<SearchResultEntity, Long> {

    Optional<SearchResultEntity> findFirstByRequestId(long requestId);

}
