package fintech.bo.api.server;

import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.product.UpdateProductSettingsRequest;
import fintech.bo.api.server.services.ProductApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class ProductApiController {

    private final ProductApiService productApiService;

    @Autowired
    public ProductApiController(ProductApiService productApiService) {
        this.productApiService = productApiService;
    }

    @Secured({BackofficePermissions.ADMIN})
    @PostMapping(path = "/api/bo/products/update-settings")
    public void update(@RequestBody @Valid UpdateProductSettingsRequest request) {
        productApiService.update(request);
    }

}
