package fintech.geoip.impl;

import com.google.common.base.Preconditions;
import fintech.geoip.spi.GeoIpProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component(MockGeoIpProvider.NAME)
public class MockGeoIpProvider implements GeoIpProvider {

    public static final String NAME = "mock-geoip-provider";

    private Map<String, String> ipCountryMap = new HashMap<>();

    @Override
    public Optional<String> getCountryCode(String ip) {
        Preconditions.checkNotNull(ip, "IP shouldn't be null");
        Preconditions.checkState(ip.length() > 0, "IP shouldn't be empty");

        if (ipCountryMap.containsKey(ip)) {
            return Optional.of(ipCountryMap.get(ip));
        }

        return Optional.of("N/A");
    }


}
