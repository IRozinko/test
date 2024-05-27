package fintech.scoring.values

import fintech.scoring.values.spi.ScoringValuesProvider
import spock.lang.Specification

class ScoringValuesProviderTest extends Specification {

    def "FlattenPojo"() {
        when:
        def props = new DummyProvider().provide(1)

        then:
        props['test_data_test_field'] == 'test'
    }

    class DummyProvider implements ScoringValuesProvider {

        @Override
        Properties provide(long clientId) {
            return flattenPojo("test", new TestData())
        }
    }

    class TestData {

        Data data

        TestData() {
            data = new Data(testField: "test")
        }
    }

    class Data {

        String testField
    }



}
