package fintech.spain.alfa.web

import fintech.email.db.EmailLogRepository
import fintech.email.db.Entities
import fintech.spain.web.common.ApiError
import fintech.web.api.models.OkResponse
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class ContactMeApiTest extends AbstractAlfaApiTest {

    @Autowired
    private EmailLogRepository emailLogRepository

    def "Test empty provided fields not pass data validation for contact me form"() {
        given:
        def request = new fintech.spain.alfa.web.models.ContactMeRequest()

        when:
        def result = restTemplate.postForEntity("/api/public/web/contactme", request, ApiError.class)

        then:
        assert result.statusCodeValue == 400
        result.body.fieldErrors.get("phone").code == "NotEmpty"
        result.body.fieldErrors.get("email").code == "NotEmpty"
        result.body.fieldErrors.get("name").code == "NotEmpty"
        result.body.fieldErrors.get("comment").code == "NotEmpty"
    }

    @Unroll
    def "Test invalid email not pass data validation for contact me form"() {
        given:
        def request = contactMeRequest().setEmail(wrongEmail)

        when:
        def result = restTemplate.postForEntity("/api/public/web/contactme", request, ApiError.class)

        then:
        assert result.statusCodeValue == 400
        result.body.fieldErrors.get("email").getMessage() == "Correo electrónico invalido"

        where:
        wrongEmail << ["abc", "abc@a", "abc@abc", "abc@abc@abc", ".com", "abc.com", "abc@.com"]
    }

    def "Test submitting contact me form send expected notification"() {
        given:
        def request = contactMeRequest()

        when:
        def result = restTemplate.postForEntity("/api/public/web/contactme", request, OkResponse.class)
        def emailLog = emailLogRepository.getOptional(Entities.emailLog.from.eq(request.email))

        then:
        assert result.statusCodeValue == 200
        assert emailLog.isPresent()
        emailLog.get().getTo() == "info@alfa.es"
        emailLog.get().getBody() == expectedEmailBody(request)
    }

    def expectedEmailBody(def request) {
        return "<html>\n" +
            "<body>\n" +
            "Nombre: ${request.name}<br>\n" +
            "Teléfono móvil: ${request.phone}<br>\n" +
            "E-mail: ${request.email}<br>\n" +
            "IP: 127.0.0.1<br>\n" +
            "Comentarios:<br>\n" +
            "${request.comment}<br>\n" +
            "</body>\n" +
            "</html>\n"
    }


    def contactMeRequest() {
        return new fintech.spain.alfa.web.models.ContactMeRequest(
            name: "TestName",
            email: "email@mail.com",
            phone: "3333333333",
            comment: "Some text"
        )
    }
}
