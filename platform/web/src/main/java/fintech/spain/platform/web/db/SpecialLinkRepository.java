package fintech.spain.platform.web.db;

import fintech.db.BaseRepository;
import fintech.spain.platform.web.SpecialLinkType;

import java.util.Optional;

public interface SpecialLinkRepository extends BaseRepository<SpecialLinkEntity, Long> {

    Optional<SpecialLinkEntity> getFirstByToken(String token);

    Optional<SpecialLinkEntity> getFirstByClientIdAndType(long clientId, SpecialLinkType type);
}
