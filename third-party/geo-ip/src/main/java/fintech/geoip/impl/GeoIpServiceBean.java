package fintech.geoip.impl;

import fintech.geoip.GeoIpService;
import fintech.geoip.spi.GeoIpProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class GeoIpServiceBean implements GeoIpService {

    @Resource(name = "${geoip.provider:" + MockGeoIpProvider.NAME + "}")
    private GeoIpProvider provider;

    @Override
    public Optional<String> getCountryCode(String ip) {
        return provider.getCountryCode(ip);
    }
}
