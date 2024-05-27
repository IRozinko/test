package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.lending.core.application.LoanApplicationQuery
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.creditlimit.AddCreditLimitCommand
import fintech.lending.core.creditlimit.CreditLimitService
import fintech.lending.core.discount.ApplyDiscountCommand
import fintech.lending.core.discount.DiscountService
import fintech.lending.core.loan.LoanService
import fintech.lending.core.product.ProductService
import fintech.lending.payday.settings.PaydayProductSettings
import fintech.spain.alfa.product.strategy.interest.AlfaInterestStrategy
import fintech.spain.alfa.strategy.StrategyType
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties
import fintech.strategy.CalculationStrategyService
import fintech.strategy.SaveCalculationStrategyCommand
import fintech.strategy.db.CalculationStrategyRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static fintech.BigDecimalUtils.amount
import static fintech.DateUtils.date
import static fintech.DateUtils.dateTime

class UnderwritingFacadeTest extends AbstractAlfaTest {

    @Autowired
    fintech.spain.alfa.product.lending.UnderwritingFacade facade

    @Autowired
    LoanService loanService

    @Autowired
    DiscountService discountService

    @Autowired
    LoanApplicationService loanApplicationService

    @Autowired
    CreditLimitService creditLimitService

    @Autowired
    ProductService productService

    @Autowired
    CalculationStrategyRepository strategyRepository

    @Autowired
    CalculationStrategyService calculationStrategyService

    def "client interest discount in percent"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        when:
        def discount = discountService.applyDiscount(new ApplyDiscountCommand(clientId: client.clientId, rateInPercent: 10.00, effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today(), maxTimesToApply: 1))

        then:
        with(facade.getDiscountOffer(client.clientId, TimeMachine.today())) {
            discountId == discount.id
            rateInPercent == 10.00
        }

        when:
        client
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        then:
        with(loanApplicationService.findLatest(LoanApplicationQuery.byClientId(client.clientId)).get()) {
            offeredInterestDiscountPercent == 10.00
            discountId == discount.id
        }

