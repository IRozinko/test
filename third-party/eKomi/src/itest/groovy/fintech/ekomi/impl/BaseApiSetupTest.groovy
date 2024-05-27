package fintech.ekomi.impl

import com.google.gson.Gson
import fintech.api.TestResponseBody
import fintech.ekomi.EKomiService
import fintech.ekomi.config.EKomiConfig
import fintech.ekomi.config.EkomiApiProperties
import fintech.ekomi.config.TestEKomiConfig
import fintech.ekomi.config.TestInterceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ActiveProfiles("itest")
@ContextConfiguration(classes = TestEKomiConfig.class)
abstract class BaseApiSetupTest extends Specification {

    @Autowired
    EkomiApiProperties ekomiApi

    Gson gson = new Gson()

    @Autowired
    EKomiService eKomiService

    @Autowired
    TestInterceptor testInterceptor
    @Autowired
    CacheManager cacheManager

    protected <T> Response response(T t, String url) {
        return new okhttp3.Response.Builder()
            .code(200).message("OK")
            .body(new TestResponseBody(gson.toJson(t), "application/json"))
            .protocol(Protocol.HTTP_1_1)
            .request(request(url))
            .build()
    }

    protected void clearCache() {
        cacheManager.getCache(EKomiConfig.EKOMI_SNAPSHOT_CACHE_NAME).clear()
    }

    protected Request request(String url) {
        return new Request.Builder()
            .url(url)
            .get()
            .build()
    }
}
