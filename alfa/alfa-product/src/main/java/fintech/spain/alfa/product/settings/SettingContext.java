package fintech.spain.alfa.product.settings;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static fintech.spain.alfa.product.settings.AlfaSettings.*;


public final class SettingContext {

    private static final Map<String, String> DEFAULT_CONTEXT = ImmutableMap.<String, String>builder()
        .put(ACTIVITY_SETTINGS, ACTIVITY_SETTINGS)
        .put(NOTIFICATION_SETTINGS, NOTIFICATION_SETTINGS)
        .put(COMPANY_CONTACT_DETAILS, COMPANY_CONTACT_DETAILS)
        .put(DISCOUNT_SETTINGS, DISCOUNT_SETTINGS)
        .put(WEALTHINESS_CALCULATION_SETTINGS, WEALTHINESS_CALCULATION_SETTINGS)
        .put(LENDING_RULES_BASIC, LENDING_RULES_BASIC)
        .build();

    private static final Map<String, String> LINE_OF_CREDIT_CONTEXT = ImmutableMap.<String, String>builder()
        .put(ACTIVITY_SETTINGS, ACTIVITY_SETTINGS)
        .put(NOTIFICATION_SETTINGS, NOTIFICATION_SETTINGS)
        .put(COMPANY_CONTACT_DETAILS, COMPANY_CONTACT_DETAILS)
        .put(DISCOUNT_SETTINGS, DISCOUNT_SETTINGS)
        .put(WEALTHINESS_CALCULATION_SETTINGS, WEALTHINESS_CALCULATION_SETTINGS)
        .put(LENDING_RULES_BASIC, LENDING_RULES_BASIC)
        .build();


}
