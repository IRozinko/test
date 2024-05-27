package fintech.geoip.free;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * Response object for http://geoip.nekudo.com/api/
 */
@Data
class GeoIpNekudoResponse {

    @SerializedName("country")
    private Country country;

    String getCountryCode() {
        if (country != null) {
            return country.countryCode;
        }
        return null;
    }

    private static class Country {

        @SerializedName("code")
        String countryCode;

        @SerializedName("name")
        String countryName;
    }
}
