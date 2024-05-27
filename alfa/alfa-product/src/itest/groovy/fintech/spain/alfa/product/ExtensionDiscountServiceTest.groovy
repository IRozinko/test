package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.crm.client.ClientService
import fintech.spain.alfa.product.extension.discounts.db.ExtensionDiscountEntity
import fintech.spain.alfa.product.extension.discounts.db.ExtensionDiscountRepository
import fintech.spain.alfa.product.extension.discounts.impl.ExtensionDiscountServiceBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDate

import static fintech.DateUtils.date

class ExtensionDiscountServiceTest extends AbstractAlfaTest {

    @Autowired
    ExtensionDiscountServiceBean extensionDiscountService

    @Autowired
    ExtensionDiscountRepository extensionDiscountRepository

    @Autowired
    ClientService clientService

    public static final String DISBURSEMENTS_FILE_NAME = "ING_20180909164712_QW7.xml";
    public static final LocalDate ISSUE_DATE = date('2020-01-01')

    def "Create extension discount for loan"() {
        given:
        def extensionDiscountId
        def issueDate = ISSUE_DATE
        def loanId = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(1000.00, 32, issueDate)
            .exportDisbursements(issueDate, DISBURSEMENTS_FILE_NAME).getLoan().getId()
        when:
        extensionDiscountId = extensionDiscountService.createExtensionDiscount(new fintech.spain.alfa.product.extension.discounts.CreateExtensionDiscountCommand()
            .setLoanId(loanId)
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setRateInPercent(35.50))

        then:
        with(extensionDiscountRepository.findOne(extensionDiscountId), {
            id == extensionDiscountId
            effectiveFrom == TimeMachine.today()
            effectiveTo == TimeMachine.today()
            rateInPercent == 35.50
            loan.id ==  loanId
        })

    }

    def "Get extension discount offer for loan"() {
        given:
        def issueDate = ISSUE_DATE
        def loanId = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(1000.00, 32, issueDate)
            .exportDisbursements(issueDate, DISBURSEMENTS_FILE_NAME).getLoan().getId()

        when:
        extensionDiscountService.createExtensionDiscount(new fintech.spain.alfa.product.extension.discounts.CreateExtensionDiscountCommand()
            .setLoanId(loanId)
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setRateInPercent(35.50))

        then:
        def extensionOffer = extensionDiscountService.findExtensionDiscount(loanId)

        and:
        with(extensionOffer, {
            effectiveTo == TimeMachine.today()
            discountInPercent == 35.50
        })
    }

    @Transactional
    def "Deactivate extension discount"() {
        given:
        def issueDate = ISSUE_DATE

        def loanId = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(1000.00, 32, issueDate)
            .exportDisbursements(issueDate, DISBURSEMENTS_FILE_NAME).getLoan().getId()

        when:
        def extensionDiscountId = extensionDiscountService.createExtensionDiscount(new fintech.spain.alfa.product.extension.discounts.CreateExtensionDiscountCommand()
            .setLoanId(loanId)
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setRateInPercent(35.50))
        def entity = extensionDiscountRepository.getRequired(extensionDiscountId)
        then:

        entity.active
        entity.rateInPercent == 35.50

        when:
        extensionDiscountService.deactivateExtensionDiscount(extensionDiscountId)
        entity = extensionDiscountRepository.getRequired(extensionDiscountId)

        then:
        with(entity, {
            (!active)
            rateInPercent == 35.50
        })
    }


    @Transactional
    def "Extension discount expiration"() {
        given:
        def issueDate = ISSUE_DATE

        def loanId = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(1000.00, 32, issueDate)
            .exportDisbursements(issueDate, DISBURSEMENTS_FILE_NAME).getLoan().getId()

        when:
        def extensionDiscountId = extensionDiscountService.createExtensionDiscount(new fintech.spain.alfa.product.extension.discounts.CreateExtensionDiscountCommand()
            .setLoanId(loanId)
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setRateInPercent(35.50))

        then:
        extensionDiscountService.getExtensionDiscount(loanId).isPresent()

        when:
        ExtensionDiscountEntity extensionDiscountEntity = extensionDiscountRepository.getRequired(extensionDiscountId)
        extensionDiscountEntity.setEffectiveTo(TimeMachine.today().minusDays(2))
        extensionDiscountRepository.save(extensionDiscountEntity)

        then:
        extensionDiscountService.getExtensionDiscount(loanId).isPresent()
        extensionDiscountRepository.getRequired(extensionDiscountId).isActive()

        when:
        extensionDiscountService.expireExtensionDiscount()

        then:
        !extensionDiscountService.getExtensionDiscount(loanId).isPresent()
        !extensionDiscountRepository.getRequired(extensionDiscountId).isActive()
    }

    @Transactional
    def "Extension discount: can not create when the 'Effective to' date to any date in the past"() {
        given:
        def issueDate = ISSUE_DATE

        def loanId = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(1000.00, 32, issueDate)
            .exportDisbursements(issueDate, DISBURSEMENTS_FILE_NAME).getLoan().getId()

        when:
        def effectiveDateTo = TimeMachine.today().minusWeeks(1)
        extensionDiscountService.createExtensionDiscount(new fintech.spain.alfa.product.extension.discounts.CreateExtensionDiscountCommand()
            .setLoanId(loanId)
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(effectiveDateTo)
            .setRateInPercent(35.50))

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Invalid effective date range"
    }

    @Transactional
    def "Delete extension discount"() {
        given:
        def issueDate = ISSUE_DATE

        def loanId = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(1000.00, 32, issueDate)
            .exportDisbursements(issueDate, DISBURSEMENTS_FILE_NAME).getLoan().getId()

        when:
        def extensionDiscountId = extensionDiscountService.createExtensionDiscount(new fintech.spain.alfa.product.extension.discounts.CreateExtensionDiscountCommand()
            .setLoanId(loanId)
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today())
            .setRateInPercent(35.50))
        def entity = extensionDiscountRepository.getRequired(extensionDiscountId)
        then:

        entity.active
        entity.rateInPercent == 35.50
        assert extensionDiscountRepository.count() == 1

        when:
        extensionDiscountService.deleteExtensionDiscount(extensionDiscountId)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Cannot delete active extension discount"

        when:
        extensionDiscountService.deactivateExtensionDiscount(extensionDiscountId)
        extensionDiscountService.deleteExtensionDiscount(extensionDiscountId)

        then:
        assert extensionDiscountRepository.count() == 0
    }

}
