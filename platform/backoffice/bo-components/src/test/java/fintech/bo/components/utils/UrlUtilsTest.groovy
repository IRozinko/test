package fintech.bo.components.utils

import spock.lang.Specification

import static fintech.bo.components.utils.UrlUtils.getParam

class UrlUtilsTest extends Specification {

    def "getParam"() {
        given:
        def url1 = "?tel=73997973&ivxServID=16516&ivxCID=&ivxSessID=67151248476663186&ivxSaliente="

        expect:
        getParam(url1, "t") == null
        getParam(url1, "te") == null
        getParam(url1, "tel ") == null
        getParam(url1, "tel") == "73997973"
        getParam(url1, "ivxServID") == "16516"
        getParam(url1, "ivxCID") == null
        getParam(url1, "ivxSessID") == "67151248476663186"
        getParam(url1, "ivxSaliente") == null
    }

    def "get tel number"() {
        expect:
        getParam("?tel=123", "tel") == "123"
        getParam("?tel=123&", "tel") == "123"
        getParam("?tel=123", "tel") == "123"
        getParam("tel=123", "tel") == null
    }

}
