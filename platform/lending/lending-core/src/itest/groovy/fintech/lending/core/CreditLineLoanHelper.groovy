package fintech.lending.core

import fintech.DateUtils
import fintech.JsonUtils
import fintech.filestorage.CloudFile
import fintech.filestorage.FileStorageService
import fintech.filestorage.SaveFileCommand
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.commands.LoanApplicationOfferCommand
import fintech.lending.core.application.commands.SaveCreditLimitCommand
import fintech.lending.core.application.commands.SubmitLoanApplicationCommand
import fintech.lending.core.invoice.InvoiceService
import fintech.lending.core.invoice.commands.GeneratedInvoice
import fintech.lending.core.invoice.db.InvoiceItemType
import fintech.lending.core.loan.LoanQuery
import fintech.lending.core.loan.LoanService
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail
import fintech.lending.core.loan.commands.ApplyFeeCommand
import fintech.lending.core.loan.commands.ApplyInterestCommand
import fintech.lending.core.loan.commands.DisburseLoanCommand
import fintech.lending.core.loan.commands.IssueLoanCommand
import fintech.lending.core.loan.commands.RepayLoanCommand
import fintech.lending.core.loan.commands.SettleDisbursementCommand
import fintech.lending.core.loan.db.LoanRepository
import fintech.lending.core.loan.spi.BreakLoanStrategy
import fintech.lending.core.loan.spi.LoanRegistry
import fintech.lending.core.periods.PeriodService
import fintech.lending.core.periods.PeriodStatus
import fintech.lending.core.periods.db.PeriodEntity
import fintech.lending.core.periods.db.PeriodRepository
import fintech.lending.core.product.ProductService
import fintech.lending.core.product.ProductType
import fintech.lending.core.product.db.ProductEntity
import fintech.lending.core.product.db.ProductRepository
import fintech.lending.core.repayments.LoanRepaymentStrategy
import fintech.lending.creditline.impl.CreditLineBreakLoanStrategy
import fintech.lending.creditline.impl.CreditLineDisbursementStrategy
import fintech.lending.creditline.impl.CreditLineLoanIssueStrategy
import fintech.lending.creditline.impl.TestLoanDerivedValuesResolver
import fintech.lending.creditline.settings.CreditLineInterestSettings
import fintech.lending.creditline.settings.CreditLineInvoiceSettings
import fintech.lending.creditline.settings.CreditLineOfferSettings
import fintech.lending.creditline.settings.CreditLinePricingSettings
import fintech.lending.creditline.settings.CreditLineProductSettings
import fintech.lending.creditline.settings.CreditLineRepaymentSettings
import fintech.payments.DisbursementService
import fintech.payments.InstitutionService
import fintech.payments.PaymentService
import fintech.payments.commands.AddDisbursementCommand
import fintech.payments.commands.AddInstitutionCommand
import fintech.payments.commands.AddPaymentCommand
import fintech.payments.model.DisbursementExportResult
import fintech.payments.model.PaymentType
import fintech.transactions.Balance
import fintech.transactions.BalanceQuery
import fintech.transactions.TransactionService
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate

import javax.transaction.Transactional
import java.time.LocalDate

import static fintech.lending.creditline.TransactionConstants.*
import static fintech.lending.creditline.settings.CreditLineRepaymentSettings.DistributionOrderType.orderType
import static fintech.lending.creditline.settings.CreditLineRepaymentSettings.DistributionOrderType.orderTypeSubType
import static fintech.lending.creditline.settings.CreditLineRepaymentSettings.InvoiceItemTypeSubTypePair.type
import static fintech.lending.creditline.settings.CreditLineRepaymentSettings.InvoiceItemTypeSubTypePair.typeSubType
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric

@Component
class CreditLineLoanHelper {

    static PRODUCT_ID = 101L

    @Autowired
    ProductRepository productRepository

    @Autowired
    LoanService loanService

    @Autowired
    LoanApplicationService loanApplicationService

