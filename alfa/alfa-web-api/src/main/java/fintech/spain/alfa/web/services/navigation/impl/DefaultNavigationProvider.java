package fintech.spain.alfa.web.services.navigation.impl;

import fintech.spain.alfa.web.services.navigation.UiState;
import fintech.spain.alfa.web.services.navigation.spi.NavigationProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class DefaultNavigationProvider implements NavigationProvider {

    @Override
    public String getState() {
        return UiState.PROFILE;
    }

    @Override
    public Map<String, Object> getStateData() {
        return Collections.emptyMap();
    }
}
