package fintech.ekomi.config;

import okhttp3.Response;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Profile("itest")
@Import({EKomiConfig.class})
@EnableCaching
@Configuration
public class TestEKomiConfig {

    @Bean
    public TestInterceptor testRetrofitInterceptor() {
        return new TestInterceptor() {

            private Response response;
            private Chain chain;
            private int interceptedCount = 0;

            @Override
            public Response intercept(Chain chain) throws IOException {
                this.interceptedCount++;
                this.chain = chain;
                return response;
            }

            public Chain getChain() {
                return chain;
            }

            public void withExpectedResponse(Response response) {
                this.response = response;
            }

            @Override
            public int getInterceptedCount() {
                return interceptedCount;
            }

            @Override
            public void resetInterceptedCount() {
                this.interceptedCount = 0;
            }
        };
    }

    @Bean
    public EkomiApiProperties ekomiApiProperties() {
        return new EkomiApiProperties()
            .setType("json")
            .setVersion("111")
            .setUrl("http://api.ekomi.de/v3/")
            .setKey("333333333")
            .setId("22");
    }
}
