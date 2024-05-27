package fintech

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import groovy.json.JsonSlurper
import spock.lang.Specification

import static fintech.JsonUtils.isJsonValid

class JsonUtilsTest extends Specification {

    static class TestObjectClass {
        String name = "Some Name"
        String password = "test1234"
    }

    class TestObjectClassWithIgnore {
        String name = "Some Name"

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password = "test1234"
    }

    def "Convert map to JSON"() {
        given:
        def sourceMap = ["key1": "value1", "key2": "value2"]
        
        when:
        def json = JsonUtils.writeValueAsString(sourceMap)
        def parsedMap = JsonUtils.readValue(json, new TypeReference<Map<String, String>>() {})
        
        then:
        sourceMap == parsedMap
    }

    def "Convert object to JSON"() {
        given:
        def jsonSlurper = new JsonSlurper()
        def testObject = new TestObjectClass()

        when:
        def jsonString = JsonUtils.writeValueAsString(testObject)

        then:
        jsonSlurper.parseText(jsonString) == [name: "Some Name", password: "test1234"]
    }

    def "Read value to list"() {
        given:
        def data = Arrays.asList(new TestObjectClass(name: "John"), new TestObjectClass(name: "Alex"))

        def jsonString = JsonUtils.writeValueAsString(data)

        when:
        List<TestObjectClass> restoredData = JsonUtils.readValue(jsonString, new TypeReference<List<TestObjectClass>>() {})

        then:
        restoredData[0].name == "John"
        restoredData[1].name == "Alex"
        restoredData[0].password == "test1234"
        restoredData[1].password == "test1234"
    }

    def "Convert object to JSON by ignoring password field"() {
        given:
        def jsonSlurper = new JsonSlurper()
        def testObject = new TestObjectClassWithIgnore()

        when:
        def jsonString = JsonUtils.writeValueAsString(testObject)

        then:
        jsonSlurper.parseText(jsonString) == [name: "Some Name"]
    }

    def "is json valid"() {
        expect:
        !isJsonValid("")
        !isJsonValid("a")
        !isJsonValid("{")
        !isJsonValid("{1}")
        isJsonValid("{}")
        isJsonValid("{\"a\": 1}")
        isJsonValid("{\"a\": []}")
        isJsonValid("{\"a\": {}}")
        isJsonValid("{\"a\": {\"b\": []}}")
    }

}
