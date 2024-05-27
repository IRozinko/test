package fintech.presence.impl;

import fintech.Validate;
import fintech.presence.*;
import fintech.presence.events.OutboundLoadRecordUpdatedEvent;
import fintech.presence.model.*;
import fintech.presence.settings.PresenceSettings;
import fintech.settings.SettingsService;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static fintech.presence.settings.PresenceSettings.PRESENCE_SETTINGS;

@Slf4j
@Component
public class PresenceServiceBean implements PresenceService {

    private final String username;
    private final String password;
    private final PresenceAdministratorProvider presenceAdministratorProvider;
    private final SettingsService settingsService;
    private final PresenceDataService presenceDataService;
    private final ApplicationEventPublisher eventPublisher;

    private final RetryPolicy loginRetryPolicy = new RetryPolicy()
        .retryOn(PresenceUnauthorizedException.class)
        .withMaxRetries(1)
        .withDelay(1, TimeUnit.MILLISECONDS);

    @Autowired
    public PresenceServiceBean(@Value("${presence.username:mock}") String username,
                               @Value("${presence.password:mock}") String password,
                               PresenceAdministratorProvider presenceAdministratorProvider, SettingsService settingsService,
                               PresenceDataService presenceDataService, ApplicationEventPublisher eventPublisher) {
        this.username = username;
        this.password = password;
        this.presenceAdministratorProvider = presenceAdministratorProvider;
        this.settingsService = settingsService;
        this.presenceDataService = presenceDataService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void updateCurrentOutboundLoad() throws IOException, PresenceException, PresenceOutboundLoadNotAvailable {
        PresenceSettings settings = settingsService.getJson(PRESENCE_SETTINGS, PresenceSettings.class);
        updateOutboundLoad(settings.getServiceId(), settings.getLoadId());
    }

    @Override
    public void updateOutboundLoad(Integer serviceId, Integer loadId) throws IOException, PresenceException, PresenceOutboundLoadNotAvailable {
        String token = login();
        try {
            checkOutboundService(token, serviceId);
            OutboundLoad currentOutboundLoad = getOutboundLoad(token, serviceId, loadId)
                .orElseThrow(() -> new PresenceOutboundLoadNotAvailable("No active outbound loads found for serviceId " + serviceId));
            GetOutboundLoadRecordsResponse outboundLoadRecords = presenceAdministratorProvider.getOutboundLoadRecords(token, currentOutboundLoad.getServiceId(), currentOutboundLoad.getLoadId());
            for (GetOutboundLoadRecordsResponse.OutboundLoadRecordData outboundLoadRecordData : outboundLoadRecords.getData()) {
                updateOutboundLoadRecord(currentOutboundLoad.getId(), token, outboundLoadRecordData);
            }
        } finally {
            presenceAdministratorProvider.logout(token);
        }
    }

    @Override
    public Long addRecordToCurrentOutboundLoad(String name, String customerId, List<PhoneRecord> phoneRecords) throws IOException, PresenceException, PresenceOutboundLoadNotAvailable {
        Validate.notBlank(name, "Invalid record name");
        validatePhoneRecords(phoneRecords);

        PresenceSettings settings = settingsService.getJson(PRESENCE_SETTINGS, PresenceSettings.class);
        String token = login();

        try {
            checkOutboundService(token, settings.getServiceId());

            GetPhoneDescriptionsResponse phoneDescriptionsResponse = presenceAdministratorProvider.getPhoneDescriptions(token);
            checkPhoneDescriptions(phoneDescriptionsResponse.getData(), phoneRecords);

            OutboundLoad currentOutboundLoad = getOutboundLoad(token, settings.getServiceId(), settings.getLoadId())
                .orElseThrow(() -> new PresenceOutboundLoadNotAvailable("No active outbound loads found for serviceId " + settings.getServiceId()));

            Integer sourceId = presenceDataService.getNextSourceId();
            presenceAdministratorProvider.addRecordToOutboundLoad(token, settings.getServiceId(), currentOutboundLoad.getLoadId(), sourceId, name, customerId, phoneRecords);
            AddOrUpdateOutboundLoadRecordCommand command = new AddOrUpdateOutboundLoadRecordCommand()
                .setOutboundLoadId(currentOutboundLoad.getId())
                .setSourceId(sourceId)
                .setName(name)
                .setStatus(OutboundLoadRecordStatus.PENDING)
                .setPhoneRecords(phoneRecords);
            OutboundLoadRecord newOutboundLoadRecord = presenceDataService.addOrUpdateOutboundLoadRecord(command);

            log.info("Added record to service outbound load {} with serviceId {} customerId {}", settings.getServiceId(), currentOutboundLoad.getLoadId(), customerId);
            return newOutboundLoadRecord.getId();
        } finally {
            presenceAdministratorProvider.logout(token);
        }
    }

    @Override
    public void removeRecordFromOutboundLoad(Long id) throws IOException, PresenceException {
        PresenceSettings settings = settingsService.getJson(PRESENCE_SETTINGS, PresenceSettings.class);
        String token = login();
        try {
            checkOutboundService(token, settings.getServiceId());
            OutboundLoadRecord outboundLoadRecord = presenceDataService.getRecord(id);
            RemoveRecordFromOutboundLoadResponse removeRecordFromOutboundLoadResponse = presenceAdministratorProvider
                .removeRecordFromOutboundLoad(token, settings.getServiceId(), outboundLoadRecord.getOutboundLoad().getLoadId(), outboundLoadRecord.getSourceId());
            if (removeRecordFromOutboundLoadResponse.getData().getTotalUnloadedRecord() == 0) {
                log.warn("Removing record with sourceId {} loadId {} serviceId {} resulted in 0 records unloaded",
                    outboundLoadRecord.getSourceId(), outboundLoadRecord.getOutboundLoad().getLoadId(), settings.getServiceId());
            }
            AddOrUpdateOutboundLoadRecordCommand command = new AddOrUpdateOutboundLoadRecordCommand()
                .setOutboundLoadId(outboundLoadRecord.getOutboundLoad().getId())
                .setOutboundLoadRecordId(outboundLoadRecord.getId())
                .setSourceId(outboundLoadRecord.getSourceId())
                .setName(outboundLoadRecord.getName())
                .setStatus(OutboundLoadRecordStatus.UNLOADED);
            presenceDataService.addOrUpdateOutboundLoadRecord(command);
            eventPublisher.publishEvent(new OutboundLoadRecordUpdatedEvent(outboundLoadRecord.getId()));
        } finally {
            presenceAdministratorProvider.logout(token);
        }
    }

    private void checkOutboundService(String token, Integer serviceId) throws IOException, PresenceException {
        GetOutboundServiceInfoResponse outboundServiceInfoResponse = presenceAdministratorProvider.getOutboundServiceInfo(token, serviceId);
        if (!outboundServiceInfoResponse.getData().getGeneral().getStatus().equals(OutboundServiceStatus.ENABLED)) {
            log.warn("Outbound serviceId {} is not enabled", serviceId);
        }
    }

    private void checkPhoneDescriptions(List<GetPhoneDescriptionsResponse.PhoneDescription> phoneDescriptions, List<PhoneRecord> phoneRecords) {
        Validate.notEmpty(phoneDescriptions, "Empty phone descriptions, check Presence server configuration");

        List<Integer> codes = phoneDescriptions.stream().map(GetPhoneDescriptionsResponse.PhoneDescription::getCode).collect(Collectors.toList());
        boolean codesNotFound = phoneRecords.stream().anyMatch(phoneRecord -> !codes.contains(phoneRecord.getDescription().toValue()));
        Validate.isTrue(!codesNotFound, "Invalid phone descriptions, check Presence server configuration");
    }

    private Optional<OutboundLoad> getOutboundLoad(String token, Integer serviceId, Integer loadId) throws IOException, PresenceException {
        List<OutboundLoad> outboundLoads = syncOutboundLoads(token, serviceId);

        if (loadId != null) {
            return getOutboundLoadByLoadId(outboundLoads, serviceId, loadId);
        } else {
            return getCurrentOutboundLoad(outboundLoads);
        }
    }

    private List<OutboundLoad> syncOutboundLoads(String token, Integer serviceId) throws IOException, PresenceException {
        GetOutboundLoadsResponse outboundLoadsResponse = presenceAdministratorProvider.getOutboundLoads(token, serviceId);
        List<OutboundLoad> outboundLoads = new ArrayList<>();
        outboundLoadsResponse.getData().forEach(outboundLoadData -> {
            AddOrUpdateOutboundLoadCommand command = new AddOrUpdateOutboundLoadCommand()
                .setServiceId(outboundLoadData.getServiceId())
                .setLoadId(outboundLoadData.getLoadId())
                .setStatus(outboundLoadData.getStatus())
                .setAddedAt(outboundLoadData.getDate())
                .setDescription(outboundLoadData.getDescription());

            outboundLoads.add(presenceDataService.addOrUpdateOutboundLoad(command));
        });
        return outboundLoads;
    }

    private Optional<OutboundLoad> getCurrentOutboundLoad(List<OutboundLoad> outboundLoads) {
        return outboundLoads.stream()
            .filter(outboundLoadData -> outboundLoadData.getStatus().equals(OutboundLoadStatus.ENABLED))
            .max(Comparator.comparing(OutboundLoad::getAddedAt));
    }

    private Optional<OutboundLoad> getOutboundLoadByLoadId(List<OutboundLoad> outboundLoads, Integer serviceId, Integer loadId) {
        Optional<OutboundLoad> currentOutboundLoadMaybe = outboundLoads.stream()
            .filter(outboundLoadData -> outboundLoadData.getLoadId().equals(loadId))
            .findFirst();

        currentOutboundLoadMaybe.ifPresent(outboundLoadData -> {
            if (!outboundLoadData.getStatus().equals(OutboundLoadStatus.ENABLED)) {
                log.warn("Outbound loadId {} for serviceId {} is not enabled", loadId, serviceId);
            }
        });
        return currentOutboundLoadMaybe;
    }

    private void updateOutboundLoadRecord(Long outboundLoadId, String token, GetOutboundLoadRecordsResponse.OutboundLoadRecordData outboundLoadRecordData) throws IOException, PresenceException {
        GetOutboundLoadRecordInfoResponse outboundLoadRecordInfoResponse = presenceAdministratorProvider.getOutboundLoadRecordInfo(token, outboundLoadRecordData.getServiceId(), outboundLoadRecordData.getLoadId(), outboundLoadRecordData.getSourceId());
        GetOutboundLoadRecordInfoResponse.OutboundLoadRecordInfoData outboundLoadRecordInfoData = outboundLoadRecordInfoResponse.getData().get(0);
        AddOrUpdateOutboundLoadRecordCommand command = new AddOrUpdateOutboundLoadRecordCommand()
            .setOutboundLoadId(outboundLoadId)
            .setName(outboundLoadRecordInfoData.getName())
            .setStatus(outboundLoadRecordInfoData.getStatus())
            .setSourceId(outboundLoadRecordInfoData.getSourceId())
            .setQualificationCode(outboundLoadRecordInfoData.getLastQCode())
            .setPhoneRecords(outboundLoadRecordInfoData.getPhoneRecordsWrapper().getPhoneRecords());
        OutboundLoadRecord outboundLoadRecord = presenceDataService.addOrUpdateOutboundLoadRecord(command);
        eventPublisher.publishEvent(new OutboundLoadRecordUpdatedEvent(outboundLoadRecord.getId()));
    }

    private String login() {
        return Failsafe.with(loginRetryPolicy).get(() -> {
            GetTokenResponse tokenResponse = presenceAdministratorProvider.getToken();
            presenceAdministratorProvider.login(tokenResponse.getData().getToken(), username, password);
            return tokenResponse.getData().getToken();
        });
    }

    private void validatePhoneRecords(List<PhoneRecord> phoneRecords) {
        Validate.notEmpty(phoneRecords, "Invalid records");
        Validate.noNullElements(phoneRecords, "Invalid records");

        phoneRecords.forEach(phoneRecord -> {
            Validate.notEmpty(phoneRecord.getNumber(), "Invalid phone number");
            Validate.notNull(phoneRecord.getDescription(), "Invalid phone description for phone number %s", phoneRecord.getNumber());
            Validate.isTrue(!phoneRecord.getDescription().equals(PhoneDescription.NOT_SPECIFIED), "Phone description not specified for phone number %s", phoneRecord.getNumber());
        });
    }
}
