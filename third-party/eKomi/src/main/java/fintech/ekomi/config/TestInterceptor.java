package fintech.ekomi.config;

import okhttp3.Interceptor;
import okhttp3.Response;

public interface TestInterceptor extends Interceptor {

    Chain getChain();

    void withExpectedResponse(Response response);

    int getInterceptedCount();

    void resetInterceptedCount();
}
