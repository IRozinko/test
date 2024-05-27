package fintech.bo.api

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.settings.SettingsService
import fintech.settings.commands.UpdatePropertyCommand
import fintech.spain.alfa.product.CrmAlfaSetup
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractAlfaBoApiTest extends AbstractBaseSpecification {

    @Autowired
    CrmAlfaSetup alfaSetup

    @Autowired
    SettingsService settingsService

    def setup() {
        testDatabase.cleanDb([])
        alfaSetup.setUp()
        TimeMachine.useDefaultClock()
    }

    def cleanup() {
        TimeMachine.useDefaultClock()
    }

    void saveJsonSettings(String name, Object settings) {
        settingsService.update(new UpdatePropertyCommand(name: name, textValue: JsonUtils.writeValueAsString(settings)))
    }
}
