package fintech.lending.core.promocode

import fintech.TimeMachine
import fintech.crm.client.ClientService
import fintech.crm.client.CreateClientCommand
import fintech.lending.BaseSpecification
import fintech.lending.core.CreditLineLoanHelper
import fintech.lending.core.LoanHolder
import fintech.lending.core.db.Entities
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail
import fintech.lending.core.promocode.db.PromoCodeClientRepository
import fintech.lending.core.promocode.db.PromoCodeEntity
import fintech.lending.core.promocode.db.PromoCodeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class PromoCodeServiceTest extends BaseSpecification {

    @Autowired
    PromoCodeServiceBean promoCodeService

    @Autowired
    PromoCodeRepository promoCodeRepository

    @Autowired
    PromoCodeClientRepository promoCodeClientRepository

    @Autowired
    ClientService clientService

    @Autowired
    CreditLineLoanHelper loanHelper

    def setup() {
        loanHelper.init()
    }

    def "Create promo code for new/unknown clients"() {
        def promoCodeId

        when:
        promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("SUMMER-MADNESS")
            .setDescription("Testing promo codes")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(100)
            .setRateInPercent(35.50))

        then:
        with(promoCodeRepository.findOne(promoCodeId), {
            id == promoCodeId
            code == "SUMMER-MADNESS"
            description == "Testing promo codes"
            effectiveFrom == TimeMachine.today()
            effectiveTo == TimeMachine.today()
            rateInPercent == 35.50
            maxTimesToApply == 100
            newClientsOnly == Boolean.TRUE
        })

    }

    def "Create promo code for repeating clients"() {
        def promoCodeId
        def clientId = clientService.create(new CreateClientCommand("T42375692"))
        def client = clientService.get(clientId)

        when:
        promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("SUMMER-MADNESS")
            .setDescription("Testing promo codes")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(100)
            .setRateInPercent(35.50)
            .setClientNumbers([client.number, "T456326745"])
        )

        then:
        RuntimeException ex = thrown()
        ex.message == "Unknown clients: T456326745"

        when:
        promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("SUMMER-MADNESS")
            .setDescription("Testing promo codes")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(100)
            .setRateInPercent(35.50)
            .setClientNumbers([client.number])
        )

        then:
        with(promoCodeRepository.findOne(promoCodeId), {
            id == promoCodeId
            code == "SUMMER-MADNESS"
            description == "Testing promo codes"
            effectiveFrom == TimeMachine.today()
            effectiveTo == TimeMachine.today()
            rateInPercent == 35.50
            maxTimesToApply == 100
            newClientsOnly == Boolean.FALSE
        })

        and:
        def clientMappings = promoCodeClientRepository.findAll(Entities.promoCodeClient.promoCodeId.eq(promoCodeId))
        clientMappings.size() == 1
        with(clientMappings[0], {
            clientNumber == client.number
        })
    }

    def "Get promo code offer for new client"() {
        when:
        def newClient = clientService.get(clientService.create(new CreateClientCommand("new-client")))
        def repeatingClient = clientService.get(clientService.create(new CreateClientCommand("repeating-client")))
        def holder = new LoanHolder(clientId: repeatingClient.id)
        loanHelper.applyAndDisburseAndRepay(holder)
        loanHelper.updateLoanStatus(holder, LoanStatus.CLOSED, LoanStatusDetail.PAID, TimeMachine.today())

        and:
        def promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("SUMMER-MADNESS")
            .setDescription("Testing promo code for new clients")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(100)
            .setRateInPercent(35.50)
        )
        promoCodeService.activate(promoCodeId)

        then:
        !promoCodeService.getPromoCodeOffer("SUMMER-MADNESS", repeatingClient.id).isPresent()

        and:
        def maybePromoCodeOffer = promoCodeService.getPromoCodeOffer("SUMMER-MADNESS", newClient.id)
        maybePromoCodeOffer.isPresent()
        with(maybePromoCodeOffer.get(), {
            promoCode == "SUMMER-MADNESS"
            discountInPercent == 35.50
        })
    }

    def "Get promo code offer for repeating client"() {
        when:
        def newClient = clientService.get(clientService.create(new CreateClientCommand("new-client")))
        def repeatingClient = clientService.get(clientService.create(new CreateClientCommand("repeating-client")))
        def holder = new LoanHolder(clientId: repeatingClient.id)
        loanHelper.applyAndDisburseAndRepay(holder)
        loanHelper.updateLoanStatus(holder, LoanStatus.CLOSED, LoanStatusDetail.PAID, TimeMachine.today())

        and:
        Long promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("SUMMER-MADNESS")
            .setDescription("Testing promo code for new clients")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(100)
            .setRateInPercent(35.50)
            .setClientNumbers(["repeating-client"])
        )
        promoCodeService.activate(promoCodeId)

        then:
        !promoCodeService.getPromoCodeOffer("SUMMER-MADNESS", newClient.id).isPresent()

        and:
        def maybePromoCodeOffer = promoCodeService.getPromoCodeOffer("SUMMER-MADNESS", repeatingClient.id)
        maybePromoCodeOffer.isPresent()
        with(maybePromoCodeOffer.get(), {
            promoCode == "SUMMER-MADNESS"
            discountInPercent == 35.50
        })
    }

    @Transactional
    def "Activate expired promo code offer"() {
        def promoCodeId
        given:
        promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("SUMMER-MADNESS")
            .setDescription("Testing promo code for new clients")
            .setEffectiveFrom(TimeMachine.today().minusWeeks(2))
            .setEffectiveTo(TimeMachine.today().plusWeeks(1))
            .setMaxTimesToApply(100)
            .setRateInPercent(35.50)
        )
        PromoCodeEntity promoCode = promoCodeRepository.getRequired(promoCodeId)
        promoCode.setEffectiveTo(TimeMachine.today().minusWeeks(2))
        promoCodeRepository.save(promoCode)

        when:
        promoCodeService.activate(promoCodeId)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Cannot activate promo code: Effective to date is in past"
    }

    def "Get inactive promo code offer"() {
        when:
        def newClient = clientService.get(clientService.create(new CreateClientCommand("new-client")))

        and:
        promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("SUMMER-MADNESS")
            .setDescription("Testing promo code for new clients")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today().plusWeeks(1))
            .setMaxTimesToApply(100)
            .setRateInPercent(35.50)
        )

        then:
        !promoCodeService.getPromoCodeOffer("SUMMER-MADNESS", newClient.id).isPresent()
    }

    def "Get used up promo code offer"() {
        when:
        def client = clientService.get(clientService.create(new CreateClientCommand("repeating-client")))
        def promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("SUMMER-MADNESS")
            .setDescription("Testing promo code for new clients")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(1)
            .setRateInPercent(35.50)
            .setClientNumbers(["repeating-client"])
        )
        promoCodeService.activate(promoCodeId)

        and:
        def holder = new LoanHolder(clientId: client.id, promoCodeId: promoCodeId)
        loanHelper.applyAndDisburseAndRepay(holder)
        loanHelper.updateLoanStatus(holder, LoanStatus.CLOSED, LoanStatusDetail.PAID, TimeMachine.today())

        then:
        !promoCodeService.getPromoCodeOffer("SUMMER-MADNESS", client.id).isPresent()
    }

    def "Create promo code for affiliated clients"() {
        when:
        def client = clientService.get(clientService.create(new CreateClientCommand("affiliate-client")))
        def companyName = "affiliateCompany"
        def promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("AFPC")
            .setDescription("Testing promo code for new clients registered with affiliate")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(1)
            .setRateInPercent(35.50)
            .setSources([companyName, "other"] as Set)
        )
        promoCodeService.activate(promoCodeId)

        then:
        !promoCodeService.getPromoCodeOffer("AFPC", client.id).isPresent()
        promoCodeService.getPromoCodeOffer("AFPC", client.id, companyName).isPresent()
    }

    def "Affiliated client can use promo-code not restricted to specific affiliate"() {
        when:
        def client = clientService.get(clientService.create(new CreateClientCommand("affiliate-client")))
        def companyName = "affiliateCompany"
        def promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("AFPC")
            .setDescription("Testing promo code for new clients registered with affiliate")
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(1)
            .setRateInPercent(35.50)
        )
        promoCodeService.activate(promoCodeId)

        then:
        promoCodeService.getPromoCodeOffer("AFPC", client.id, companyName).isPresent()
    }

    @Transactional
    def "Promo code expiration"() {
        given:
        def client = clientService.get(clientService.create(new CreateClientCommand("affiliate-client")))
        def promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode("AFPC")
            .setDescription("Testing promo code for new clients registered with affiliate")
            .setEffectiveFrom(TimeMachine.today().minusWeeks(1))
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(1)
            .setRateInPercent(35.50)
        )

        when:
        promoCodeService.activate(promoCodeId)

        then:
        promoCodeService.getPromoCodeOffer("AFPC", client.id).isPresent()

        when:
        PromoCodeEntity promoCode = promoCodeRepository.getRequired(promoCodeId)
        promoCode.setEffectiveTo(TimeMachine.today().minusDays(2))
        promoCodeRepository.save(promoCode)

        then:
        !promoCodeService.getPromoCodeOffer("AFPC", client.id).isPresent()
        promoCodeRepository.getRequired(promoCodeId).isActive()

        when:
        promoCodeService.expirePromoCodes()

        then:
        !promoCodeService.getPromoCodeOffer("AFPC", client.id).isPresent()
        !promoCodeRepository.getRequired(promoCodeId).isActive()
    }

    @Transactional
    def "Promocode: can not create when the 'Effective to' date to any date in the past"() {
        when:
        def effectiveDateFrom = TimeMachine.today().minusWeeks(1)
        def code = "AFPC"
        promoCodeService.create(new CreatePromoCodeCommand()
            .setCode(code)
            .setDescription("Testing promo code for new clients registered with affiliate")
            .setEffectiveFrom(effectiveDateFrom)
            .setEffectiveTo(TimeMachine.today().minusDays(1))
            .setMaxTimesToApply(1)
            .setRateInPercent(35.50)
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "The promo code can not be created with the 'Effective to' date to any date in the past"
    }

    @Transactional
    def "Promocode: can not update when the 'Effective to' date to any date in the past"() {
        given:
        def client = clientService.get(clientService.create(new CreateClientCommand("affiliate-client")))
        def effectiveDateFrom = TimeMachine.today().minusWeeks(1)
        def code = "AFPC"
        def promoCodeId = promoCodeService.create(new CreatePromoCodeCommand()
            .setCode(code)
            .setDescription("Testing promo code for new clients registered with affiliate")
            .setEffectiveFrom(effectiveDateFrom)
            .setEffectiveTo(TimeMachine.today())
            .setMaxTimesToApply(1)
            .setRateInPercent(35.50)
        )

        when:
        promoCodeService.activate(promoCodeId)

        then:
        promoCodeService.getPromoCodeOffer(code, client.id).isPresent()

        when:
        def updatePromoCodeCommand = new UpdatePromoCodeCommand()
        updatePromoCodeCommand.setPromoCodeId(promoCodeId)
        updatePromoCodeCommand.setDescription(null)
        updatePromoCodeCommand.setEffectiveFrom(effectiveDateFrom)
        updatePromoCodeCommand.setEffectiveTo(TimeMachine.today().minusDays(1))
        updatePromoCodeCommand.setMaxTimesToApply(1)
        updatePromoCodeCommand.setRateInPercent(10.00)
        promoCodeService.update(updatePromoCodeCommand)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "The promo code can not be created with the 'Effective to' date to any date in the past"
    }

}
