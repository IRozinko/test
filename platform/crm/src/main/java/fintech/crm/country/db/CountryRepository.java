package fintech.crm.country.db;

import fintech.db.BaseRepository;

import java.util.Optional;

public interface CountryRepository extends BaseRepository<CountryEntity, Long> {

    Optional<CountryEntity> findByCodeIgnoreCase(String countryCode);
}
