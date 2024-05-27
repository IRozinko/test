package fintech.spain.alfa.web.models;

import fintech.crm.country.Country;
import lombok.Getter;

@Getter
public class CountryModel {
    private String code;
    private String name;

    public CountryModel(Country country) {
        this.code = country.getCode();
        this.name = country.getDisplayName();
    }
}
