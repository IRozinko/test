package fintech.bo.api.model.cms;

import lombok.Data;

@Data
public class AddCmsItemRequest {
    private String key;
    private String locale;
    private String type;
    private String scope;
    private String description;
}