    @Autowired
    DisbursementService disbursementService

    @Autowired
    InstitutionService institutionService

    @Autowired
    TransactionService transactionService

    @Autowired
    PaymentService paymentService

    @Autowired
    LoanRegistry loanRegistry

    @Autowired
    InvoiceService invoiceService

    @Autowired
    CreditLineDisbursementStrategy creditLineDisbursementStrategy

    @Autowired
    LoanRepaymentStrategy invoiceItemBasedRepaymentStrategy

    @Autowired
    CreditLineLoanIssueStrategy creditLineLoanIssueStrategy

    @Autowired
    TestLoanDerivedValuesResolver testLoanDerivedValuesResolver

    @Autowired
    CreditLineBreakLoanStrategy breakLoanStrategy

    @Autowired
    PeriodRepository periodRepository

    @Autowired
    TransactionTemplate txTemplate

    @Autowired
    PeriodService periodService

    @Autowired
    ProductService productService

    @Autowired
    LoanRepository loanRepository

    @Autowired
    private FileStorageService fileStorageService

    Long institutionAccountId

    Long institutionId

    @Transactional
    void init() {
        createProduct()
        institutions()
    }

    void issueLoan(LoanHolder holder) {
        holder.loanId = loanService.issueLoan(buildIssueLoanCommand(holder))
    }

    void updateOffer(LoanHolder holder) {
        loanApplicationService.updateOffer(buildLoanApplicationOfferCommand(holder))
        loanApplicationService.saveCreditLimit(new SaveCreditLimitCommand(holder.applicationId, holder.offeredPrincipal))
    }

    void submitApplication(LoanHolder holder) {
        holder.applicationId = loanApplicationService.submit(buildSubmitLoanApplicationCommand(holder))
    }

    def disburse(LoanHolder holder) {
        holder.disbursementId = disbursementService.add(new AddDisbursementCommand(clientId: holder.clientId, loanId: holder.loanId, institutionId: institutionId, institutionAccountId: institutionAccountId, amount: holder.offeredPrincipal, valueDate: holder.issueDate, reference: RandomStringUtils.randomAlphabetic(8)))
        FileInputStream file = new FileInputStream(File.createTempFile("disbursement", "export"))
        def cloudFile = fileStorageService.save(new SaveFileCommand(contentType: "application/pdf", originalFileName: "disbursement", inputStream: file, directory: "temp"))
        file.close()
        disbursementService.exported(holder.disbursementId, holder.issueDate.atStartOfDay(), new DisbursementExportResult(1, new CloudFile(cloudFile.fileId, "export.csv")))
        loanService.disburseLoan(new DisburseLoanCommand(disbursementId: holder.disbursementId))
    }

    Long settleDisbursement(LoanHolder holder, LocalDate valueDate, BigDecimal amount) {
        def paymentId = paymentService.addPayment(new AddPaymentCommand(accountId: institutionAccountId, paymentType: PaymentType.OUTGOING, valueDate: valueDate, postedAt: valueDate.atStartOfDay(), amount: amount, details: "", reference: RandomStringUtils.randomAlphabetic(8), key: RandomStringUtils.randomAlphabetic(8)))
        loanService.settleDisbursement(new SettleDisbursementCommand(disbursementId: holder.disbursementId, paymentId: paymentId, amount: amount))
    }

    def applyAndDisburse(LoanHolder holder) {
        submitApplication(holder)
        updateOffer(holder)
        issueLoan(holder)
        disburse(holder)
        loanService.getLoan(holder.loanId).status == LoanStatus.OPEN
        return holder
    }

    void applyAndDisburseAndRepay(LoanHolder holder) {
        applyAndDisburse(holder)
        repayLoan(holder, holder.issueDate, holder.offeredPrincipal + holder.offeredInterest)
    }

    Balance loanBalance(LoanHolder holder) {
        return transactionService.getBalance(BalanceQuery.byLoan(holder.loanId))
    }

