package fintech.lending.core

import fintech.DateUtils
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils

import java.time.LocalDate
import java.time.LocalDateTime

class LoanHolder {

    Long clientId = RandomUtils.nextLong(0, Long.MAX_VALUE)
    String number = RandomStringUtils.randomAlphabetic(8)
    LocalDateTime applicationDate = DateUtils.dateTime("2000-12-30 11:00:00")
    LocalDate offerDate = DateUtils.date("2000-12-31")
    LocalDate issueDate = DateUtils.date("2001-01-01")
    BigDecimal requestedPrincipal = 2000.00g
    Long requestedMonths = 1
    BigDecimal offeredPrincipal = 1000.00g
    BigDecimal offeredInterest = 20.00g
    Long offeredDays = 0L
    Integer invoicePaymentDate = 10
    Long discountId
    Long promoCodeId

    Long applicationId
    Long loanId
    Long disbursementId
}
