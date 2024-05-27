package fintech.presence.impl;

import com.google.common.collect.ImmutableList;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import fintech.ClasspathUtils;
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
import fintech.presence.model.OutboundLoadRecordStatus;
import fintech.presence.model.PhoneDescription;
import fintech.presence.model.PresenceResponse;
import fintech.presence.model.RemoveRecordFromOutboundLoadResponse;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static fintech.presence.PresenceJsonUtils.getObjectMapper;

@Component
@ConditionalOnProperty(name = "presence.administrator.provider", havingValue = "mock")
public class MockPresenceAdministratorProviderBean implements PresenceAdministratorProvider {

    @SneakyThrows
    @Override
    public GetTokenResponse getToken() {
        return loadResponse("mock/get-token-response.peb", GetTokenResponse.class);
    }

    @Override
    public void login(String token, String user, String password) {
        Validate.notBlank(token, "Presence token not valid");
        loggedIn = true;
    }

    @Override
    public void logout(String token) {
        loggedIn = false;
    }

    @SneakyThrows
    @Override
    public GetOutboundServiceInfoResponse getOutboundServiceInfo(String token, Integer serviceId) {
        commonChecks();
        return loadResponse("mock/get-outbound-service-info-response.peb", GetOutboundServiceInfoResponse.class);
    }

    @SneakyThrows
    @Override
    public GetOutboundLoadRecordInfoResponse getOutboundLoadRecordInfo(String token, Integer serviceId, Integer loadId, Integer sourceId) {
        commonChecks();
        this.sourceId = sourceId;
        return loadResponse("mock/get-outbound-load-record-info-response.peb", GetOutboundLoadRecordInfoResponse.class);
    }

    @SneakyThrows
    @Override
    public GetOutboundLoadsResponse getOutboundLoads(String token, Integer serviceId) {
        commonChecks();
        if (noLoads) {
            return loadResponse("mock/get-empty-response.peb", GetOutboundLoadsResponse.class);
        }
        return loadResponse("mock/get-outbound-loads-response.peb", GetOutboundLoadsResponse.class);
    }

    @SneakyThrows
    @Override
    public GetOutboundLoadRecordsResponse getOutboundLoadRecords(String token, Integer serviceId, Integer loadId) {
        commonChecks();
        if (noRecords) {
            return loadResponse("mock/get-empty-response.peb", GetOutboundLoadRecordsResponse.class);
        }
        GetOutboundLoadRecordsResponse outboundLoadRecordsResponse = loadResponse("mock/get-outbound-load-records-response.peb", GetOutboundLoadRecordsResponse.class);
        outboundLoadRecordsResponse.getData().forEach(outboundLoadRecordData -> {
            OutboundLoadRecordStatus outboundLoadRecordStatus = outboundLoadRecordStatusOverride != null ? outboundLoadRecordStatusOverride : outboundLoadRecordData.getStatus();
            addMockRecord(outboundLoadRecordData.getSourceId(), outboundLoadRecordData.getName(), outboundLoadRecordStatus, new PhoneRecord(outboundLoadRecordData.getPhoneNumber(), PhoneDescription.MOBILE));
        });
        return outboundLoadRecordsResponse;
    }

    @SneakyThrows
    @Override
    public void addRecordToOutboundLoad(String token, Integer serviceId, Integer loadId, Integer sourceId, String name, String customerId, List<PhoneRecord> phoneRecords) {
        commonChecks();
        if (records.containsKey(sourceId)) {
            PresenceResponse response = loadErrorResponse(-33685512, "SRV_ERROR_INSERT_OUTBOUND_RECORD");
            throw new PresenceException("Error", response.getCode(), response.getErrorMessage());
        }
        addMockRecord(sourceId, name, OutboundLoadRecordStatus.PENDING, phoneRecords.get(0));
    }

