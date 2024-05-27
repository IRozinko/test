package fintech.crm.country.impl;

import fintech.crm.country.Country;
import fintech.crm.country.CountryService;
import fintech.crm.country.db.CountryEntity;
import fintech.crm.country.db.CountryRepository;
import fintech.crm.db.Entities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CountryServiceBean implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryServiceBean(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Country getCountry(String countryCode) {
        if (countryCode == null) {
            return null;
        }
        Optional<CountryEntity> countryEntity = countryRepository.findByCodeIgnoreCase(countryCode);
        return countryEntity.map(CountryEntity::toValueObject)
            .orElseThrow(() -> new CountryNotValidException("Country with code " + countryCode + " not valid"));
    }

    @Override
    public boolean isValid(String countryCode) {
        Optional<CountryEntity> countryEntity = countryRepository.findByCodeIgnoreCase(countryCode);
        return countryEntity.isPresent();
    }

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll(Entities.country.code.asc())
            .stream()
            .map(CountryEntity::toValueObject)
            .collect(Collectors.toList());
    }
}
