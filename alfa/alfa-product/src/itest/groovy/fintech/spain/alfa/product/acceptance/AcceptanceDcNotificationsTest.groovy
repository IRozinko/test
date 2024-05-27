package fintech.spain.alfa.product.acceptance

import fintech.TimeMachine
import fintech.lending.core.loan.LoanService
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.cms.CmsSetup

import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoField

import static fintech.BigDecimalUtils.amount

class AcceptanceDcNotificationsTest extends AbstractAlfaTest {

    @Autowired
    DcTestCases dcTestCases

    @Autowired
    fintech.spain.alfa.product.web.spi.PopupService popupService

    @Autowired
    fintech.spain.alfa.product.web.db.PopupRepository popupRepository

    @Autowired
    LoanService loanService

    @Unroll
    def "Send #cmsKey notifications on dpd #dpd"() {
        when:
        TimeMachine.useFixedClockAt(LocalDateTime.now().with(ChronoField.HOUR_OF_DAY,15))
        LocalDate issueDate = TimeMachine.today().minusDays(30).minusDays(dpd)
        fintech.spain.alfa.product.testing.TestLoan loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(amount(200), 30, issueDate)
            .applyPenalty(issueDate.plusDays(30).plusDays(dpd))
            .postToDc()

        and:
        loan.triggerDcActions()

        then:
        assert loan.getLoan().getOverdueDays() == dpd
        assert loan.toClient().emailCount(cmsKey) == emailCount
        assert loan.toClient().smsCount(cmsKey) == smsCount

        where:
        dpd | cmsKey                       | emailCount | smsCount
        3   | CmsSetup.DPD_03_NOTIFICATION | 0          | 1
        7   | CmsSetup.DPD_07_NOTIFICATION | 1          | 0
        10  | CmsSetup.DPD_10_NOTIFICATION | 1          | 0
        17  | CmsSetup.DPD_17_NOTIFICATION | 0          | 1
        25  | CmsSetup.DPD_25_NOTIFICATION | 1          | 0
        35  | CmsSetup.DPD_35_NOTIFICATION | 0          | 1
        45  | CmsSetup.DPD_45_NOTIFICATION | 0          | 1
        55  | CmsSetup.DPD_55_NOTIFICATION | 1          | 0
        60  | CmsSetup.DPD_60_NOTIFICATION | 0          | 1
    }

    def "DC Web Popup notifications"() {
        when:
        LocalDate issueDate = TimeMachine.today().minusDays(60)

        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
        fintech.spain.alfa.product.testing.TestLoan loan = client
            .issueActiveLoan(amount(200), 30, issueDate)
            .postToDc()

        and:
        loan.triggerDcActions()

        then:
        def actual = popupService.getActual(client.clientId)
        assert actual.size() == 1
        def popupInfo = actual.get(0)
        assert popupInfo.type == fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION
        assert popupInfo.resolution == fintech.spain.alfa.product.web.model.PopupResolution.NONE

        when:
        loan.writeOff(TimeMachine.today(), loan.getBalance().principalDue, loan.getBalance().interestDue)
        loan.writeOffPenalty(TimeMachine.today(), loan.getBalance().penaltyDue)

        and:
        loan.triggerDcActions()

        then:
        def emptyActual = popupService.getActual(client.clientId)
        assert emptyActual.size() == 0
        def popupEntity = popupRepository.getRequired(popupInfo.id)

        assert popupEntity.type == fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION
        assert popupEntity.resolution == fintech.spain.alfa.product.web.model.PopupResolution.EXHAUSTED
    }

    @Unroll
    def "Notification #cmsKey - extension limit is reached"() {
        when:
        int dpdCount = 260;
        LocalDate today = TimeMachine.today();
        LocalDate issueDate = today.minusDays(dpdCount);

        fintech.spain.alfa.product.testing.TestLoan loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .randomEmailAndName("Extension_limit_no_DC_notifications")
            .registerDirectly()
            .issueActiveLoan(amount(200), 15L, issueDate)
            .postToDc();
        4.times {
            loan.extend(amount(70), issueDate.plusDays(15).plusDays(3));
        }
        LocalDate virtualToday = issueDate;
        try {
            while (virtualToday.isBefore(today.plusDays(1))) {
                TimeMachine.useFixedClockAt(virtualToday.atTime(15, 0));
                loanService.resolveLoanDerivedValues(loan.getLoanId(), virtualToday);
                loan.postToDc();
                virtualToday = virtualToday.plusDays(1);
            }
        } finally {
            TimeMachine.useDefaultClock();
        }

        and:
        loan.triggerDcActions()

        then:
        assert loan.toClient().emailCount(cmsKey) == emailCount
        assert loan.toClient().smsCount(cmsKey) == smsCount

        where:
        dpd | cmsKey                                 | emailCount | smsCount
        1   | CmsSetup.DPD_01_NOTIFICATION           | 1          | 1
        3   | CmsSetup.DPD_03_EXTENSION_NOTIFICATION | 0          | 1
        7   | CmsSetup.DPD_07_EXTENSION_NOTIFICATION | 1          | 0
        10  | CmsSetup.DPD_10_EXTENSION_NOTIFICATION | 1          | 0
        17  | CmsSetup.DPD_17_EXTENSION_NOTIFICATION | 0          | 1
        25  | CmsSetup.DPD_25_EXTENSION_NOTIFICATION | 1          | 0
        35  | CmsSetup.DPD_35_EXTENSION_NOTIFICATION | 0          | 1
        3   | CmsSetup.DPD_03_NOTIFICATION           | 0          | 0
        7   | CmsSetup.DPD_07_NOTIFICATION           | 0          | 0
        10  | CmsSetup.DPD_10_NOTIFICATION           | 0          | 0
        17  | CmsSetup.DPD_17_NOTIFICATION           | 0          | 0
        25  | CmsSetup.DPD_25_NOTIFICATION           | 0          | 0
        35  | CmsSetup.DPD_35_NOTIFICATION           | 0          | 0
        45  | CmsSetup.DPD_45_NOTIFICATION           | 0          | 1
        55  | CmsSetup.DPD_55_NOTIFICATION           | 1          | 0
        60  | CmsSetup.DPD_60_NOTIFICATION           | 0          | 1
    }
}
