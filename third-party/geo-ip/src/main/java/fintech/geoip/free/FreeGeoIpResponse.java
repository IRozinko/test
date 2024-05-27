package fintech.geoip.free;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * Response object for http://freegeoip.net/json/
 */
@Data
class FreeGeoIpResponse {

    @SerializedName("country_code")
    String countryCode;

    @SerializedName("country_name")
    String countryName;

}
