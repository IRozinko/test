package fintech.presence;

import fintech.JsonUtils;
import fintech.Validate;
import fintech.presence.settings.PresenceSettings;
import fintech.settings.SettingsService;
import org.springframework.stereotype.Component;

@Component
public class PresenceSetup {

    private final SettingsService settings;

    public PresenceSetup(SettingsService settings) {
        this.settings = settings;
    }

    public void setup() {
        settings.initProperty(PresenceSettings.PRESENCE_SETTINGS, defaultCallCenterSettings(), "Settings for call center integration", v -> Validate.isTrue(v != null));
    }

    private String defaultCallCenterSettings() {
        PresenceSettings settings = new PresenceSettings();
        settings.setServiceId(299);
        return JsonUtils.writeValueAsString(settings);
    }
}