    List<Long> repayLoan(LoanHolder holder, LocalDate valueDate, BigDecimal amount) {
        def paymentId = addPayment(valueDate, amount)
        return loanService.repayLoan(new RepayLoanCommand(loanId: holder.loanId, paymentId: paymentId, paymentAmount: amount, comments: "test"))
    }

    long addPayment(LocalDate valueDate, BigDecimal amount) {
        paymentService.addPayment(new AddPaymentCommand(accountId: institutionAccountId, paymentType: PaymentType.INCOMING, valueDate: valueDate, postedAt: valueDate.atStartOfDay(), amount: amount, details: "", reference: RandomStringUtils.randomAlphabetic(8), key: RandomStringUtils.randomAlphabetic(8)))
    }

    private IssueLoanCommand buildIssueLoanCommand(LoanHolder holder) {
        new IssueLoanCommand(loanApplicationId: holder.applicationId, loanNumber: holder.number, issueDate: holder.issueDate)
    }

    private LoanApplicationOfferCommand buildLoanApplicationOfferCommand(LoanHolder holder) {
        new LoanApplicationOfferCommand(id: holder.applicationId, principal: holder.offeredPrincipal, interest: holder.offeredInterest, offerDate: holder.offerDate, nominalApr: 120.00, effectiveApr: 213.84, periodCount: holder.requestedMonths, periodUnit: PeriodUnit.MONTH, discountId: holder.discountId)
    }

    private SubmitLoanApplicationCommand buildSubmitLoanApplicationCommand(LoanHolder holder) {
        long loansPaid = loanService.findLoans(LoanQuery.paidLoans(holder.clientId)).size()
        new SubmitLoanApplicationCommand(clientId: holder.clientId, applicationNumber: holder.number, productId: PRODUCT_ID, principal: holder.requestedPrincipal, submittedAt: holder.applicationDate, periodCount: holder.requestedMonths, periodUnit: PeriodUnit.MONTH, discountId: holder.discountId, invoiceDay: holder.invoicePaymentDate, loansPaid: loansPaid, promoCodeId: holder.promoCodeId)
    }

    private void institutions() {
        institutionId = institutionService.addInstitution(new AddInstitutionCommand(
            name: "Bank X",
            institutionType: "Bank",
            primary: true,
            accounts: [new AddInstitutionCommand.Account(accountNumber: "1", accountingAccountCode: "2620", primary: true)]
        ))
        institutionAccountId = institutionService.getInstitution(institutionId).accounts[0].id
    }

    private void createProduct() {
        def entity = new ProductEntity()
        entity.id = PRODUCT_ID
        entity.productType = ProductType.LINE_OF_CREDIT
        entity.defaultSettingsJson = JsonUtils.writeValueAsString(createProductSettings())
        productRepository.saveAndFlush(entity).id

        loanRegistry.addRepaymentStrategy(PRODUCT_ID, invoiceItemBasedRepaymentStrategy)
        loanRegistry.addDisbursementStrategy(PRODUCT_ID, creditLineDisbursementStrategy)
        loanRegistry.addBreakLoanStrategy(PRODUCT_ID, breakLoanStrategy)
        loanRegistry.addLoanIssueStrategy(PRODUCT_ID, creditLineLoanIssueStrategy)
        loanRegistry.addLoanDerivedValueResolver(PRODUCT_ID, testLoanDerivedValuesResolver)
    }

    @Transactional
    void updateProductSettings(CreditLineProductSettings settings) {
        def productEntity = productRepository.getRequired(PRODUCT_ID)
        productEntity.defaultSettingsJson = JsonUtils.writeValueAsString(settings)
    }

    CreditLineProductSettings getProductSettings() {
        return productService.getSettings(PRODUCT_ID, CreditLineProductSettings.class)
    }

