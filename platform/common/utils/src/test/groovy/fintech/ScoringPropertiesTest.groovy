package fintech

import spock.lang.Specification

class ScoringPropertiesTest extends Specification {

    def "Put"() {
        given:
        ScoringProperties props = new ScoringProperties("test")

        when:
        props.put("foo", 1)
        props.setProperty("bar", "2")

        then:
        props['test_foo'] == 1
        props['test_bar'] == "2"
    }

}
