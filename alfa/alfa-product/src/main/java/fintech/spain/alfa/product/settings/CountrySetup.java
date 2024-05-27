package fintech.spain.alfa.product.settings;

import au.com.bytecode.opencsv.CSVReader;
import fintech.ClasspathUtils;
import fintech.crm.country.db.CountryEntity;
import fintech.crm.country.db.CountryRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CountrySetup {

    private final CountryRepository repository;

    @Autowired
    public CountrySetup(CountryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void setUp() {
        if (repository.count() == 0) {
            repository.save(defaultCountries());
        }
    }

    @SneakyThrows
    private List<CountryEntity> defaultCountries() {
        CSVReader reader = new CSVReader(new StringReader(ClasspathUtils.resourceToString("countries/countries.csv")));
        return reader.readAll()
            .stream()
            .map(a -> country(a[0], a[1], a[2], a[3], a[4], a[5]))
            .collect(Collectors.toList());
    }


    private static CountryEntity country(String code, String name, String displayName, String nationality,
                                         String nationalityDisplayName, String homeCountry) {
        CountryEntity r = new CountryEntity();
        r.setCode(code.trim());
        r.setName(name.trim());
        r.setDisplayName(displayName.trim());
        r.setNationality(nationality.trim());
        r.setNationalityDisplayName(nationalityDisplayName.trim());
        r.setHomeCountry(Boolean.valueOf(homeCountry.trim()));
        return r;
    }
}
