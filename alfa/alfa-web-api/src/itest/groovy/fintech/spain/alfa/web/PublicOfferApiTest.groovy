package fintech.spain.alfa.web

import fintech.TimeMachine
import fintech.lending.core.promocode.CreatePromoCodeCommand
import fintech.lending.core.promocode.PromoCodeService
import fintech.spain.alfa.product.lending.Offer
import fintech.spain.alfa.product.lending.OfferSettings
import org.springframework.beans.factory.annotation.Autowired

class PublicOfferApiTest extends AbstractAlfaApiTest {

    @Autowired
    private PromoCodeService promoCodeService;

    def "offer settings"() {
        when:
        def result = restTemplate.getForEntity("/api/public/web/offer-settings", OfferSettings.class)

        then:
        assert result.statusCodeValue == 200
        assert result.body.maxAmount > 0
    }

    def "prepare offer"() {
        when:
        def result = restTemplate.postForEntity("/api/public/web/prepare-offer", new fintech.spain.alfa.web.models.PrepareOfferRequest().setAmount(300.00).setTermInDays(30), Offer.class)

        then:
        assert result.statusCodeValue == 200
        assert result.body.principal == 300.0
        assert result.body.interest > 0
        assert result.body.nominalApr == result.body.monthlyInterestRatePercent * 12
        assert result.body.monthlyInterestRatePercent > 0
    }

    def "prepare offer with promo code"() {
        def request = new fintech.spain.alfa.web.models.PrepareOfferRequest()
            .setAmount(300.00)
            .setTermInDays(30)
            .setPromoCode("SUMMER-MADNESS")

        expect:
        with(restTemplate.postForEntity("/api/public/web/prepare-offer", request, Offer.class), {
            statusCodeValue == 200
            body.principal == 300.0
            body.interest > 0
            body.promoCodeId == null
            body.interestDiscountRatePercent == 0.00
            body.interestDiscountAmount == 0.00
        })

        when:
        def promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("SUMMER-MADNESS")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(100)
            .setRateInPercent(35.50)
        )
        promoCodeService.activate(promoCodeId)

        then:
        with(restTemplate.postForEntity("/api/public/web/prepare-offer", request, Offer.class), {
            statusCodeValue == 200
            body.principal == 300.0
            body.interest > 0
            body.promoCodeId == promoCodeId
            body.interestDiscountRatePercent == 35.50
            body.interestDiscountAmount > 0.00
            body.nominalApr > 0.00
        })

    }
}
