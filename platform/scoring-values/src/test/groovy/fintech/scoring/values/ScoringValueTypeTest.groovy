package fintech.scoring.values

import fintech.scoring.values.db.ScoringValueData
import fintech.scoring.values.db.ScoringValueSource
import fintech.scoring.values.db.ScoringValueType
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime

import static fintech.DateUtils.localDateTimeToString
import static fintech.DateUtils.toYyyyMmDd

class ScoringValueTypeTest extends Specification {

    @Unroll
    def "ScoringValue::setVal[key: #key, value: #value, type: #type]"() {
        when:
        def variable = new ScoringValueData(ScoringValueSource.INNER, key, value)

        then:
        variable.type == type
        variable.getVal() == string_value
        variable.getValAsObject() == value

        where:
        key         | value                           | string_value                                 | type
        "map"       | ['key': 'value']                | '{\n  "key" : "value"\n}'                    | ScoringValueType.LINKED_HASH_MAP
        "name"      | "Anton"                         | "Anton"                                      | ScoringValueType.STRING
        "bool"      | true                            | "true"                                       | ScoringValueType.BOOLEAN
        "date"      | LocalDate.now()                 | toYyyyMmDd((LocalDate) value)                | ScoringValueType.LOCAL_DATE
        "date_time" | LocalDateTime.now().withNano(0) | localDateTimeToString((LocalDateTime) value) | ScoringValueType.LOCAL_DATE_TIME
        "decimal"   | BigDecimal.TEN                  | "10"                                         | ScoringValueType.BIG_DECIMAL
        "long"      | 1L                              | "1"                                          | ScoringValueType.LONG
        "int"       | 1                               | "1"                                          | ScoringValueType.INTEGER
        "array"     | [1, 2]                          | "[ 1, 2 ]"                                   | ScoringValueType.ARRAY_LIST
    }
}
