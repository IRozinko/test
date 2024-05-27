package fintech.spain.alfa.web

import fintech.TimeMachine
import fintech.lending.core.promocode.CreatePromoCodeCommand
import fintech.lending.core.promocode.PromoCodeService
import fintech.spain.web.common.ApiError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod

class PromoCodeApiTest extends AbstractAlfaApiTest {

    @Autowired
    PromoCodeService promoCodeService

    def "Validate promo code request with invalid code"() {
        def request = new fintech.spain.alfa.web.models.ValidatePromoCodeRequest()

        when:
        def noCodeResult = restTemplate.exchange("/api/public/web/promo-code", HttpMethod.POST,
            ApiHelper.authorized("", new fintech.spain.alfa.web.models.ValidatePromoCodeRequest()), ApiError.class)

        then:
        with(noCodeResult, {
            statusCodeValue == 400
            body.fieldErrors.size() == 1
            body.fieldErrors.containsKey("promoCode")
        })


        when:
        request.setPromoCode("11-11234")

        def invalidCodeResult = restTemplate.exchange("/api/public/web/promo-code", HttpMethod.POST,
            ApiHelper.authorized("", request), ApiError.class)

        then:
        with(invalidCodeResult, {
            statusCodeValue == 400
            body.fieldErrors.size() == 1
            body.fieldErrors.containsKey("promoCode")
        })
    }

    def "Valid promo code"() {
        given:
        Long promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("SUMMER-MADNESS")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(100)
            .setRateInPercent(35.50))
        promoCodeService.activate(promoCodeId)

        def request = new fintech.spain.alfa.web.models.ValidatePromoCodeRequest()
        request.setPromoCode("summer-madness")

        when:
        def result = restTemplate.exchange("/api/public/web/promo-code", HttpMethod.POST,
            ApiHelper.authorized("", request), fintech.spain.alfa.web.models.ValidatePromoCodeResponse.class)

        then:
        with(result, {
            statusCodeValue == 200
            body.promoCode == "SUMMER-MADNESS"
            body.discount == 35.50
        })
    }

}