        when:
        client
            .submitApplicationAndStartFirstLoanWorkflow(300.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        then:
        with(loanApplicationService.findLatest(LoanApplicationQuery.byClientId(client.clientId)).get()) {
            offeredInterestDiscountPercent == 0.00
            !discountId
        }

        and:
        with(facade.getDiscountOffer(client.clientId, TimeMachine.today())) {
            !discountId
            rateInPercent == 15.00
        }

        when:
        client
            .submitApplicationAndStartFirstLoanWorkflow(300.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        then:
        with(loanApplicationService.findLatest(LoanApplicationQuery.byClientId(client.clientId)).get()) {
            offeredInterestDiscountPercent == 15.00
            !discountId
        }

        and:
        with(facade.getDiscountOffer(client.clientId, TimeMachine.today())) {
            !discountId
            rateInPercent == 15.00
        }
    }

    def "make offer without strategy id"() {
        given: "inquiry without interest strategy id"
        def testApplication = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toApplication()
        def inquiry = new fintech.spain.alfa.product.lending.Inquiry()
            .setPrincipal(300.00g)
            .setTermInDays(30)
            .setSubmittedAt(dateTime("2018-01-02 12:00:00"))
            .setInterestDiscountPercent(0.00g)
            .setApplicationId(testApplication.application.id)

        when:
        def offer = facade.makeOffer(inquiry)

        then: "default interest strategy is used with monthly rate 35%"
        with(offer) {
            assert principal == 300.00
            assert interest == 105.00
            assert total == 405.00
            assert monthlyInterestRatePercent == 35.00
            assert nominalApr == 35.00 * 12
            assert aprPercent == 3752.00
            assert interestDiscountAmount == 0.00
            assert interestDiscountRatePercent == 0.00
            assert termInDays == 30L
            assert offerDate == date("2018-01-02")
            assert maturityDate == date("2018-02-01")
        }

        when:
        def anotherOffer = facade.makeOffer(new fintech.spain.alfa.product.lending.Inquiry()
            .setPrincipal(50.00g)
            .setTermInDays(7)
            .setSubmittedAt(TimeMachine.now())
            .setInterestDiscountPercent(0.00g)
            .setApplicationId(testApplication.application.id)
        )

        then:
        with(anotherOffer) {
            assert principal == 50.00
            assert interest == 4.00
            assert total == 54.00
            assert monthlyInterestRatePercent == 34.29
            assert nominalApr == 34.29 * 12
            assert aprPercent == 5431.00
        }
    }

    def "make offer with strategy id"() {
        given: "inquiry interest strategy id"
        def testApplication = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toApplication()
        def strategyId = calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.INTEREST.getType())
                .setCalculationType(AlfaInterestStrategy.CALCULATION_TYPE.name())
                .setVersion("002")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new MonthlyInterestStrategyProperties()
                        .setMonthlyInterestRate(amount(38.00))
                        .setUsingDecisionEngine(false)
                        .setScenario("test")
                ))

        def inquiry = new fintech.spain.alfa.product.lending.Inquiry()
            .setInterestStrategyId(strategyId)
            .setPrincipal(300.00g)
            .setTermInDays(30)
            .setSubmittedAt(dateTime("2018-01-02 12:00:00"))
            .setInterestDiscountPercent(0.00g)
            .setApplicationId(testApplication.application.id)

        when:
        def offer = facade.makeOffer(inquiry)

        then: "interest strategy is used with monthly rate 38%"
        with(offer) {
            assert principal == 300.00
            assert interest == 114.00
            assert total == 414.00
            assert monthlyInterestRatePercent == 38.00
            assert nominalApr == 38.00 * 12
            assert aprPercent == 4933.00
            assert interestDiscountAmount == 0.00
            assert interestDiscountRatePercent == 0.00
            assert termInDays == 30L
            assert offerDate == date("2018-01-02")
            assert maturityDate == date("2018-02-01")
        }
    }

    def "make offer with strategy id using Decision Engine"() {
        given: "inquiry interest strategy id"
        mockSpainScoringProvider.useInterestRateResponse(amount(32))
        def testApplication = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUpWithApplication()
            .toApplication()
        def strategyId = calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.INTEREST.getType())
                .setCalculationType(AlfaInterestStrategy.CALCULATION_TYPE.name())
                .setVersion("002")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new MonthlyInterestStrategyProperties()
                        .setMonthlyInterestRate(amount(50.00))
                        .setUsingDecisionEngine(true)
                        .setScenario("interest_rate_setting")
                ))

        def inquiry = new fintech.spain.alfa.product.lending.Inquiry()
            .setInterestStrategyId(strategyId)
            .setPrincipal(300.00g)
            .setTermInDays(30)
            .setSubmittedAt(dateTime("2018-01-02 12:00:00"))
            .setInterestDiscountPercent(0.00g)
            .setApplicationId(testApplication.application.id)

        when:
        def offer = facade.makeOffer(inquiry)

        then: "interest strategy is used with monthly rate 32% from DE"
        with(offer) {
            assert principal == 300.00
            assert interest == 96.00
            assert total == 396.00
            assert monthlyInterestRatePercent == 32.00
            assert nominalApr == 32.00 * 12
            assert aprPercent == 2831.00
            assert interestDiscountAmount == 0.00
            assert interestDiscountRatePercent == 0.00
            assert termInDays == 30L
            assert offerDate == date("2018-01-02")
            assert maturityDate == date("2018-02-01")
        }
        cleanup:
        mockSpainScoringProvider.reset();
    }

    def "make offer with strategy id using Decision Engine when interest rate 0"() {
        given: "inquiry interest strategy id"
        def score = amount(0)
        mockSpainScoringProvider.useInterestRateResponse(score)
        def testApplication = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUpWithApplication()
            .toApplication()
        def strategyId = calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.INTEREST.getType())
                .setCalculationType(AlfaInterestStrategy.CALCULATION_TYPE.name())
                .setVersion("002")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new MonthlyInterestStrategyProperties()
                        .setMonthlyInterestRate(amount(50.00))
                        .setUsingDecisionEngine(true)
                        .setScenario("interest_rate_setting")
                ))

        def inquiry = new fintech.spain.alfa.product.lending.Inquiry()
            .setInterestStrategyId(strategyId)
            .setPrincipal(300.00g)
            .setTermInDays(30)
            .setSubmittedAt(dateTime("2018-01-02 12:00:00"))
            .setInterestDiscountPercent(0.00g)
            .setApplicationId(testApplication.application.id)

        when:
        def offer = facade.makeOffer(inquiry)

        then: "interest strategy is used with monthly rate 0% from DE"
        with(offer) {
            assert principal == 300.00
            assert interest == 0.00
            assert total == 300.00
            assert monthlyInterestRatePercent == score
            assert nominalApr == 0.00 * 12
            assert aprPercent == 0.00
            assert interestDiscountAmount == 0.00
            assert interestDiscountRatePercent == 0.00
            assert termInDays == 30L
            assert offerDate == date("2018-01-02")
            assert maturityDate == date("2018-02-01")
        }
        cleanup:
        mockSpainScoringProvider.reset();
    }

    def "make offer with discount and with strategy id using Decision Engine when interest rate 0 "() {
        given: "inquiry interest strategy id"
        def score = amount(0)
        mockSpainScoringProvider.useInterestRateResponse(score)
        def testApplication = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUpWithApplication()
            .toApplication()
        def strategyId = calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.INTEREST.getType())
                .setCalculationType(AlfaInterestStrategy.CALCULATION_TYPE.name())
                .setVersion("002")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new MonthlyInterestStrategyProperties()
                        .setMonthlyInterestRate(amount(50.00))
                        .setUsingDecisionEngine(true)
                        .setScenario("interest_rate_setting")
                ))

        def inquiry = new fintech.spain.alfa.product.lending.Inquiry()
            .setInterestStrategyId(strategyId)
            .setPrincipal(300.00g)
            .setTermInDays(30)
            .setSubmittedAt(dateTime("2018-01-02 12:00:00"))
            .setInterestDiscountPercent(10.00g)
            .setApplicationId(testApplication.application.id)

        when:
        def offer = facade.makeOffer(inquiry)

        then: "interest strategy is used with monthly rate 0% from DE"
        with(offer) {
            assert principal == 300.00
            assert interest == 0.00
            assert total == 300.00
            assert monthlyInterestRatePercent == score
            assert nominalApr == 0.00 * 12
            assert aprPercent == 0.00
            assert interestDiscountAmount == 0.00
            assert interestDiscountRatePercent == 10.00
            assert termInDays == 30L
            assert offerDate == date("2018-01-02")
            assert maturityDate == date("2018-02-01")
        }
        cleanup:
        mockSpainScoringProvider.reset();
    }

    def "make offer with discount"() {
        given:
        def testApplication = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toApplication()

        expect:
        with(facade.makeOffer(new fintech.spain.alfa.product.lending.Inquiry()
            .setPrincipal(300)
            .setTermInDays(30)
            .setSubmittedAt(TimeMachine.now())
            .setInterestDiscountPercent(10.0)
            .setApplicationId(testApplication.application.id)
        )) {
            assert principal == 300.00
            assert interest == 94.00
            assert total == 394.00
            assert monthlyInterestRatePercent == 31.33
            assert nominalApr == 31.33 * 12
            assert aprPercent == 2656.00
            assert interestDiscountAmount == 10.50
            assert interestDiscountRatePercent == 10.00
        }

        and:
        with(facade.makeOffer(new fintech.spain.alfa.product.lending.Inquiry()
            .setPrincipal(300)
            .setTermInDays(30)
            .setSubmittedAt(TimeMachine.now())
            .setInterestDiscountPercent(100.0)
            .setApplicationId(testApplication.application.id)
        )) {
            assert principal == 300.00
            assert interest == 0.00
            assert total == 300.00
            assert monthlyInterestRatePercent == 0.00
            assert aprPercent == 0.00
            assert interestDiscountAmount == 105.00
            assert interestDiscountRatePercent == 100.00
        }
    }

    def "public offer settings"() {
        given:
        def settings = productService.getSettings(AlfaConstants.PRODUCT_ID, PaydayProductSettings.class)

        when:
        settings.publicOfferSettings.amountStep = 50
        settings.publicOfferSettings.defaultAmount = 100
        settings.publicOfferSettings.defaultTerm = 30
        settings.publicOfferSettings.maxAmount = 300
        settings.publicOfferSettings.maxTerm = 60
        settings.publicOfferSettings.minAmount = 50
        settings.publicOfferSettings.minTerm = 10
        settings.publicOfferSettings.termStep = 1
        productService.updateSettings(AlfaConstants.PRODUCT_ID, settings)

        then:
        def publicOfferSettings = facade.publicOfferSettings()
        with(publicOfferSettings) {
            minAmount == 50
            maxAmount == 300
            minTerm == 10
            maxTerm == 60
            defaultAmount == 100
            defaultTerm == 30
            amountStep == 50
            termStep == 1
        }
    }

    def "client offer settings - first loan"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        def clientOfferSettings = facade.clientOfferSettings(client.getClientId(), TimeMachine.today())

        then:
        with(clientOfferSettings) {
            maxAmount == 300.00
            interestDiscountPercent == 0.0
        }
    }

    def "client offer settings - credit limit less than default amount"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
        def settings = productService.getSettings(AlfaConstants.PRODUCT_ID, PaydayProductSettings.class)
        settings.clientOfferSettings.defaultAmount = 600
        productService.updateSettings(AlfaConstants.PRODUCT_ID, settings)

        when:
        def clientOfferSettings = facade.clientOfferSettings(client.getClientId(), TimeMachine.today())

        then:
        with(clientOfferSettings) {
            maxAmount == 300.00
            defaultAmount == 300.00
            interestDiscountPercent == 0.0
        }
    }

    @Unroll
    def "client offer discounts, principal #principalPaid, discount #discount"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(principalPaid, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .updateDerivedValues(date("2018-02-02"))
            .toClient()

        expect:
        facade.clientOfferSettings(client.getClientId(), date("2018-02-02")).interestDiscountPercent == discount

        where:
        principalPaid | discount
        299.00        | 0.00
        300.00        | 15.00
        750.00        | 20.00
        1350.00       | 25.00
    }

    def "client offer has no discount if loan was delayed"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(450.0, 30, date("2018-01-01"))
            .repayAll(date("2018-03-01"))
            .updateDerivedValues(date("2018-03-02"))
            .toClient()

        expect:
        facade.clientOfferSettings(client.getClientId(), date("2018-03-02")).interestDiscountPercent == 0.0
    }

    @Unroll
    def "client offer max amount, #scenario"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        def settings = productService.getSettings(AlfaConstants.PRODUCT_ID, PaydayProductSettings.class)
        settings.clientOfferSettings.useCreditLimitAsMaxAmount = useCreditLimitMaxAmount
        settings.clientOfferSettings.maxAmount = 400
        productService.updateSettings(AlfaConstants.PRODUCT_ID, settings)

        when: "credit limit is added"
        if (creditLimitAmount) {
            def creditLimit = new AddCreditLimitCommand()
            creditLimit.clientId = client.clientId
            creditLimit.limit = creditLimitAmount
            creditLimit.activeFrom = date("2018-01-01")
            creditLimit.reason = "Test"
            creditLimitService.addLimit(creditLimit)
        }

        then: "max amount is "
        facade.clientOfferSettings(client.getClientId(), TimeMachine.today()).maxAmount == expectedAmount

        where:
        scenario                   | useCreditLimitMaxAmount | creditLimitAmount | expectedAmount
        "credit limit is not zero" | true                    | 200.00            | 200.00
        "credit limit is zero"     | true                    | 0.00              | 400.00
        "credit limit is missed"   | true                    | null              | 400.00

        "credit limit is not zero" | false                   | 200.00            | 400.00
        "credit limit is zero"     | false                   | 0.00              | 400.00
        "credit limit is missed"   | false                   | null              | 400.00
    }

    @Unroll
    def "client offer default amount, #scenario"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        def settings = productService.getSettings(AlfaConstants.PRODUCT_ID, PaydayProductSettings.class)
        settings.clientOfferSettings.useCreditLimitAsMaxAmount = false
        settings.clientOfferSettings.setSliderToMaxAmount = setSliderToMaxAmount
        settings.clientOfferSettings.maxAmount = 400
        settings.clientOfferSettings.defaultAmount = 300
        productService.updateSettings(AlfaConstants.PRODUCT_ID, settings)

        expect: "default amount is "
        facade.clientOfferSettings(client.getClientId(), TimeMachine.today()).defaultAmount == expectedDefaultAmount

        where:
        scenario                     | setSliderToMaxAmount | expectedDefaultAmount
        "setSliderToMaxAmount=true"  | true                 | 400.00
        "setSliderToMaxAmount=false" | false                | 300.00

    }
}
