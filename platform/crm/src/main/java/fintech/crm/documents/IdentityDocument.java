package fintech.crm.documents;

import fintech.crm.country.Country;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"number"})
public class IdentityDocument {

    private Long id;
    private Long clientId;
    private String type;
    private String number;
    private Country nationality;
}
