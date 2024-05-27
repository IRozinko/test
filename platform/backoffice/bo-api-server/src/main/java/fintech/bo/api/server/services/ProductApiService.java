package fintech.bo.api.server.services;

import com.google.common.collect.ImmutableMap;
import fintech.Validate;
import fintech.bo.api.model.product.UpdateProductSettingsRequest;
import fintech.lending.core.product.Product;
import fintech.lending.core.product.ProductService;
import fintech.lending.core.product.ProductSettings;
import fintech.lending.core.product.ProductType;
import fintech.lending.creditline.settings.CreditLineProductSettings;
import fintech.lending.payday.settings.PaydayProductSettings;
import fintech.lending.revolving.settings.RevolvingProductSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ProductApiService {

    private static final Map<ProductType, Class<? extends ProductSettings>> productTypeMapping = ImmutableMap.of(
        ProductType.PAYDAY, PaydayProductSettings.class,
        ProductType.LINE_OF_CREDIT, CreditLineProductSettings.class,
        ProductType.REVOLVING, RevolvingProductSettings.class
    );

    private final ProductService productService;

    @Autowired
    public ProductApiService(ProductService productService) {
        this.productService = productService;
    }

    public void update(UpdateProductSettingsRequest request) {
        log.info("Updating product settings [{}]", request);

        ProductType productType = ProductType.valueOf(request.getProductType());
        Class<? extends ProductSettings> clazz = productTypeMapping.get(productType);

        ProductSettings productSettings = productService.parseSettings(request.getSettingsJson(), clazz);
        Product currentProduct = productService.getProduct(request.getProductId());
        Validate.isTrue(currentProduct.getProductType().equals(productType), "Cannot update product %d type %s with settings of type %s", request.getProductId(), currentProduct.getProductType(), productType);

        productService.updateSettings(request.getProductId(), productSettings);
    }
}
