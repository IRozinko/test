package fintech.presence.impl;

import fintech.Validate;
import fintech.presence.PhoneRecord;
import fintech.presence.PresenceAdministratorProvider;
import fintech.presence.PresenceException;
import fintech.presence.PresenceUnauthorizedException;
import fintech.presence.model.GetOutboundLoadRecordInfoResponse;
import fintech.presence.model.GetOutboundLoadRecordsResponse;
import fintech.presence.model.GetOutboundLoadsResponse;
import fintech.presence.model.GetOutboundServiceInfoResponse;
import fintech.presence.model.GetPhoneDescriptionsResponse;
import fintech.presence.model.GetTokenResponse;
import fintech.presence.model.LoginRequest;
import fintech.presence.model.PhoneRecordsWrapper;
import fintech.presence.model.PostOutboundRecordRequest;
import fintech.presence.model.PresenceResponse;
import fintech.presence.model.RemoveRecordFromOutboundLoadResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static fintech.presence.PresenceJsonUtils.getObjectMapper;

@Slf4j
@Component
@ConditionalOnProperty(name = "presence.administrator.provider", havingValue = "presence")
public class PresenceAdministratorProviderBean implements PresenceAdministratorProvider {

    private static final int DEFAULT_RECORD_PRIORITY = 100;
    private static final String PRESENCE_TOKEN_CACHE_NAME = "presence_token";

    private final URI baseUri;
    private final String basePath;
    private final String username;
    private final String password;
    private final CacheManager cacheManagerToken;

    public PresenceAdministratorProviderBean(@Value("${presence.url:http://mock}") URI baseUri,
                                             @Value("${presence.administrator.path:/administratorrest}") String basePath,
                                             @Value("${presence.administrator.username:presenceapi}") String username,
                                             @Value("${presence.administrator.password:presenceapi}") String password,
                                             @Qualifier("presenceCacheManagerToken") CacheManager cacheManagerToken) {
        this.baseUri = baseUri;
        this.basePath = basePath;
        this.username = username;
        this.password = password;
        this.cacheManagerToken = cacheManagerToken;
    }

