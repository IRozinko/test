package fintech.spain.alfa.product.testing.settings;

import fintech.JsonUtils;
import fintech.Validate;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.settings.LocSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static fintech.BigDecimalUtils.amount;

@Profile("itest")
@Component
public class LocTestSettings {

    @Autowired
    private SettingsService settings;

    public void setUp() {
        settings.removeProperty(LocSettings.LOC_CREDIT_LIMIT_SETTINGS);
        settings.initProperty(LocSettings.LOC_CREDIT_LIMIT_SETTINGS, defaultLocCreditLimitSettings(),
            "Loc Credit limit settings ", v -> Validate.isTrue(v != null &&
                JsonUtils.readValue(v, LocSettings.LocCreditLimitSettings.class).getMaxCreditLimitAllowed() != null));
    }

    private String defaultLocCreditLimitSettings() {
        LocSettings.LocCreditLimitSettings settings = new LocSettings.LocCreditLimitSettings();
        settings.setMinCreditLimitAllowed(amount(1));
        settings.setMaxCreditLimitAllowed(amount(10000));
        settings.setCreditLimitCalculatedCoefficient(amount(1.6));
        return JsonUtils.writeValueAsString(settings);
    }
}
