package fintech.bo.api.model.loan;


import lombok.Data;

import java.util.List;

@Data
public class GetExtensionPricesResponse {

    private List<ExtensionPrice> extensions;
}
