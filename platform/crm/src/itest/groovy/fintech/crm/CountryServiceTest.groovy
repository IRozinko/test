package fintech.crm

import fintech.crm.country.CountryService
import fintech.crm.country.db.CountryEntity
import fintech.crm.country.db.CountryRepository
import fintech.crm.country.impl.CountryNotValidException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Subject

class CountryServiceTest extends BaseSpecification {

    @Subject
    @Autowired
    CountryService countryService

    @Autowired
    CountryRepository countryRepository

    @Autowired
    TransactionTemplate txTemplate

    def "get country with empty database"() {
        when:
        countryService.getCountry("test")

        then:
        thrown(CountryNotValidException)
    }

    def "get country with code case insensitive"() {
        given:
        initCountries()

        when:
        def result = countryService.getCountry("es")

        then:
        result
        result.name == "Spain"
        result.homeCountry
    }

    def "get country with code null"() {
        given:
        initCountries()

        when:
        def result = countryService.getCountry(null)

        then:
        noExceptionThrown()
        !result
    }

    def "check country is valid with empty database"() {
        when:
        def result = countryService.isValid("test")

        then:
        noExceptionThrown()
        !result
    }

    def "check country is valid case insensitive"() {
        given:
        initCountries()

        when:
        def result = countryService.isValid("es")

        then:
        result
    }

    def "check country is valid with code null"() {
        given:
        initCountries()

        when:
        def result = countryService.isValid(null)

        then:
        noExceptionThrown()
        !result
    }

    private void initCountries() {
        txTemplate.execute({
            countryRepository.save(new CountryEntity(id: 1L, code: "ES", name: "Spain", displayName: "España", nationality: "Spanish", nationalityDisplayName: "Español", homeCountry: true))
            countryRepository.save(new CountryEntity(id: 2L, code: "IT", name: "Italy", displayName: "Italia", nationality: "Italian", nationalityDisplayName: "Italiano", homeCountry: false))
        })
    }
}
