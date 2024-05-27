//package fintech.spain.alfa.product.affiliate;
//
//import fintech.crm.contacts.EmailContact;
//import fintech.crm.contacts.PhoneContact;
//import lombok.Data;
//import lombok.experimental.Accessors;
//
//import java.util.List;
//import java.util.Set;
//
//public interface AffiliateRegistrationFacade {
//
//    @Deprecated
//    AffiliateRegistrationResult step1V1(String affiliateName, AffiliateRegistrationStep1FormV1 form);
//
//    AffiliateRegistrationResult step1(String affiliateName, AffiliateRegistrationStep1Form form);
//
//    AffiliateRegistrationResult step2(AffiliateRegistrationStep2Form form);
//
//    @Deprecated
//    String statusV1(String applicationUuid);
//
//    AffiliateApplicationStatus status(String applicationUuid);
//
//    Set<Long> getAlreadyRegisteredClientIds(List<EmailContact> emailContacts, List<PhoneContact> phoneContacts);
//
//    Long getExistingClientId(String email, String mobilePhone);
//
//    @Data
//    @Accessors(chain = true)
//    class AffiliateRegistrationResult {
//
//        Long clientId;
//        Long workflowId;
//        String applicationUuid;
//        Long applicationId;
//        AffiliateApplicationStatus applicationStatus;
//        boolean verified;
//        boolean existingClient;
//        String applicationDetails;
//    }
//
//}
