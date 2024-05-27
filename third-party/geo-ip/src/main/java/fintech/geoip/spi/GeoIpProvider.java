package fintech.geoip.spi;

import java.util.Optional;

public interface GeoIpProvider {
    Optional<String> getCountryCode(String ip);
}
