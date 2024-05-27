package fintech.spain.alfa.product

import fintech.risk.checklist.CheckListConstants
import fintech.risk.checklist.commands.AddCheckListEntryCommand
import fintech.spain.alfa.product.web.WebLoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

class WebLoginServiceTest extends AbstractAlfaTest {

    @Autowired
    private WebLoginService webLoginService

    def "Verify email not blacklisted"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()

        then:
        webLoginService.login(client.email, "test1234")

        when:
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_EMAIL, value1: client.email))

        and:
        webLoginService.login(client.email, "test1234")

        then:
        thrown AccessDeniedException.class
    }

    def "Verify dni not blacklisted"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()

        then:
        webLoginService.login(client.email, "test1234")

        when:
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_DNI, value1: client.dni))

        and:
        webLoginService.login(client.email, "test1234")

        then:
        thrown AccessDeniedException.class
    }
}
