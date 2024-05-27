package fintech.spain.callcenter

import fintech.JsonUtils
import fintech.presence.db.OutboundLoadRecordRepository
import fintech.presence.impl.MockPresenceAdministratorProviderBean
import fintech.presence.settings.PresenceSettings
import fintech.settings.SettingsService
import fintech.settings.commands.UpdatePropertyCommand
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

    def presenceServiceId = 1
    def presenceLoadId = 200

    def setup() {
        mockProviderBean.clearRecords()
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        testDatabase.cleanDb()
        initPresenceSettings()
    }

    void initPresenceSettings() {
        def settings = new PresenceSettings()
        settings.setServiceId(presenceServiceId)
        settings.setLoadId(presenceLoadId)
        settingsService.initProperty(PRESENCE_SETTINGS, JsonUtils.writeValueAsString(settings), "", { true })
    }

    void updatePresenceSettings(Integer serviceId, Integer loadId) {
        PresenceSettings settings = settingsService.getJson(PRESENCE_SETTINGS, PresenceSettings.class)
        settings.setServiceId(serviceId)
        settings.setLoadId(loadId)
        settingsService.update(new UpdatePropertyCommand(name: PRESENCE_SETTINGS, textValue: JsonUtils.writeValueAsString(settings)))
    }

}
