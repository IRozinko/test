package fintech.scoring.values

import fintech.scoring.values.db.ScoringModelRepository
import fintech.scoring.values.db.ScoringValueSource
import fintech.scoring.values.db.ScoringValueType
import fintech.scoring.values.spi.ScoringValuesProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ScoringValuesServiceTestProfile")
class ScoringValuesServiceTest extends BaseSpecification {

    @Autowired
    ScoringValuesService service

    @Autowired
    ScoringModelRepository modelRepository

    def "ScoringValuesService::collectValues"() {
        when:
        def model = service.collectValues(1)
        def values = modelRepository.getRequired(model.id).values

        then: "Scoring values and model saved"
        values.size() == 2

        values.find({
            it.key == 'first.fake.provider.property'
            it.val == 'true'
            it.src == ScoringValueSource.INNER
            it.type == ScoringValueType.BOOLEAN
        })

        values.find({
            it.key == 'second.fake.provider.property'
            it.val == '10'
            it.src == ScoringValueSource.INNER
            it.type == ScoringValueType.LONG
        })
    }

    def "ScoringValuesService::getValuesAsMap"() {
        when:
        def model = service.collectValues(1)
        def values = service.getValuesAsMap(model.id)

        then:
        values['first.fake.provider.property'] == true
        values['second.fake.provider.property'] == 10L
    }

    def "ScoringValuesService::saveValues"() {
        when:
        def model = service.collectValues(1)
        service.saveValues(model.id, ["test_key": 1])
        def values = modelRepository.getRequired(model.id).values

        then:
        values.size() == 3

        values.find({
            it.key == 'test_key'
            it.val == 1
            it.src == ScoringValueSource.OUTER
            it.type == ScoringValueType.INTEGER
        })
    }

    @Configuration
    @Profile("ScoringValuesServiceTestProfile")
    static class ContextConfiguration {

        @Bean("first_fake_provider")
        ScoringValuesProvider firstFakeProvider() {
            return { clientId -> new Properties(['first.fake.provider.property': true]) }
        }

        @Bean("second_fake_provider")
        ScoringValuesProvider secondFakeProvider() {
            return { clientId -> new Properties(['second.fake.provider.property': 10L]) }
        }
    }
}
