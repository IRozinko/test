package fintech.crm.country;

import lombok.Data;

@Data
public class Country {
    private String code;
    private String name;
    private String displayName;
    private String nationality;
    private String nationalityDisplayName;
    private boolean homeCountry;
}