    @Override
    @Cacheable(value = PRESENCE_TOKEN_CACHE_NAME, cacheManager = "presenceCacheManagerToken", sync = true, key = "#root.methodName")
    public GetTokenResponse getToken() throws PresenceException, IOException {
        HttpHost targetHost = new HttpHost(baseUri.getHost(), baseUri.getPort(), baseUri.getScheme());

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        provider.setCredentials(AuthScope.ANY, credentials);

        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(provider);
        context.setAuthCache(authCache);

        URI uri = baseUri.resolve(basePath + "/api/v1/token");
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(uri);
            try (CloseableHttpResponse response = client.execute(request, context)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    GetTokenResponse tokenResponse = getObjectMapper().readValue(response.getEntity().getContent(), GetTokenResponse.class);
                    log.debug("Retrieved Presence token for user {}: {}", username, tokenResponse);
                    return tokenResponse;
                }
                handleErrors(request, response);
                return null;
            }
        }
    }

    @Override
    public void login(String token, String user, String password) throws IOException, PresenceException {
        Validate.notBlank(token, "Presence token not valid");

        URI uri = baseUri.resolve(basePath + "/api/v1/sessions/login");
        LoginRequest loginRequest = new LoginRequest(user, password);
        HttpPost request = new HttpPost(uri);
        request.setEntity(new StringEntity(getObjectMapper().writeValueAsString(loginRequest), ContentType.APPLICATION_JSON));
        executeRequest(token, request, HttpStatus.SC_CREATED, null);
    }

    @Override
    public void logout(String token) throws IOException, PresenceException {
        Validate.notBlank(token, "Presence token not valid");
        URI uri = baseUri.resolve(basePath + "/api/v1/sessions/logout");
        HttpPost request = new HttpPost(uri);
        executeRequest(token, request, HttpStatus.SC_NO_CONTENT, null);
    }

    @Override
    @Cacheable(value = "presence_outbound_loads", cacheManager = "presenceCacheManager30Mins", sync = true, key = "#serviceId")
    public GetOutboundLoadsResponse getOutboundLoads(String token, Integer serviceId) throws IOException, PresenceException {
        Validate.notNull(serviceId, "ServiceId not valid");

        URI uri = baseUri.resolve(basePath + "/api/v1/services/outbound/" + serviceId + "/loads");
        HttpGet request = new HttpGet(uri);
        GetOutboundLoadsResponse outboundServices = executeRequest(token, request, HttpStatus.SC_OK, GetOutboundLoadsResponse.class);
        log.debug("Retrieved list of outbound loads for service {}: {}", serviceId, outboundServices);
        return outboundServices;
    }

    @Override
    public GetOutboundLoadRecordsResponse getOutboundLoadRecords(String token, Integer serviceId, Integer loadId) throws IOException, PresenceException {
        Validate.notNull(serviceId, "ServiceId not valid");
        Validate.notNull(loadId, "LoadId not valid");

        URI uri = baseUri.resolve(basePath + "/api/v1/services/outbound/" + serviceId + "/loads/" + loadId + "/records");
        HttpGet request = new HttpGet(uri);
        GetOutboundLoadRecordsResponse outboundLoadRecords = executeRequest(token, request, HttpStatus.SC_OK, GetOutboundLoadRecordsResponse.class);
        log.debug("Retrieved records of outbound load {} for service {}: {}", loadId, serviceId, outboundLoadRecords);
        return outboundLoadRecords;
    }

    @Override
    @Cacheable(value = "presence_outbound_services_info", cacheManager = "presenceCacheManager30Mins", sync = true, key = "#serviceId")
    public GetOutboundServiceInfoResponse getOutboundServiceInfo(String token, Integer serviceId) throws IOException, PresenceException {
        Validate.notNull(serviceId, "ServiceId not valid");

        URI uri = baseUri.resolve(basePath + "/api/v2/services/outbound/" + serviceId);
        HttpGet request = new HttpGet(uri);
        GetOutboundServiceInfoResponse outboundServiceInfoResponse = executeRequest(token, request, HttpStatus.SC_OK, GetOutboundServiceInfoResponse.class);
        log.debug("Retrieved info about outbound service {}: {}", serviceId, outboundServiceInfoResponse);
        return outboundServiceInfoResponse;
    }

    @Override
    public GetOutboundLoadRecordInfoResponse getOutboundLoadRecordInfo(String token, Integer serviceId, Integer loadId, Integer sourceId) throws IOException, PresenceException {
        Validate.notNull(serviceId, "ServiceId not valid");
        Validate.notNull(loadId, "LoadId not valid");
        Validate.notNull(sourceId, "SourceId not valid");

        URI uri = baseUri.resolve(basePath + "/api/v3/services/outbound/" + serviceId + "/loads/" + loadId + "/records/" + sourceId);
        HttpGet request = new HttpGet(uri);
        GetOutboundLoadRecordInfoResponse outboundLoadRecordInfo = executeRequest(token, request, HttpStatus.SC_OK, GetOutboundLoadRecordInfoResponse.class);
        log.debug("Retrieved info about outbound record with source id {} service {} load {}: {}", sourceId, serviceId, loadId, outboundLoadRecordInfo);
        return outboundLoadRecordInfo;
    }

    @Override
    public void addRecordToOutboundLoad(String token, Integer serviceId, Integer loadId, Integer sourceId, String name, String customerId, List<PhoneRecord> phoneRecords) throws IOException, PresenceException {
        Validate.notNull(serviceId, "ServiceId not valid");
        Validate.notNull(loadId, "LoadId not valid");
        Validate.notNull(sourceId, "SourceId not valid");
        Validate.notBlank(name, "Name must not be empty");
        Validate.inclusiveBetween(1, 10, phoneRecords.size());

        URI uri = baseUri.resolve(basePath + "/api/v3/services/outbound/" + serviceId + "/loads/" + loadId + "/records");
        PostOutboundRecordRequest outboundRecordRequest = new PostOutboundRecordRequest();
        outboundRecordRequest
            .setName(name)
            .setSourceId(sourceId)
            .setScheduled(false)
            .setPriority(DEFAULT_RECORD_PRIORITY)
            .setCustomerId(customerId)
            .setPhoneRecordsWrapper(new PhoneRecordsWrapper(phoneRecords));

        HttpPost request = new HttpPost(uri);
        request.setEntity(new StringEntity(getObjectMapper().writeValueAsString(outboundRecordRequest), ContentType.APPLICATION_JSON));
        executeRequest(token, request, HttpStatus.SC_CREATED, null);
    }

    @Override
    public RemoveRecordFromOutboundLoadResponse removeRecordFromOutboundLoad(String token, Integer serviceId, Integer loadId, Integer sourceId) throws IOException, PresenceException {
        Validate.notNull(serviceId, "ServiceId not valid");
        Validate.notNull(loadId, "LoadId not valid");
        Validate.notNull(sourceId, "SourceId not valid");

        URI uri = baseUri.resolve(basePath + "/api/v1/services/outbound/" + serviceId + "/loads/" + loadId + "/records/" + sourceId + "/unload");
        HttpPut request = new HttpPut(uri);
        RemoveRecordFromOutboundLoadResponse removeRecordFromOutboundLoadResponse = executeRequest(token, request, HttpStatus.SC_OK, RemoveRecordFromOutboundLoadResponse.class);
        log.debug("Removed record {} from outbound load {} for service {}: {}", sourceId, loadId, serviceId, removeRecordFromOutboundLoadResponse);
        return removeRecordFromOutboundLoadResponse;
    }

    @Override
    @Cacheable(value = "presence_phone_descriptions", cacheManager = "presenceCacheManager12Hours", sync = true, key = "#root.methodName")
    public GetPhoneDescriptionsResponse getPhoneDescriptions(String token) throws IOException, PresenceException {
        URI uri = baseUri.resolve(basePath + "/api/v1/system/phonedescriptions");
        HttpGet request = new HttpGet(uri);
        GetPhoneDescriptionsResponse phoneDescriptions = executeRequest(token, request, HttpStatus.SC_OK, GetPhoneDescriptionsResponse.class);
        log.debug("Retrieved list of phone descriptions: {}", phoneDescriptions);
        return phoneDescriptions;
    }

    private <T extends PresenceResponse> T executeRequest(String token, HttpRequestBase request, int expectedStatusCode, Class<T> expectedResultClass) throws IOException, PresenceException {
        Validate.notBlank(token, "Presence token not valid");
        Validate.notNull(request, "Presence request not valid");

        request.addHeader("Authorization", "Bearer " + token);
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getStatusLine().getStatusCode() == expectedStatusCode) {
                    if (expectedResultClass == null) {
                        return null;
                    }
                    return getObjectMapper().readValue(response.getEntity().getContent(), expectedResultClass);
                }
                handleErrors(request, response);
                return null;
            }
        }
    }

    private void handleErrors(HttpRequestBase request, CloseableHttpResponse response) throws PresenceException {
        log.debug("Handling error from Presence with request {} and response {}", request, response);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            cacheManagerToken.getCache(PRESENCE_TOKEN_CACHE_NAME).clear();
            throw new PresenceUnauthorizedException("User " + username + " not authorized");
        }
        try {
            if (response.getEntity() != null) {
                PresenceResponse apiResponse = getObjectMapper().readValue(response.getEntity().getContent(), PresenceResponse.class);
                throw new PresenceException("Error executing " + request.getRequestLine().getUri() + " for user " + username + ": " + apiResponse.getErrorMessage(), apiResponse.getCode(), apiResponse.getErrorMessage());
            }
        } catch (IOException e) {
            log.error("Error retrieving response from Presence", e);
            throw new PresenceException("Error executing " + request.getRequestLine().getUri() + " for user " + username, e);
        }
        throw new PresenceException("Error executing " + request.getRequestLine().getUri() + " for user " + username + ": " + response.getStatusLine().getStatusCode());
    }
}
