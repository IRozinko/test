package fintech.spain.alfa.bo.api;

import fintech.spain.alfa.bo.model.SendCmsNotificationRequest;
import fintech.spain.alfa.product.workflow.DormantsLocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class DormantsLocApiController {

    private final DormantsLocService locFacade;

    public DormantsLocApiController(DormantsLocService locFacade) {
        this.locFacade = locFacade;
    }

    @PostMapping("api/bo/workflow/dormants/resendPreOfferEmail")
    void resendPreOfferEmail(@RequestBody SendCmsNotificationRequest request) {
        locFacade.resendPreOfferEmail(request.getApplicationId());
    }

    @PostMapping("api/bo/workflow/dormants/resendPreOfferSms")
    void resendPreOfferSms(@RequestBody SendCmsNotificationRequest request) {
        locFacade.resendPreOfferSms(request.getApplicationId());
    }

    @PostMapping("api/bo/workflow/dormants/resendOfferEmail")
    void resendOfferEmail(@RequestBody SendCmsNotificationRequest request) {
        locFacade.resendOfferEmail(request.getApplicationId());
    }

    @PostMapping("api/bo/workflow/dormants/resendOfferSms")
    void resendOfferSms(@RequestBody SendCmsNotificationRequest request) {
        locFacade.resendOfferSms(request.getApplicationId());
    }

}
