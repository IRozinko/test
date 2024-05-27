package fintech.spain.alfa.product.crm

import fintech.risk.checklist.CheckListService
import fintech.risk.checklist.model.CheckListQuery
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.crm.impl.AlfaClientBlacklistService
import org.springframework.beans.factory.annotation.Autowired

import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_DNI
import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_EMAIL
import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_PHONE

class AlfaClientBlacklistServiceTest extends AbstractAlfaTest {

    @Autowired
    private AlfaClientBlacklistService alfaClientBlacklistService

    @Autowired
    private CheckListService checkListService

    def "blacklist client"() {
        given:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()

        when:
        alfaClientBlacklistService.blacklistClient(client.clientId, "comment")

        then:
        with(checkListService.find(new CheckListQuery(CHECKLIST_TYPE_EMAIL, client.getEmail()))) {
            it.size() == 1
            it[0].comment == 'comment'
            it[0].value1 == client.getEmail().toUpperCase()
        }

        with(checkListService.find(new CheckListQuery(CHECKLIST_TYPE_PHONE, client.getMobilePhone()))) {
            it.size() == 1
            it[0].comment == 'comment'
            it[0].value1 == client.getMobilePhone().toUpperCase()
        }

        with(checkListService.find(new CheckListQuery(CHECKLIST_TYPE_DNI, client.dni))) {
            it.size() == 1
            it[0].comment == 'comment'
            it[0].value1 == client.getDni().toUpperCase()
        }

        when:
        alfaClientBlacklistService.unblacklistClient(client.clientId)

        then:
        checkListService.find(new CheckListQuery(CHECKLIST_TYPE_EMAIL, client.getEmail())).isEmpty()
        checkListService.find(new CheckListQuery(CHECKLIST_TYPE_PHONE, client.getMobilePhone())).isEmpty()
        checkListService.find(new CheckListQuery(CHECKLIST_TYPE_DNI, client.getDni())).isEmpty()
    }
}
