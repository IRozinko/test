package fintech.crm.contacts;

import fintech.crm.contacts.db.PhoneVerificationEntity;

import java.util.List;
import java.util.Optional;

public interface PhoneContactService {
	
	Long addPhoneContact(AddPhoneCommand command);

    PhoneContact updatePhoneContact(long id, UpdatePhoneContactCommand command);

    void makePhonePrimary(Long phoneContactId) throws DuplicatePrimaryPhoneException;

    void toggleActive(long phoneContactId);

    void toggleLegalConsent(long phoneContactId);

    boolean isPrimaryPhoneNumberAvailable(String localNumber);
	
	Optional<PhoneContact> findPrimaryPhone(Long clientId);

    List<PhoneContact> findAdditionalPhones(Long clientId);
	
	List<PhoneContact> findClientPhoneContacts(Long clientId);

    Optional<PhoneContact> findActualAdditionalPhone(Long clientId);

    List<PhoneContact> findByLocalPhoneNumber(String phoneNumber);

    void addPhoneVerification(AddPhoneVerificationCommand command);

    boolean verifyPhone(VerifyPhoneCommand command);

    void markPhoneUnVerified(Long phoneContactIdLong);

    Optional<PhoneVerificationEntity> findLatestVerificationCode(Long phoneContactId);

    int countSentVerificationCodes(Long clientId, Long phoneContactId);

    int getVerificationAttempts(Long phoneContactId);
}
