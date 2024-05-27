package fintech.crm.marketing.impl;

import fintech.crm.marketing.MarketingConsentService;
import fintech.crm.marketing.event.ClientMarketingConsentChanged;
import fintech.crm.marketing.model.MarketingConsentLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class MarketingConsentServiceBean implements MarketingConsentService {

    private final MarketingConsentLogRepository repository;

    @Autowired
    public MarketingConsentServiceBean(MarketingConsentLogRepository repository) {
        this.repository = repository;
    }

    @Override
    @EventListener
    public void handleMarketingConsentChanged(ClientMarketingConsentChanged event) {
        repository.save(new MarketingConsentLogEntity()
            .setClientId(event.getClientId())
            .setTimestamp(event.getWhen())
            .setValue(event.getNewValue()))
            .setSource(event.getSource())
            .setNote(event.getNote());
    }
}
