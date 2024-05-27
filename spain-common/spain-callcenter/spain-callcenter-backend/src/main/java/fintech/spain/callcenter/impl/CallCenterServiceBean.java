package fintech.spain.callcenter.impl;

import com.google.common.collect.ImmutableSet;
import fintech.Validate;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.contacts.PhoneContact;
import fintech.crm.contacts.PhoneContactService;
import fintech.presence.OutboundLoad;
import fintech.presence.OutboundLoadRecord;
import fintech.presence.PhoneRecord;
import fintech.presence.PresenceDataService;
import fintech.presence.PresenceException;
import fintech.presence.PresenceOutboundLoadNotAvailable;
import fintech.presence.PresenceService;
import fintech.presence.events.OutboundLoadRecordUpdatedEvent;
import fintech.presence.model.OutboundLoadRecordStatus;
import fintech.presence.model.OutboundLoadStatus;
import fintech.presence.model.PhoneDescription;
import fintech.spain.callcenter.AddCallCommand;
import fintech.spain.callcenter.Call;
import fintech.spain.callcenter.CallCenterDataService;
import fintech.spain.callcenter.CallCenterException;
import fintech.spain.callcenter.CallCenterService;
import fintech.spain.callcenter.CallQuery;
import fintech.spain.callcenter.CallStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CallCenterServiceBean implements CallCenterService {

    private static final String PHONE_COUNTRY_CODE_SPAIN = "34";

    private final CallCenterDataService callCenterDataService;
    private final ClientService clientService;
    private final PresenceService presenceService;
    private final PresenceDataService presenceDataService;
    private final PhoneContactService phoneContactService;

    public CallCenterServiceBean(CallCenterDataService callCenterDataService, ClientService clientService,
                                 PresenceService presenceService, PresenceDataService presenceDataService, PhoneContactService phoneContactService) {
        this.callCenterDataService = callCenterDataService;
        this.clientService = clientService;
        this.presenceService = presenceService;
        this.presenceDataService = presenceDataService;
        this.phoneContactService = phoneContactService;
    }

    @Override
    public void addPhoneRecordsToCallList(Long clientId) throws CallCenterException {
        List<Call> calls = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.PENDING)));
        if (!calls.isEmpty()) {
            for (Call call : calls) {
                OutboundLoad outboundLoad = presenceDataService.getRecord(call.getProviderId()).getOutboundLoad();
                if (outboundLoad.getStatus().equals(OutboundLoadStatus.ENABLED)) {
                    log.info("Call for client {} already pending in outbound load {} with status {}", clientId, outboundLoad.getId(), outboundLoad.getStatus());
                    return;
                } else {
                    log.warn("Call for client {} already pending in outbound load {} with status {}", clientId, outboundLoad.getId(), outboundLoad.getStatus());
                }
            }
        }

        Client client = clientService.get(clientId);
        List<PhoneContact> phoneContacts = phoneContactService.findClientPhoneContacts(clientId);

        List<PhoneRecord> phoneRecords = phoneContacts.stream().map(phoneContact -> {
            Validate.isTrue(phoneContact.getCountryCode().equals(PHONE_COUNTRY_CODE_SPAIN), "Phone contact with country code %s for client %d", phoneContact.getCountryCode(), clientId);
            return new PhoneRecord(phoneContact.getLocalNumber(), PhoneDescription.MOBILE);
        }).collect(Collectors.toList());

        try {
            log.info("Adding call for client {} with name {} and phone records {}", clientId, client.getFullName(), phoneRecords);
            Long providerId = presenceService.addRecordToCurrentOutboundLoad(client.getFullName(), String.valueOf(clientId), phoneRecords);
            AddCallCommand command = new AddCallCommand()
                .setClientId(clientId)
                .setProviderCallId(providerId)
                .setStatus(CallStatus.PENDING);
            callCenterDataService.addCall(command);
        } catch (IOException | PresenceException | PresenceOutboundLoadNotAvailable e) {
            log.error("Exception adding call to Presence", e);
            throw new CallCenterException("Exception adding call to Presence", e);
        }
    }

    @Override
    public void removePhoneRecordsFromCallList(Long clientId) throws CallCenterException {
        List<Call> calls = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.PENDING)));
        for (Call call : calls) {
            try {
                presenceService.removeRecordFromOutboundLoad(call.getProviderId());
                callCenterDataService.updateCallStatus(call.getId(), CallStatus.REMOVED);
            } catch (IOException | PresenceException e) {
                callCenterDataService.updateCallStatus(call.getId(), CallStatus.ERROR);
                log.error("Exception removing call from Presence", e);
                throw new CallCenterException("Exception removing call from Presence", e);
            }
        }
    }

    @Override
    public void updatePhoneRecordsFromProvider() {
        try {
            presenceService.updateCurrentOutboundLoad();
        } catch (IOException | PresenceException | PresenceOutboundLoadNotAvailable e) {
            log.error("Exception updating call from Presence", e);
        }
    }

    @EventListener
    public void onOutboundLoadRecordUpdated(OutboundLoadRecordUpdatedEvent event) {
        Optional<Call> call = callCenterDataService.findFirst(new CallQuery().setProviderId(event.getId()));
        OutboundLoadRecord record = presenceDataService.getRecord(event.getId());

        if (call.isPresent()) {
            log.info("Updating call to list for client {}", call.get().getClientId());
            callCenterDataService.updateCallStatus(call.get().getId(), mapCallStatus(record.getStatus()));
            return;
        }

        Long clientId = extractClientId(record);
        if (clientId == null) {
            log.error("Received update event for outbound load {}. Cannot find corresponding client", event.getId());
            return;
        }
        List<Call> callsByClientId = callCenterDataService.find(new CallQuery().setClientId(clientId));

        if (callsByClientId.isEmpty()) {
            log.info("Adding call to list for client {}", clientId);

            AddCallCommand command = new AddCallCommand()
                .setClientId(clientId)
                .setProviderCallId(event.getId())
                .setStatus(mapCallStatus(record.getStatus()));
            callCenterDataService.addCall(command);
        } else {
            log.info("Updating call to list for client {}", callsByClientId.get(0).getClientId());
            callCenterDataService.updateCallStatus(callsByClientId.get(0).getId(), mapCallStatus(record.getStatus()));
        }
    }

    private Long extractClientId(OutboundLoadRecord record) {
        Set<Long> clientIds = record.getPhoneRecords().stream()
            .map(phoneRecord -> phoneContactService.findByLocalPhoneNumber(phoneRecord.getNumber()))
            .flatMap(List::stream)
            .map(PhoneContact::getClientId)
            .collect(Collectors.toSet());

        if (clientIds.isEmpty()) {
            log.warn("No clients found with the phone number from outbound load record {}", record);
            return null;
        }
        if (clientIds.size() > 1) {
            log.warn("Found multiple clients with same number from outbound load record {}", record);
            return null;
        }

        return clientIds.iterator().next();
    }

    private static CallStatus mapCallStatus(OutboundLoadRecordStatus recordStatus) {
        switch (recordStatus) {
            case PENDING:
            case SCHEDULED:
                return CallStatus.PENDING;
            case COMPLETED:
                return CallStatus.COMPLETED;
            case UNLOADED:
                return CallStatus.REMOVED;
            default:
                return CallStatus.ERROR;
        }

    }
}
