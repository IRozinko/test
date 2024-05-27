package fintech.geoip;

import java.util.Optional;

public interface GeoIpService {

    Optional<String> getCountryCode(String ip);
}
