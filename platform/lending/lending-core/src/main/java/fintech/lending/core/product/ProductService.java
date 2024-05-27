package fintech.lending.core.product;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface ProductService {

    Product getProduct(Long productId);

    <T extends ProductSettings> T getSettings(Long productId, Class<T> settingsClass);

    <T extends ProductSettings> void updateSettings(Long productId, @Valid T settings);

    <T extends ProductSettings> T parseSettings(String json, Class<T> settingsClass);
}
