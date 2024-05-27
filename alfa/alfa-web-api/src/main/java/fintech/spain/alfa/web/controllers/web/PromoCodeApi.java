package fintech.spain.alfa.web.controllers.web;

import fintech.lending.core.promocode.PromoCodeService;
import fintech.spain.alfa.web.config.security.CurrentClient;
import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.web.models.ValidatePromoCodeRequest;
import fintech.spain.alfa.web.models.ValidatePromoCodeResponse;
import fintech.spain.web.common.ValidationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PromoCodeApi {

    @Autowired
    private PromoCodeService promoCodeService;

    @Autowired
    private ValidationExceptions validationExceptions;

    @PostMapping("/api/public/web/promo-code")
    public ValidatePromoCodeResponse validate(@CurrentClient WebApiUser client,
                                              @Valid @RequestBody ValidatePromoCodeRequest request) {
        return promoCodeService.getPromoCodeOffer(request.getPromoCode(), client != null ? client.getClientId() : null)
            .map(promoCodeOffer -> new ValidatePromoCodeResponse()
                .setPromoCode(promoCodeOffer.getPromoCode())
                .setDiscount(promoCodeOffer.getDiscountInPercent()))
            .orElseThrow(() -> validationExceptions.invalidValue("promoCode", request.getPromoCode()));
    }

}
