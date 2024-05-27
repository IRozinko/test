package fintech.presence

import fintech.JsonUtils
import fintech.presence.db.OutboundLoadRecordRepository
import fintech.presence.impl.MockPresenceAdministratorProviderBean
import fintech.presence.settings.PresenceSettings
import fintech.settings.SettingsService
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

import static fintech.presence.settings.PresenceSettings.PRESENCE_SETTINGS

class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    SettingsService settingsService

    @Autowired
    OutboundLoadRecordRepository outboundLoadRecordRepository

    @Autowired
    MockPresenceAdministratorProviderBean mockProviderBean

    def setup() {
        mockProviderBean.clearRecords()
        testDatabase.cleanDb()
        outboundLoadRecordRepository.restartSourceIdSequence()
    }

    void setupSettings(Integer serviceId, Integer loadId) {
        def settings = new PresenceSettings()
        settings.setServiceId(serviceId)
        settings.setLoadId(loadId)
        settingsService.initProperty(PRESENCE_SETTINGS, JsonUtils.writeValueAsString(settings), "", { true })
    }
}
