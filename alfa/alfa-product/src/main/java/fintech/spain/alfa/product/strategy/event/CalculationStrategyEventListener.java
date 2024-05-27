package fintech.spain.alfa.product.strategy.event;

import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsItemType;
import fintech.cms.spi.CmsRegistry;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.strategy.CalculationStrategyCmsItemKey;
import fintech.strategy.event.CalculationStrategySavedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CalculationStrategyEventListener {

    @Autowired
    private CmsRegistry cmsRegistry;

    @EventListener
    public void onCalculationStrategySavedEvent(CalculationStrategySavedEvent event) {
        CalculationStrategyCmsItemKey key = new CalculationStrategyCmsItemKey(event.getStrategy().getStrategyType(), event.getStrategy().getCalculationType());
        if (!cmsRegistry.findItem(key.get(), AlfaConstants.LOCALE).isPresent()) {
            CmsItem item = new CmsItem();
            item.setKey(key.get());
            item.setLocale(AlfaConstants.LOCALE);
            item.setDescription("CMS Item for calculation strategy");
            item.setScope("");
            item.setItemType(CmsItemType.EMBEDDABLE);
            cmsRegistry.saveItem(item, false);
        }
    }
}
