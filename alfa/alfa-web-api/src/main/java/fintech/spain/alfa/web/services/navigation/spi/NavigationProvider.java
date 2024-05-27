package fintech.spain.alfa.web.services.navigation.spi;

import java.util.Map;

public interface NavigationProvider {

    String getState();

    Map<String, Object> getStateData();
}
