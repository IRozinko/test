package fintech.geoip.free;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "geoip.config")
class FreeGeoIpConfig {

    /**
     * http://freegeoip.net
     */
    String freeGeoIp = "http://freegeoip.net/json/";

    /**
     * http://geoip.nekudo.com/
     */
    String nekudoGeoIp = "http://geoip.nekudo.com/api/";

    boolean debug;

}
