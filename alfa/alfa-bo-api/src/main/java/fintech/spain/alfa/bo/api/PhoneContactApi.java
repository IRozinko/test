package fintech.spain.alfa.bo.api;

import fintech.bo.api.model.client.PhoneContactRequest;
import fintech.crm.contacts.AddPhoneCommand;
import fintech.crm.contacts.PhoneContact;
import fintech.crm.contacts.PhoneContactService;
import fintech.crm.contacts.PhoneSource;
import fintech.crm.contacts.PhoneType;
import fintech.crm.contacts.UpdatePhoneContactCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class PhoneContactApi {

    private final PhoneContactService phoneContactService;

    @Autowired
    public PhoneContactApi(PhoneContactService phoneContactService) {
        this.phoneContactService = phoneContactService;
    }

    @PostMapping("/api/bo/phone-contact")
    public ResponseEntity<Long> create(@RequestBody @Valid PhoneContactRequest req) {
        Long phoneContactId = phoneContactService.addPhoneContact(toAddPhoneCommand(req));
        if (req.isPrimary())
            phoneContactService.makePhonePrimary(phoneContactId);

        return ResponseEntity.ok(phoneContactId);
    }

    @PostMapping("/api/bo/phone-contact/{id}")
    public ResponseEntity<PhoneContact> create(@PathVariable Long id, @RequestBody @Valid PhoneContactRequest req) {
        return ResponseEntity.ok(phoneContactService.updatePhoneContact(id, toUpdatePhoneCommand(req)));
    }

    @PostMapping("/api/bo/phone-contact/{id}/make-primary")
    public ResponseEntity makePrimary(@PathVariable long id) {
        phoneContactService.makePhonePrimary(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/bo/phone-contact/{id}/toggle-active")
    public ResponseEntity toggleActive(@PathVariable long id) {
        phoneContactService.toggleActive(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/bo/phone-contact/{id}/toggle-legal-consent")
    public ResponseEntity toggleLegalConsent(@PathVariable long id) {
        phoneContactService.toggleLegalConsent(id);
        return ResponseEntity.noContent().build();
    }

    private AddPhoneCommand toAddPhoneCommand(PhoneContactRequest req) {
        return new AddPhoneCommand()
            .setClientId(req.getClientId())
            .setCountryCode(req.getCountryCode())
            .setLocalNumber(req.getPhoneNumber())
            .setLegalConsent(req.isLegalConsent())
            .setSource(PhoneSource.valueOf(req.getSource()))
            .setType(PhoneType.valueOf(req.getType()));
    }

    private UpdatePhoneContactCommand toUpdatePhoneCommand(PhoneContactRequest req) {
        return new UpdatePhoneContactCommand()
            .setCountryCode(req.getCountryCode())
            .setLocalNumber(req.getPhoneNumber())
            .setLegalConsent(req.isLegalConsent())
            .setSource(PhoneSource.valueOf(req.getSource()))
            .setType(PhoneType.valueOf(req.getType()));
    }

}
