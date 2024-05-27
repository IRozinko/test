package fintech.bo.api.model.address;

import lombok.Data;

@Data
public class EditAddressCatalogEntryRequest {

    private Long id;
    private AddressCatalogEntry entry;
}
