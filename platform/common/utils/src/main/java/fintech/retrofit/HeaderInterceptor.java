package fintech.retrofit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HeaderInterceptor implements Interceptor {

    private final String name;
    private final String value;


    public HeaderInterceptor(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
            .header(name, value).build();
        return chain.proceed(authenticatedRequest);
    }
}
