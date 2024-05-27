package fintech.nordigen.utils

import fintech.ClasspathUtils
import fintech.JsonUtils
import fintech.nordigen.json.NordigenJson
import spock.lang.Specification

import static fintech.DateUtils.date

class NordigenParserTest extends Specification {

    def "Sum"() {
        given:
        def json = JsonUtils.readValue(ClasspathUtils.resourceToString("nordigen-large-statement.json"), NordigenJson.class)
        def parser = new NordigenParser(json)

        expect:
        parser.sum("ES5984249479828539118875", [-1] as Set, date("2001-01-01"), date("2100-01-01"), false, false) == 0.0d
        parser.sum("ES5984249479828539118875", [-1] as Set, date("2017-08-31"), date("2017-08-31"), false, false) == 0.0d
        parser.sum("ES5984249479828539118875", [84] as Set, date("2017-08-31"), date("2017-08-31"), false, false) == -929.0d
        parser.sum("ES5984249479828539118875", [85] as Set, date("2017-08-31"), date("2017-08-31"), false, false) == -929.0d
        parser.sum("ES5984249479828539118875", [84] as Set, date("2017-08-30"), date("2017-08-31"), false, false) == -1409.0d
        parser.sum("ES5984249479828539118875", [23] as Set, date("2017-08-28"), date("2017-08-28"), false, false) == 810.0d

        and: "account not found"
        parser.sum("ES5984249479828539118876", [23] as Set, date("2001-01-01"), date("2100-01-01"), false, false) == 0.0d
    }
}
