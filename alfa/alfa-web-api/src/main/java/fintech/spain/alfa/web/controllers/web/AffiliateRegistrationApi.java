//package fintech.spain.alfa.web.controllers.web;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import fintech.affiliate.AffiliateService;
//import fintech.affiliate.model.SaveAffiliateRequestCommand;
//import fintech.spain.alfa.web.config.security.AffiliateApiUser;
//import fintech.spain.alfa.product.affiliate.AffiliateRegistrationFacade;
//import fintech.spain.alfa.product.affiliate.AffiliateRegistrationFacade.AffiliateRegistrationResult;
//import fintech.spain.alfa.product.affiliate.AffiliateRegistrationStep1Form;
//import fintech.spain.alfa.product.affiliate.AffiliateRegistrationStep1FormV1;
//import fintech.spain.alfa.product.affiliate.AffiliateRegistrationStep2Form;
//import fintech.spain.alfa.product.web.WebAuthorities;
//import fintech.spain.alfa.product.web.WebLoginService;
//import lombok.Value;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.time.Duration;
//
//@Slf4j
//@RestController
//public class AffiliateRegistrationApi {
//
////    private final AffiliateRegistrationFacade affiliateRegistrationFacade;
//    private final WebLoginService webLoginService;
//    private final AffiliateService affiliateService;
//
//    @Autowired
//    public AffiliateRegistrationApi(AffiliateRegistrationFacade affiliateRegistrationFacade, WebLoginService webLoginService, AffiliateService affiliateService) {
//        this.affiliateRegistrationFacade = affiliateRegistrationFacade;
//        this.webLoginService = webLoginService;
//        this.affiliateService = affiliateService;
//    }
//
//    @Secured(WebAuthorities.AFFILIATE)
//    @PostMapping("/api/affiliate/v1/step1")
//    @Deprecated
//    public AffiliateRegistrationStep1Result step1V1(@AuthenticationPrincipal AffiliateApiUser user, @RequestBody @Valid AffiliateRegistrationStep1FormV1 form) {
//        log.info("Affiliate registration request: [{}], affiliate [{}]", form, user.getAffiliateName());
//
//        AffiliateRegistrationResult result = affiliateRegistrationFacade.step1V1(user.getAffiliateName(), form);
//        AffiliateRegistrationStep1Result response = new AffiliateRegistrationStep1Result(result.getApplicationUuid(), null, result.isExistingClient(), null, result.getApplicationDetails());
//
//        affiliateService.saveAffiliateRequest(new SaveAffiliateRequestCommand()
//            .setRequestType("step1V1")
//            .setApplicationId(result.getApplicationId())
//            .setClientId(result.getClientId())
//            .setRequest(form)
//            .setResponse(response));
//
//        return response;
//    }
//
//    @Secured(WebAuthorities.AFFILIATE)
//    @PostMapping("/api/affiliate/v2/step1")
//    public AffiliateRegistrationStep1Result step1(@AuthenticationPrincipal AffiliateApiUser user, @RequestBody @Valid AffiliateRegistrationStep1Form form) {
//        log.info("Affiliate registration request: [{}], affiliate [{}]", form, user.getAffiliateName());
//        AffiliateRegistrationResult result = affiliateRegistrationFacade.step1(user.getAffiliateName(), form);
//        String token = null;
//        if (result.getApplicationStatus().isOk()) {
//            token = webLoginService.login(result.getClientId(), user.getAffiliateName(), Duration.ofHours(2));
//        }
//        AffiliateRegistrationStep1Result response = new AffiliateRegistrationStep1Result(result.getApplicationUuid(), result.getApplicationStatus().toString(), result.isExistingClient(), token, result.getApplicationDetails());
//
//        affiliateService.saveAffiliateRequest(new SaveAffiliateRequestCommand()
//            .setRequestType("step1")
//            .setApplicationId(result.getApplicationId())
//            .setClientId(result.getClientId())
//            .setRequest(form)
//            .setResponse(response));
//
//        return response;
//    }
//
//    @Secured(WebAuthorities.AFFILIATE)
//    @RequestMapping(value = {"/api/affiliate/v1/step2", "/api/affiliate/v2/step2"}, method = RequestMethod.POST)
//    public ResponseEntity<AffiliateRegistrationStep2Result> step2(@AuthenticationPrincipal AffiliateApiUser affiliateApiUser, @RequestBody @Valid AffiliateRegistrationStep2Form form) {
//        log.info("Affiliate verifying phone request: [{}], affiliate [{}]", form, affiliateApiUser.getAffiliateName());
//
//        AffiliateRegistrationResult result = affiliateRegistrationFacade.step2(form);
//
//        if (result.isVerified()) {
//            AffiliateRegistrationStep2Result response = new AffiliateRegistrationStep2Result(
//                "SMS Code Success", result.getApplicationUuid(), webLoginService.login(result.getClientId(), affiliateApiUser.getAffiliateName(), Duration.ofHours(2)));
//
//            affiliateService.saveAffiliateRequest(new SaveAffiliateRequestCommand()
//                .setRequestType("step2")
//                .setApplicationId(result.getApplicationId())
//                .setClientId(result.getClientId())
//                .setRequest(form)
//                .setResponse(response));
//
//            return ResponseEntity.ok(response);
//        }
//
//        AffiliateRegistrationStep2Result response = new AffiliateRegistrationStep2Result("SMS Code Failure", result.getApplicationUuid(), null);
//
//        affiliateService.saveAffiliateRequest(new SaveAffiliateRequestCommand()
//            .setRequestType("step2")
//            .setApplicationId(result.getApplicationId())
//            .setClientId(result.getClientId())
//            .setRequest(form)
//            .setResponse(response));
//
//        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
//    }
//
//    @Secured(WebAuthorities.AFFILIATE)
//    @GetMapping("/api/affiliate/v1/status/{applicationUuid}")
//    @Deprecated
//    public StatusResult statusV1(@AuthenticationPrincipal AffiliateApiUser user, @PathVariable String applicationUuid) {
//        log.info("Affiliate getting status request: [{}], affiliate [{}]", applicationUuid, user.getAffiliateName());
//
//        return new StatusResult(affiliateRegistrationFacade.statusV1(applicationUuid));
//    }
//
//    @Secured(WebAuthorities.AFFILIATE)
//    @GetMapping("/api/affiliate/v2/status/{applicationUuid}")
//    public StatusResult status(@AuthenticationPrincipal AffiliateApiUser user, @PathVariable String applicationUuid) {
//        log.info("Affiliate getting status request: [{}], affiliate [{}]", applicationUuid, user.getAffiliateName());
//
//        return new StatusResult(affiliateRegistrationFacade.status(applicationUuid).toString());
//    }
//
//    @Value
//    public static class AffiliateRegistrationStep1Result {
//        @JsonProperty("requestid")
//        String applicationUuid;
//        @JsonProperty("status")
//        String applicationStatus;
//        boolean repeated;
//        String token;
//        @JsonProperty("status_details")
//        String applicationStatusDetails;
//    }
//
//    @Value
//    public static class AffiliateRegistrationStep2Result {
//        String message;
//        @JsonProperty("request_id")
//        String applicationUuid;
//        String token;
//    }
//
//    @Value
//    public static class StatusResult {
//        String status;
//    }
//}
