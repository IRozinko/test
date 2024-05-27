package fintech.geoip.free;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import fintech.geoip.spi.GeoIpProvider;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component(FreeGeoIpProvider.NAME)
class FreeGeoIpProvider implements GeoIpProvider {

    public static final String NAME = "free-geoip-provider";

    private final OkHttpClient client;
    private final FreeGeoIpConfig config;
    private final Gson gson;

    public FreeGeoIpProvider(FreeGeoIpConfig config) {
        this.config = config;

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(config.isDebug() ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);

        this.client = new OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();

        gson = new Gson();
    }

    @Override
    public Optional<String> getCountryCode(String ip) {
        Preconditions.checkNotNull(ip, "IP shouldn't be null");
        Preconditions.checkState(ip.length() > 0, "IP shouldn't be empty");

        String countryCode = callGeoIpService(config.getFreeGeoIp(), ip, FreeGeoIpResponse.class)
            .map(FreeGeoIpResponse::getCountryCode)
            .orElseGet(() -> callGeoIpService(config.getNekudoGeoIp(), ip, GeoIpNekudoResponse.class)
                .map(GeoIpNekudoResponse::getCountryCode)
                .orElse(null));

        return Optional.ofNullable(countryCode);

    }

    private <T> Optional<T> callGeoIpService(String serviceUrl, String ip, Class<T> clazz) {
        Request r = new Request.Builder().url(serviceUrl + ip).build();
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Response response = client.newCall(r).execute();

            T responseObject = gson.fromJson(response.body().charStream(), clazz);
            return Optional.ofNullable(responseObject);
        } catch (Exception e) {
            log.error("Error in " + serviceUrl, e);
        } finally {
            log.info("Completed FreeGeoIp request: {} in {} ms", r.url().toString(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        return Optional.empty();
    }
}
