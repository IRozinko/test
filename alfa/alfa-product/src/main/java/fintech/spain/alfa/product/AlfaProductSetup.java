package fintech.spain.alfa.product;

import fintech.JsonUtils;
import fintech.lending.core.product.ProductType;
import fintech.lending.core.product.db.ProductEntity;
import fintech.lending.core.product.db.ProductRepository;
import fintech.lending.payday.settings.PaydayOfferSettings;
import fintech.lending.payday.settings.PaydayProductSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static fintech.BigDecimalUtils.amount;

@Slf4j
@Component
public class AlfaProductSetup {

    @Autowired
    ProductRepository productRepository;

    @Transactional
    public void init() {
        ProductEntity entity = productRepository.findOne(AlfaConstants.PRODUCT_ID);
        if (entity == null) {
            log.info("Adding new product");
            entity = new ProductEntity();
            entity.setId(AlfaConstants.PRODUCT_ID);
            entity.setProductType(ProductType.PAYDAY);
        } else {
            log.info("Product already exists, skipping");
            return;
        }

        PaydayProductSettings settings = new PaydayProductSettings();

        PaydayOfferSettings publicOfferSettings = new PaydayOfferSettings();
        publicOfferSettings.setMinAmount(amount(50));
        publicOfferSettings.setMaxAmount(amount(300));
        publicOfferSettings.setAmountStep(amount(10));
        publicOfferSettings.setDefaultAmount(amount(100));
        publicOfferSettings.setMinTerm(7);
        publicOfferSettings.setMaxTerm(30);
        publicOfferSettings.setTermStep(1);
        publicOfferSettings.setDefaultTerm(15);
        settings.setPublicOfferSettings(publicOfferSettings);

        PaydayOfferSettings clientOfferSettings = new PaydayOfferSettings();
        clientOfferSettings.setMinAmount(amount(50));
        clientOfferSettings.setMaxAmount(amount(300));
        clientOfferSettings.setAmountStep(amount(10));
        clientOfferSettings.setDefaultAmount(amount(300));
        clientOfferSettings.setMinTerm(7);
        clientOfferSettings.setMaxTerm(30);
        clientOfferSettings.setTermStep(1);
        clientOfferSettings.setDefaultTerm(15);
        clientOfferSettings.setSetSliderToMaxAmount(true);
        clientOfferSettings.setUseCreditLimitAsMaxAmount(true);
        settings.setClientOfferSettings(clientOfferSettings);

        entity.setDefaultSettingsJson(JsonUtils.writeValueAsString(settings));
        productRepository.saveAndFlush(entity);
    }
}
