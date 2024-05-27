package fintech.crm.documents;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"number"})
public class AddIdentityDocumentCommand {
    
    private Long clientId;
    private String type;
    private String number;
    private String countryCodeOfNationality;
}
