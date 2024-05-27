package fintech.crm.contacts;

import fintech.crm.contacts.db.EmailContactEntity;

import java.util.List;
import java.util.Optional;

public interface EmailContactService {

    Long addEmailContact(AddEmailContactCommand command);

    List<EmailContact> findAllEmailContacts(Long clientId);

    Optional<EmailContact> findPrimaryEmail(Long clientId);

    void makeEmailPrimary(Long emailContactId) throws DuplicatePrimaryEmailException;

    boolean isEmailAvailableForClient(Long clientId, String email);

    List<EmailContact> findByEmail(String email);

    List<EmailContact> findByEmailNormalized(String email);

    Optional<EmailContactEntity> findExistingEmailContact(Long clientId, String email);

    void verifyEmail(EmailVerificationCommand emailVerificationCommand);
}
