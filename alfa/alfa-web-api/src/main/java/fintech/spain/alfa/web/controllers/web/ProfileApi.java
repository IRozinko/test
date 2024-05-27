package fintech.spain.alfa.web.controllers.web;

import fintech.TimeMachine;
import fintech.crm.attachments.AttachmentSubType;
import fintech.spain.alfa.web.common.WebRequestUtils;
import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.web.models.*;
import fintech.spain.alfa.web.services.WebProfileService;
import fintech.spain.alfa.product.lending.Offer;
import fintech.spain.alfa.product.lending.OfferSettings;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import fintech.spain.alfa.product.web.WebAuthorities;
import fintech.spain.alfa.web.models.*;
import fintech.web.api.models.OkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class ProfileApi {

    @Autowired
    private WebProfileService webProfileService;

    @Autowired
    private UnderwritingFacade underwritingFacade;

    @GetMapping("/api/web/profile/loans")
    public LoansResponse loans(@AuthenticationPrincipal WebApiUser user) {
        return webProfileService.getLoans(user.getClientId());
    }

    @GetMapping("/api/web/profile/offer-settings")
    public OfferSettings offerSettings(@AuthenticationPrincipal WebApiUser user) {
        return underwritingFacade.clientOfferSettings(user.getClientId(), TimeMachine.today());
    }

//    @PostMapping("/api/web/profile/prepare-offer")
//    public Offer prepareOffer(@AuthenticationPrincipal WebApiUser user, @Valid @RequestBody PrepareOfferRequest request) {
//        return webProfileService.prepareOffer(user.getClientId(), request);
//    }

    @PostMapping("/api/web/profile/approve-offer/{applicationId}")
    public OkResponse approveOffer(@AuthenticationPrincipal WebApiUser user, @PathVariable Long applicationId) {
        underwritingFacade.webApproveApplication(user.getClientId(), applicationId, WebRequestUtils.resolveIpAddress());
        return OkResponse.OK;
    }

    @PostMapping("/api/web/profile/approve-upsell-offer/{applicationId}")
    public OkResponse approveUpsellOffer(@AuthenticationPrincipal WebApiUser user, @PathVariable Long applicationId, @Valid @RequestBody ApproveUpsellOfferRequest request) {
        underwritingFacade.webApproveUpsellOffer(user.getClientId(), applicationId, request.getPrincipal(), WebRequestUtils.resolveIpAddress(), request.getAbsource());
        return OkResponse.OK;
    }

    @GetMapping("/api/web/profile/loan-application")
    public LoanApplicationData loanApplication(@AuthenticationPrincipal WebApiUser user) {
        return webProfileService.getLoanApplication(user.getClientId());
    }

//    @PostMapping("/api/web/profile/submit-loan-application")
//    public OkResponse submitLoanApplication(@AuthenticationPrincipal WebApiUser user, @Valid @RequestBody SubmitLoanApplicationRequest request) {
//        webProfileService.submitLoanApplication(user.getClientId(), request);
//        return OkResponse.OK;
//    }

    @Secured(WebAuthorities.WEB_FULL)
    @PostMapping("/api/web/profile/change-password")
    public OkResponse changePassword(@AuthenticationPrincipal WebApiUser user, @RequestBody @Valid ChangePasswordRequest params) {
        webProfileService.changePassword(user.getClientId(), params);
        return OkResponse.OK;
    }

    @GetMapping("/api/web/profile/personal-details")
    public PersonalDetailsResponse getPersonalDetails(@AuthenticationPrincipal WebApiUser user) {
        return webProfileService.getClient(user.getClientId());
    }

    @PostMapping(path = "/api/web/profile/upload-file")
    public OkResponse uploadDocument(@AuthenticationPrincipal WebApiUser user, @RequestParam("file") MultipartFile multiPart, @RequestParam("type") AttachmentSubType type) throws IOException {
        log.info("Uploading file {}, {}, {}", multiPart.getOriginalFilename(), multiPart.getSize(), type);
        webProfileService.uploadDocument(user.getClientId(), multiPart, type);
        return OkResponse.OK;
    }

    @GetMapping("/api/web/profile/uploaded-files")
    public List<AttachmentInfo> listUploadedFiles(@AuthenticationPrincipal WebApiUser user) {
        return webProfileService.findAttachmentInfos(user.getClientId());
    }

    @PostMapping("/api/web/profile/uploaded-files")
    public OkResponse uploadedDocuments(@AuthenticationPrincipal WebApiUser user) {
        webProfileService.uploadedDocuments(user.getClientId());
        return OkResponse.OK;
    }

    @PostMapping("/api/web/profile/accept-marketing")
    public AcceptMarketingResponse acceptMarketing(@AuthenticationPrincipal WebApiUser user, @Valid @RequestBody AcceptMarketingRequest request) {
        boolean newFlag = webProfileService.changeAcceptMarketing(user.getClientId(), request.getAcceptMarketing(), Optional.ofNullable(request.getSource()).orElse("OTHER"));
        return new AcceptMarketingResponse().setAcceptMarketing(newFlag);
    }

}
