package fintech

import spock.lang.Specification

class ExtraStringUtilsTest extends Specification {

    def "Comma separate list contains string"() {
        expect:
        !ExtraStringUtils.commaSeparatedListContainsValueIgnoringCaseAndWhitespaces(null, null)
        !ExtraStringUtils.commaSeparatedListContainsValueIgnoringCaseAndWhitespaces(null, "")
        !ExtraStringUtils.commaSeparatedListContainsValueIgnoringCaseAndWhitespaces("", null)
        !ExtraStringUtils.commaSeparatedListContainsValueIgnoringCaseAndWhitespaces("", "")
        ExtraStringUtils.commaSeparatedListContainsValueIgnoringCaseAndWhitespaces("Abc", "Abc")
        ExtraStringUtils.commaSeparatedListContainsValueIgnoringCaseAndWhitespaces("Abc,  Def  ,Cfg", "def")
        ExtraStringUtils.commaSeparatedListContainsValueIgnoringCaseAndWhitespaces("Abc,  D ef  ,Cfg", "d e f")
        !ExtraStringUtils.commaSeparatedListContainsValueIgnoringCaseAndWhitespaces("Abc,  D ef  ,Cfg", "d e fc")
    }

    def "Similarity"() {
        expect:
        ExtraStringUtils.similarity("", "") == 1.0d
        ExtraStringUtils.similarity("a", "") == 0.0d
        ExtraStringUtils.similarity("john", "john") == 1.0d
        ExtraStringUtils.similarity("john", "johny") == 0.8d
        ExtraStringUtils.similarity("johny", "john") == 0.8d
        ExtraStringUtils.similarity("john", "jihn") == 0.75d
    }
}
