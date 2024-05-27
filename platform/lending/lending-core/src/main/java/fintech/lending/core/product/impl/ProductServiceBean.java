package fintech.lending.core.product.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fintech.lending.core.product.Product;
import fintech.lending.core.product.ProductService;
import fintech.lending.core.product.ProductSettings;
import fintech.lending.core.product.db.ProductEntity;
import fintech.lending.core.product.db.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;

@Transactional
@Component
class ProductServiceBean implements ProductService {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceBean(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product getProduct(Long productId) {
        ProductEntity entity = productRepository.getRequired(productId);
        return entity.toValueObject();
    }

    @Override
    public <T extends ProductSettings> T getSettings(Long productId, Class<T> settingsClass) {
        ProductEntity entity = productRepository.getRequired(productId);
        return parseSettings(entity.getDefaultSettingsJson(), settingsClass);
    }

    @Override
    public <T extends ProductSettings> void updateSettings(Long productId, @Valid T settings) {
        ProductEntity entity = productRepository.getRequired(productId);
        try {
            String settingsJson = mapper.writeValueAsString(settings);
            entity.setDefaultSettingsJson(settingsJson);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(String.format("Failed to write value to json: %s", settings), e);
        }
    }

    @Override
    public <T extends ProductSettings> T parseSettings(String json, Class<T> settingsClass) {
        try {
            return mapper.readValue(json, settingsClass);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to parse to class %s json: %s", settingsClass, json), e);
        }
    }
}