    @SneakyThrows
    @Override
    public RemoveRecordFromOutboundLoadResponse removeRecordFromOutboundLoad(String token, Integer serviceId, Integer loadId, Integer sourceId) {
        commonChecks();
        return loadResponse("mock/remove-record-from-outbound-load-response.peb", RemoveRecordFromOutboundLoadResponse.class);
    }

    @SneakyThrows
    @Override
    public GetPhoneDescriptionsResponse getPhoneDescriptions(String token) {
        if (!loggedIn) {
            throw new PresenceUnauthorizedException("Not authorized");
        }
        return loadResponse("mock/get-phone-descriptions-response.peb", GetPhoneDescriptionsResponse.class);
    }

    private boolean loggedIn;
    private Integer sourceId;

    public boolean noLoads;
    public boolean noRecords;
    public boolean noServices;
    public boolean throwGenericError;

    public int serviceId;
    public String serviceStatus = "E";
    public int loadId;
    public boolean loadEnabled = true;
    public OutboundLoadRecordStatus outboundLoadRecordStatusOverride = null;
    private Map<Integer, Object> records = new HashMap<>();

    private static final PebbleEngine pebbleEngine = new PebbleEngine.Builder()
        .autoEscaping(false)
        .loader(new DelegatingLoader(ImmutableList.of(new ClasspathLoader(), new StringLoader())))
        .build();

    private <T extends PresenceResponse> T loadResponse(String filename, Class<T> expectedResultClass) throws IOException {
        String template = ClasspathUtils.resourceToString(filename);
        Map<String, Object> context = new HashMap<>();
        context.put("serviceId", serviceId);
        context.put("serviceStatus", serviceStatus);
        context.put("loadId", loadId);
        context.put("loadEnabled", loadEnabled);
        context.put("records", records);
        if (sourceId != null) {
            context.put("sourceId", sourceId);
        }
        String content = render(template, context);
        return getObjectMapper().readValue(content, expectedResultClass);
    }

    private PresenceResponse loadErrorResponse(Integer errorCode, String errorMessage) throws IOException {
        String template = ClasspathUtils.resourceToString("mock/error-response.peb");
        Map<String, Object> context = new HashMap<>();
        context.put("errorCode", errorCode);
        context.put("errorMessage", errorMessage);
        String content = render(template, context);
        return getObjectMapper().readValue(content, PresenceResponse.class);
    }

    private void addMockRecord(Integer sourceId, String name, OutboundLoadRecordStatus status, PhoneRecord phoneRecord) {
        Map<String, Object> record = new HashMap<>();
        record.put("name", name);
        record.put("status", status.toValue());
        record.put("number", phoneRecord.getNumber());
        record.put("description", phoneRecord.getDescription().toValue());
        records.put(sourceId, record);
    }

    @SneakyThrows
    private static String render(String template, Map<String, Object> context) {
        StringWriter sw = new StringWriter();
        pebbleEngine.getTemplate(template).evaluate(sw, context, Locale.getDefault());
        return sw.toString();
    }

    @SneakyThrows
    private void commonChecks() {
        if (!loggedIn) {
            throw new PresenceUnauthorizedException("Not authorized");
        }
        if (noServices) {
            PresenceResponse response = loadErrorResponse(-335544833, "ADM_NOT_FOUND_OUTBOUND_SERVICE");
            throw new PresenceException("Error", response.getCode(), response.getErrorMessage());
        }
        if (throwGenericError) {
            PresenceResponse response = loadErrorResponse(-1, "GENERIC_ERROR");
            throw new PresenceException("Error", response.getCode(), response.getErrorMessage());
        }
    }

    public void setup(Integer serviceId, Integer loadId) {
        this.serviceId = serviceId;
        this.serviceStatus = "E";
        this.loadId = loadId;
        this.loadEnabled = true;
        this.sourceId = null;
        this.noRecords = false;
        this.noLoads = false;
        this.noServices = false;
        this.throwGenericError = false;
    }

    public void clearRecords() {
        records.clear();
    }
}
