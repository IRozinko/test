package fintech.bo.api.model.product;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateProductSettingsRequest {

    @NotNull
    private Long productId;

    @NotNull
    private String settingsJson;

    @NotNull
    private String productType;

}
