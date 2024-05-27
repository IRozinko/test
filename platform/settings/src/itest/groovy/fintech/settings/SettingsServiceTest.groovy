package fintech.settings

import com.fasterxml.jackson.core.type.TypeReference
import fintech.JsonUtils
import fintech.Validate
import fintech.settings.commands.UpdatePropertyCommand
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate
import java.time.LocalDateTime

class SettingsServiceTest extends AbstractBaseSpecification {

    @Autowired
    SettingsService settingsService

    def setup() {
        testDatabase.cleanDb()
    }

    def "Should set Decimal Property"() {
        when:
        settingsService.initProperty("test", 0.10g, "test property", {})

        then:
        BigDecimal value = settingsService.getDecimal("test")
        value == 0.10g
    }

    def "Should set Boolean Property"() {
        when:
        settingsService.initProperty("boolean_prop", true, "test property", {})
        then:
        boolean value = settingsService.getBoolean("boolean_prop")
        assert value
    }

    def "Should set Date Property"() {
        when:
        settingsService.initProperty("date_prop", LocalDate.of(2011, 12, 21), "test property", {})

        then:
        LocalDate value = settingsService.getDate("date_prop")
        value == LocalDate.of(2011, 12, 21)
    }


    def "Should set DateTime Property"() {
        when:
        settingsService.initProperty("date_time_prop", LocalDateTime.of(2011, 12, 21, 23, 59), "test property", {})

        then:
        LocalDateTime value = settingsService.getDateTime("date_time_prop")
        value == LocalDateTime.of(2011, 12, 21, 23, 59)
    }

    def "Should set Number Property"() {
        when:
        settingsService.initProperty("number_prop", 77, "test property", {})

        then:
        Long value = settingsService.getNumber("number_prop")
        value == 77L
    }

    def "Should set Text Property"() {
        when:
        settingsService.initProperty("text_prop", "here is text", "test property", {})

        then:
        String value = settingsService.getString("text_prop")
        value == "here is text"
    }


    def "Should return empty result if property not set"() {
        when:
        settingsService.getNumber("not_Existing_prop")

        then:
        thrown IllegalArgumentException
    }

    def "Should throw exception if property name is not provided"() {
        when:
        settingsService.getNumber("")

        then:
        thrown IllegalArgumentException
    }

    def "Should not override property"() {
        given:
        settingsService.initProperty("text_prop", "here is text", "test property", {})

        when:
        settingsService.initProperty("text_prop", "new text", "test property", {})

        then:
        String value = settingsService.getString("text_prop")
        value == "here is text"
    }

    def "Should not override property type"() {
        given:
        settingsService.initProperty("error", 404L, "test property", {})

        when:
        settingsService.initProperty("error", "Not Found", "test property", {})

        then:
        settingsService.getNumber("error") == 404L
    }

    def "should unset property"() {
        given:
        settingsService.initProperty("error", 404L, "test property", {})

        def propertyToRemove = settingsService.getNumber("error")
        assert propertyToRemove == 404L

        when:
        settingsService.removeProperty("error")
        settingsService.getNumber("error")

        then:
        thrown IllegalArgumentException
    }

    def "should list all properties"() {
        given:
        settingsService.initProperty("a1", 400, "test property", {})
        settingsService.initProperty("a2", "40!", "test property", {})
        settingsService.initProperty("a3", LocalDate.now(), "test property", {})
        settingsService.initProperty("a4", false, "test property", {})

        when:
        def allProperties = settingsService.listAll()

        then:
        allProperties.size() == 4
    }

    def "update property"() {
        given:
        settingsService.initProperty("textProp", "hello", "test property", {})

        when:
        settingsService.update(new UpdatePropertyCommand(name: "textProp", textValue: "world"))

        then:
        def value = settingsService.getString("textProp")

        assert value == "world"
    }

    def "cant update property with different type"() {
        given:
        settingsService.initProperty("textProp", "hello", "test property", {})

        when:
        settingsService.update(new UpdatePropertyCommand(name: "textProp", decimalValue: 10.00g))

        then:
        thrown NullPointerException
    }


    def "validate property"() {
        when:
        settingsService.initProperty("validated property", 0L, "", { val -> Validate.isTrue(val > 0)})

        then:
        thrown(IllegalArgumentException.class)

        when:
        settingsService.initProperty("validated property", 1L, "", { val -> Validate.isTrue(val > 0)})
        settingsService.update(new UpdatePropertyCommand(name: "validated property", numberValue: 0L))

        then:
        thrown(IllegalArgumentException.class)

        and:
        settingsService.getNumber("validated property") == 1L
    }

    def "Should save and read json with special characters in keys"() {
        when:
        settingsService.initProperty("json_with_atypical_key_names", '{";key1": "value1", "1.key.2": "value2"}', "json with special characters keys", {});

        then:
        def json = settingsService.getString("json_with_atypical_key_names")
        json == '{";key1": "value1", "1.key.2": "value2"}'
        def parsedMap = JsonUtils.readValue(json, new TypeReference<Map<String, String>>() {})
        parsedMap.size() == 2
        parsedMap[';key1'] == 'value1'
        parsedMap['1.key.2'] == 'value2'
    }
}
