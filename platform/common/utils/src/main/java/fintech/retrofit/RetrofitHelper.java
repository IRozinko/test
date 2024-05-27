package fintech.retrofit;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.apache.commons.lang3.tuple.Pair;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class RetrofitHelper {

    public static <T> Optional<T> syncCall(Call<T> call) {
        try {
            Response<T> response = call.execute();
            validateResponse(response);
            return Optional.ofNullable(response.body());
        } catch (IOException e) {
            log.warn("Requested failed", e);
            return Optional.empty();
        }
    }

    public static <T> Optional<Pair<Headers, T>> syncCallWithHeaders(Call<T> call) {
        try {
            Response<T> response = call.execute();
            validateResponse(response);
            return Optional.of(Pair.of(response.headers(), response.body()));
        } catch (IOException e) {
            log.warn("Requested failed", e);
            return Optional.empty();
        }
    }

    private static <T> void validateResponse(Response<T> response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException(response.errorBody().string());
        }
    }
}
