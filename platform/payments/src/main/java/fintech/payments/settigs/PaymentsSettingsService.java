package fintech.payments.settigs;

import fintech.JsonUtils;
import fintech.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentsSettingsService {

    private final SettingsService settingsService;

    public static final String PAYMENT_SETTINGS = "PaymentsSettings";

    public void setup() {
        settingsService.initProperty(PAYMENT_SETTINGS, JsonUtils.writeValueAsString(defaultPaymentSettings()), "Payment settings", val -> {
        });
    }

    private PaymentsSettings defaultPaymentSettings() {
        return new PaymentsSettings()
            .setUnnaxEnabled(false)
            .setAutoExportEnabled(false);
    }
}
