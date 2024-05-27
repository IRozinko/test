package fintech.dc;

import fintech.dc.model.DcSettings;

public interface DcSettingsService {
    DcSettings getSettings();

    void saveSettings(DcSettings settings, boolean overwrite);
}
