//package fintech.spain.alfa.product.affiliate.validators;
//
//import fintech.crm.CrmConstants;
//import fintech.crm.contacts.EmailContact;
//import fintech.crm.contacts.EmailContactService;
//import fintech.crm.contacts.PhoneContact;
//import fintech.crm.contacts.PhoneContactService;
//import fintech.crm.documents.IdentityDocument;
//import fintech.crm.documents.IdentityDocumentService;
//import fintech.spain.alfa.product.affiliate.AffiliateRegistrationFacade;
//import fintech.spain.alfa.product.affiliate.AffiliateRegistrationStep1Form;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.validation.ConstraintValidator;
//import javax.validation.ConstraintValidatorContext;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//public class UniqueClientContactsValidator implements ConstraintValidator<UniqueClientContacts, AffiliateRegistrationStep1Form> {
//
//    @Autowired
//    private IdentityDocumentService identityDocumentService;
//
//    @Autowired
//    private PhoneContactService phoneContactService;
//
//    @Autowired
//    private EmailContactService emailContactService;
//
//    @Autowired
//    private AffiliateRegistrationFacade affiliateRegistrationFacade;
//
//    @Override
//    public void initialize(UniqueClientContacts constraintAnnotation) {
//
//    }
//
//    @Override
//    public boolean isValid(AffiliateRegistrationStep1Form value, ConstraintValidatorContext context) {
//        if (StringUtils.isBlank(value.getDocumentNumber()) || StringUtils.isBlank(value.getEmail()) || StringUtils.isBlank(value.getMobilePhone())) {
//            return true;
//        }
//
//        Optional<IdentityDocument> document = identityDocumentService.findByNumber(value.getDocumentNumber(), CrmConstants.IDENTITY_DOCUMENT_DNI, true);
//        List<EmailContact> emailContacts = emailContactService.findByEmail(value.getEmail());
//        List<PhoneContact> phoneContacts = phoneContactService.findByLocalPhoneNumber(value.getMobilePhone());
//        boolean foundDni = document.isPresent();
//        boolean foundEmail = !emailContacts.isEmpty();
//        boolean foundPhone = !phoneContacts.isEmpty();
//        if (!foundDni && !foundEmail && !foundPhone) {
//            return true;
//        }
//        if (foundEmail && foundPhone) {
//            Set<Long> commonClientIds = affiliateRegistrationFacade.getAlreadyRegisteredClientIds(emailContacts, phoneContacts);
//            if (!commonClientIds.isEmpty()) {
//                return true;
//            }
//        }
//
//        context.disableDefaultConstraintViolation();
//        if (foundDni) {
//            context.buildConstraintViolationWithTemplate("").addPropertyNode("documentNumber").addConstraintViolation();
//        }
//        if (foundEmail) {
//            context.buildConstraintViolationWithTemplate("").addPropertyNode("email").addConstraintViolation();
//        }
//        if (foundPhone) {
//            context.buildConstraintViolationWithTemplate("").addPropertyNode("mobilePhone").addConstraintViolation();
//        }
//        return false;
//    }
//}
