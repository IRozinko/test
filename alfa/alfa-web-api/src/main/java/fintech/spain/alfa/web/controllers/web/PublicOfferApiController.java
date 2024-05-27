package fintech.spain.alfa.web.controllers.web;

import fintech.TimeMachine;
import fintech.lending.core.promocode.PromoCodeService;
import fintech.spain.alfa.web.config.security.CurrentClient;
import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.web.models.PrepareOfferRequest;
import fintech.spain.alfa.product.lending.Inquiry;
import fintech.spain.alfa.product.lending.Offer;
import fintech.spain.alfa.product.lending.OfferSettings;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static fintech.BigDecimalUtils.amount;

@RestController
@Slf4j
public class PublicOfferApiController {

    @Autowired
    private UnderwritingFacade underwritingFacade;

    @Autowired
    private PromoCodeService promoCodeService;

    @GetMapping("/api/public/web/offer-settings")
    public OfferSettings offerSettings() {
        return underwritingFacade.publicOfferSettings();
    }

    @PostMapping("/api/public/web/prepare-offer")
    public Offer prepareOffer(@CurrentClient WebApiUser client, @Valid @RequestBody PrepareOfferRequest request) {
        Inquiry inquiry = new Inquiry()
            .setPrincipal(request.getAmount())
            .setTermInDays(request.getTermInDays())
            .setInterestDiscountPercent(amount(0))
            .setSubmittedAt(TimeMachine.now());

        promoCodeService.getPromoCodeOffer(request.getPromoCode(), client != null ? client.getClientId() : null)
            .ifPresent(promoCodeOffer -> {
                inquiry.setInterestDiscountPercent(promoCodeOffer.getDiscountInPercent());
                inquiry.setPromoCodeId(promoCodeOffer.getPromoCodeId());
            });

        return underwritingFacade.makeOffer(inquiry);
    }
}
