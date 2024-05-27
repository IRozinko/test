package fintech.spain.alfa.product.documents;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InvalidateIdentificationDocument {
    private Long identificationDocumentId;
}
