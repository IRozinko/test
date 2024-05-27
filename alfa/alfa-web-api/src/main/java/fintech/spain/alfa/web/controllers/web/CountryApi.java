package fintech.spain.alfa.web.controllers.web;

import fintech.crm.country.CountryService;
import fintech.spain.alfa.web.models.CountryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CountryApi {

    private final CountryService countryService;

    @Autowired
    public CountryApi(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/api/public/web/countries")
    public List<CountryModel> getCountries() {
        return countryService.getAllCountries()
            .stream()
            .map(CountryModel::new)
            .collect(Collectors.toList());
    }
}
