//package fintech.spain.alfa.product.affiliate.validators;
//
//import com.google.common.collect.Lists;
//import fintech.TimeMachine;
//import fintech.lending.core.application.LoanApplication;
//import fintech.lending.core.application.LoanApplicationQuery;
//import fintech.lending.core.application.LoanApplicationService;
//import fintech.lending.core.application.LoanApplicationStatusDetail;
////import fintech.spain.alfa.product.affiliate.AffiliateRegistrationFacade;
//import fintech.spain.alfa.product.affiliate.AffiliateRegistrationStep1FormV1;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.validation.ConstraintValidator;
//import javax.validation.ConstraintValidatorContext;
//import java.util.Optional;
//
//@Deprecated
//public class DeprecatedRejectedClientApplicationValidator implements ConstraintValidator<DeprecatedRejectedClientApplication, AffiliateRegistrationStep1FormV1> {
//
//    @Autowired
//    private LoanApplicationService loanApplicationService;
//
////    @Autowired
////    private AffiliateRegistrationFacade affiliateRegistrationFacade;
//
//    @Override
//    public void initialize(DeprecatedRejectedClientApplication constraintAnnotation) {
//
//    }
//
//    @Override
//    public boolean isValid(AffiliateRegistrationStep1FormV1 value, ConstraintValidatorContext context) {
//        if (StringUtils.isBlank(value.getEmail()) || StringUtils.isBlank(value.getMobilePhone())) {
//            return true;
//        }
////        Long existingClientId = affiliateRegistrationFacade.getExistingClientId(value.getEmail(), value.getMobilePhone());
////
////        if (existingClientId == null) {
////            return true;
////        }
////
//        Optional<LoanApplication> rejectedApplication = loanApplicationService.findLatest(new LoanApplicationQuery()
//            .setClientId(existingClientId)
//            .setSubmittedDateFrom(TimeMachine.today().minusDays(30))
//            .setStatusDetails(Lists.newArrayList(LoanApplicationStatusDetail.REJECTED))
//        );
//
//        if (rejectedApplication.isPresent()) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("").addPropertyNode("email").addConstraintViolation();
//            context.buildConstraintViolationWithTemplate("").addPropertyNode("mobilePhone").addConstraintViolation();
//            return false;
//        }
//        return true;
//    }
//}
