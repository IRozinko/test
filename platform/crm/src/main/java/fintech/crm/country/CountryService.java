package fintech.crm.country;

import java.util.List;

public interface CountryService {

    Country getCountry(String countryCode);

    boolean isValid(String countryCode);

    List<Country> getAllCountries();
}
