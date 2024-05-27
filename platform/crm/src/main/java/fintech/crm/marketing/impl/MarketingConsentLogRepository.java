package fintech.crm.marketing.impl;

import fintech.crm.marketing.model.MarketingConsentLogEntity;
import fintech.db.BaseRepository;

import java.util.List;

public interface MarketingConsentLogRepository extends BaseRepository<MarketingConsentLogEntity, Long> {

    List<MarketingConsentLogEntity> findByClientId(long clientId);
}
