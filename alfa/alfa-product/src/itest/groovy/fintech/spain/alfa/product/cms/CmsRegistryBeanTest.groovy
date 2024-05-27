package fintech.spain.alfa.product.cms

import fintech.cms.impl.CmsRegistryBean
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired

class CmsRegistryBeanTest extends AbstractAlfaTest {

    @Autowired
    CmsRegistryBean cmsRegistryBean

    def "Generate context documentation"() {
        when:
        def doc = cmsRegistryBean.getTestingContextDocumentation()

        then:
        noExceptionThrown()
        doc.contains("{{client.number}}")
        doc.contains("{{clientIncomingPayment.amount | numberformat(currencyFormat)}}")
        doc.contains("{{schedule.startDate | ldate(dateFormat)}}")
    }

}
