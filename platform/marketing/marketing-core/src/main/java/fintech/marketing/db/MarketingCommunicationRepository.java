package fintech.marketing.db;

import fintech.db.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MarketingCommunicationRepository extends BaseRepository<MarketingCommunicationEntity, Long> {

    @Query("from MarketingCommunicationEntity e where e.marketingCampaignId = ?1 and e.status = 'QUEUED'")
    List<MarketingCommunicationEntity> findQueuedByMarketingCampaignId(Long id);
}