    private CreditLineProductSettings createProductSettings() {
        def pricingSettings = new CreditLinePricingSettings(
            interestRatePerYearPercent: [new CreditLineInterestSettings(startDate: DateUtils.farFarInPast(), ratePerYearPercent: 80.00)],
            servicingFeeRatePerYearPercent: 80.00,
            firstDisbursementFeePercent: 1,
            disbursementFeePercent: 2
        )
        def offerSettings = new CreditLineOfferSettings(
            minAmount: 300.00,
            maxAmount: 1000.00,
            amountStep: 50.00,
            defaultAmount: 500.00,
        )
        def invoiceSettings = new CreditLineInvoiceSettings(
            dueDays: 9,
            minAmount: 50.00,
            principalAmountPercentage: 15.00,
            minPeriodDays: 14
        )
        return new CreditLineProductSettings(
            pricingSettings: pricingSettings,
            offerSettings: offerSettings,
            invoiceSettings: invoiceSettings,
            repaymentSettings: new CreditLineRepaymentSettings(
                outstandingWriteOffAmount: 1.00,
                invoiceDistributionOrder: [
                    typeSubType(InvoiceItemType.FEE, "DISBURSEMENT_FEE"),
                    type(InvoiceItemType.PENALTY),
                    typeSubType(InvoiceItemType.FEE, "SERVICING_FEE"),
                    type(InvoiceItemType.INTEREST),
                    type(InvoiceItemType.PRINCIPAL)],
                brokenLoanDistributionOrder: [
                    orderTypeSubType(TRANSACTION_POSITION_FEE, "DISBURSEMENT_FEE"),
                    orderType(TRANSACTION_POSITION_PENALTY),
                    orderType(TRANSACTION_POSITION_INTEREST),
                    orderTypeSubType(TRANSACTION_POSITION_FEE, "SERVICING_FEE"),
                    orderType(TRANSACTION_POSITION_PRINCIPAL)
                ]
            )
        )
    }

    Long createFirstInvoice(LoanHolder loanHolder, items) {
        invoiceService.createInvoice(new GeneratedInvoice(
            invoiceDate: loanHolder.issueDate.plusDays(31),
            periodFrom: loanHolder.issueDate,
            periodTo: loanHolder.issueDate.plusDays(30),
            number: "random",
            dueDate: loanHolder.issueDate.plusDays(60),
            loanId: loanHolder.loanId,
            productId: CreditLineLoanHelper.PRODUCT_ID,
            clientId: loanHolder.clientId,
            items: items
        ))
    }

    Long createSecondInvoice(LoanHolder loanHolder, items) {
        invoiceService.createInvoice(new GeneratedInvoice(
            invoiceDate: loanHolder.issueDate.plusDays(61),
            periodFrom: loanHolder.issueDate.plusDays(31),
            periodTo: loanHolder.issueDate.plusDays(60),
            number: randomAlphanumeric(8),
            dueDate: loanHolder.issueDate.plusDays(90),
            loanId: loanHolder.loanId,
            productId: CreditLineLoanHelper.PRODUCT_ID,
            clientId: loanHolder.clientId,
            items: items
        ))
    }

    void closePeriodSilently(period) {
        txTemplate.execute {
            periodRepository.saveAndFlush(new PeriodEntity(periodDate: period, closeDate: LocalDate.now(), status: PeriodStatus.CLOSED))
        }
    }

    void applyInterest(LoanHolder loanHolder, LocalDate date, BigDecimal amount) {
        loanService.applyInterest(new ApplyInterestCommand(loanId: loanHolder.loanId, amount: amount, valueDate: date))
    }

    void applyFee(LoanHolder loanHolder, LocalDate date, BigDecimal amount) {
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: amount, valueDate: date))
    }

    void updateLoanStatus(LoanHolder loanHolder, LoanStatus loanStatus, LoanStatusDetail loanStatusDetail, LocalDate closeDate) {
        txTemplate.execute {
            def loan = loanRepository.getRequired(loanHolder.loanId)
            loan.status = loanStatus
            loan.statusDetail = loanStatusDetail
            loan.closeDate = closeDate
        }
    }
}
