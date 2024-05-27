package fintech.spain.web.common

import fintech.ClasspathUtils
import fintech.cms.spi.CmsItem
import fintech.cms.spi.CmsItemType

class ValidationExceptionsTest extends BaseSpecification {

    def "validation exceptions check"() {
        given:
        CmsItem apiLocalization = new CmsItem()
        apiLocalization.setItemType(CmsItemType.TRANSLATION)
        apiLocalization.setKey("localization")
        apiLocalization.setLocale("es")
        apiLocalization.setScope("")
        apiLocalization.setDescription("error messages")
        apiLocalization.setContentTemplate(ClasspathUtils.resourceToString("api_localization.json"))

        when:
        cmsRegistry.saveItem(apiLocalization, true)

        then:
        def e = validationExceptions.invalidValue("token")
        with(e.error.fieldErrors["token"]) {
            assert code == 'InvalidValue'
            assert message == 'Enlace ha caducado'
        }
    }

}
