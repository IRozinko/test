package fintech.spain.alfa.product.testing;

import com.google.common.base.Preconditions;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.dc.DcService;
import fintech.dc.commands.RecoverExternalCommand;
import fintech.dc.db.DebtRepository;
import fintech.dc.model.Debt;
import fintech.filestorage.CloudFile;
import fintech.lending.core.loan.*;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import fintech.lending.core.loan.commands.SettleDisbursementCommand;
import fintech.lending.core.loan.commands.WriteOffAmountCommand;
import fintech.lending.core.promocode.CreatePromoCodeCommand;
import fintech.lending.core.promocode.PromoCodeService;
import fintech.lending.core.promocode.db.PromoCodeRepository;
import fintech.payments.DisbursementService;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementExportResult;
import fintech.payments.model.DisbursementStatusDetail;
import fintech.spain.alfa.product.lending.LoanPrepayment;
import fintech.spain.alfa.product.lending.LoanReschedulingService;
import fintech.spain.alfa.product.lending.LoanServicingFacade;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import fintech.spain.dc.command.BreakReschedulingCommand;
import fintech.spain.dc.command.RepurchaseDebtCommand;
import fintech.spain.dc.command.RescheduleCommand;
import fintech.spain.dc.command.ReschedulingPreviewCommand;
import fintech.spain.dc.model.ReschedulingPreview;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.dc.DcFacade;
import fintech.spain.alfa.product.extension.ApplyAndRepayExtensionFeeCommand;
import fintech.spain.alfa.product.extension.ExtensionService;
import fintech.spain.alfa.product.lending.penalty.PenaltyService;
import fintech.strategy.model.ExtensionOffer;
import fintech.transactions.*;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.isPositive;
import static fintech.TimeMachine.today;
import static fintech.crm.attachments.ClientAttachmentService.AttachmentQuery.byLoan;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Accessors(chain = true)
public class TestLoan {

    @Getter
    private final TestClient testClient;

    @Getter
    private final Long loanId;

    @Autowired
    private LoanService loanService;

    @Autowired
    private DisbursementService disbursementService;

    @Autowired
    private ExtensionService extensionService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UnderwritingFacade underwritingFacade;

    @Autowired
    private LoanServicingFacade loanServicingFacade;

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private DcFacade dcFacade;

    @Autowired
    private DcService dcService;

    @Autowired
    private ClientAttachmentService clientAttachmentService;

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private PromoCodeService promoCodeService;

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @Autowired
    private TransactionTemplate tx;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private LoanReschedulingService loanReschedulingService;

    private Long debtId;

    public TestLoan(TestClient testClient, Long loanId) {
        this.testClient = testClient;
        this.loanId = loanId;
    }

    public Loan getLoan() {
        return loanService.getLoan(loanId);
    }

    public Debt getDebt() {
        return debtRepository.findOne(fintech.dc.db.Entities.debt.loanId.eq(loanId)).toValueObject();
    }

    public TestLoan exportDisbursements(LocalDate when) {
        List<Disbursement> disbursements = disbursementService.findDisbursements(DisbursementService.DisbursementQuery.byLoan(loanId, DisbursementStatusDetail.PENDING));
        disbursements.forEach(d -> disbursementService.exported(d.getId(), when.atStartOfDay(), new DisbursementExportResult(1, new CloudFile())));
        return this;
    }

    public TestLoan exportDisbursements(LocalDate when, String fileName) {
        List<Disbursement> disbursements = disbursementService.findDisbursements(DisbursementService.DisbursementQuery.byLoan(loanId, DisbursementStatusDetail.PENDING));
        disbursements.forEach(d -> disbursementService.exported(d.getId(), when.atStartOfDay(), new DisbursementExportResult(1, new CloudFile(null, fileName))));
        return this;
    }

    public TestLoan settleDisbursements(LocalDate when) {
        List<Disbursement> disbursements = disbursementService.findDisbursements(DisbursementService.DisbursementQuery.byLoan(loanId, DisbursementStatusDetail.EXPORTED));
        disbursements.forEach(d -> {
            Long paymentId = TestFactory.payments().newOutgoingPayment(d.getAmount(), when).getPaymentId();
            loanService.settleDisbursement(new SettleDisbursementCommand()
                .setDisbursementId(d.getId())
                .setAmount(d.getAmount())
                .setPaymentId(paymentId)
            );
        });
        return this;
    }

    public TestLoan repayAll(LocalDate when) {
        Loan loan = getLoan();
        BigDecimal amount = loan.getTotalDue();
        return repay(amount, when);
    }

    public TestLoan repay(BigDecimal amount, LocalDate when) {
        Validate.isTrue(isPositive(amount), "No amount specified");
        Loan loan = getLoan();
        Long paymentId = TestFactory.payments().newIncomingPayment(amount, when).getPaymentId();
        loanService.repayLoan(new RepayLoanCommand()
            .setLoanId(loan.getId())
            .setPaymentId(paymentId)
            .setValueDate(when)
            .setPaymentAmount(amount)
        );
        loanService.resolveLoanDerivedValues(loan.getId(), when);
        return this;
    }

    public TestLoan repayUsingOverpayment(BigDecimal amount, LocalDate when) {
        Loan loan = getLoan();
        loanService.repayLoan(new RepayLoanCommand()
            .setLoanId(loan.getId())
            .setValueDate(when)
            .setOverpaymentAmount(amount)
        );
        loanService.resolveLoanDerivedValues(loan.getId(), when);
        return this;
    }

    public Optional<ExtensionOffer> findExtensionOffer(BigDecimal price, LocalDate date) {
        return extensionService.findOfferForLoan(getLoanId(), price, true, date);
    }

    public List<ExtensionOffer> listExtensionOffers(LocalDate date) {
        return extensionService.listOffersForLoan(getLoanId(), date);
    }

    public TestLoan extendOrRepay(BigDecimal amount, LocalDate paymentDate) {
        if (findExtensionOffer(amount, paymentDate).isPresent()) {
            extend(amount, paymentDate);
        } else {
            repay(amount, paymentDate);
        }
        return this;
    }

    public TestLoan extend(BigDecimal amount, LocalDate paymentDate) {
        Long paymentId = TestFactory.payments().newIncomingPayment(amount, paymentDate).getPaymentId();
        Loan loan = getLoan();
        Optional<ExtensionOffer> offer = findExtensionOffer(amount, paymentDate);
        Validate.isTrue(offer.isPresent(), "Extension offer not found");
        extensionService.applyAndRepayExtensionFee(new ApplyAndRepayExtensionFeeCommand()
            .setLoanId(loan.getId())
            .setPaymentId(paymentId)
            .setPaymentAmount(amount)
            .setExtensionOffer(offer.get())
        );
        loanService.resolveLoanDerivedValues(loan.getId(), paymentDate);
        return this;
    }

    public TestLoan extendUsingOverpayment(BigDecimal amount, LocalDate paymentDate) {
        Loan loan = getLoan();
        Optional<ExtensionOffer> offer = findExtensionOffer(amount, paymentDate);
        Validate.isTrue(offer.isPresent(), "Extension offer not found");
        extensionService.applyAndRepayExtensionFee(new ApplyAndRepayExtensionFeeCommand()
            .setLoanId(loan.getId())
            .setOverpaymentAmount(amount)
            .setValueDate(paymentDate)
            .setExtensionOffer(offer.get())
        );
        loanService.resolveLoanDerivedValues(loan.getId(), paymentDate);
        return this;
    }

    public TestLoan applyPenalty(LocalDate date) {
        penaltyService.applyPenalty(getLoanId(), date);
        loanService.resolveLoanDerivedValues(getLoanId(), date);
        return this;
    }

    public TestLoan applyPenalty(LocalDate from, LocalDate to) {
        penaltyService.applyPenalty(loanId, from, to);
        loanService.resolveLoanDerivedValues(getLoanId(), to);
        return this;
    }

    public TestLoan writeOff(LocalDate date, BigDecimal principal, BigDecimal interest) {
        loanService.writeOffAmount(new WriteOffAmountCommand()
            .setLoanId(getLoanId())
            .setWhen(date)
            .setPrincipal(principal)
            .setInterest(interest)
        );
        loanService.resolveLoanDerivedValues(getLoanId(), date);
        return this;
    }

    public TestLoan writeOffPenalty(LocalDate date, BigDecimal amount) {
        loanService.writeOffAmount(new WriteOffAmountCommand()
            .setLoanId(getLoanId())
            .setWhen(date)
            .setPenalty(amount)
        );
        loanService.resolveLoanDerivedValues(getLoanId(), date);
        return this;
    }

    public LocalDate getGracePeriodEndDate() {
        return getLoan().getMaturityDate().plusDays(AlfaConstants.GRACE_PERIOD_IN_DAYS);
    }

    public static BigDecimal expectedExtensionPrice(BigDecimal principal, int days) {
        switch (days) {
            case 7:
                return principal.multiply(amount(0.1));
            case 14:
                return principal.multiply(amount(0.14));
            case 30:
                return principal.multiply(amount(0.26));
            case 45:
                return principal.multiply(amount(0.35));
            default:
                throw new IllegalArgumentException("Unsupported extension days " + days);
        }
    }

    public LoanStatusDetail getStatusDetail() {
        return getLoan().getStatusDetail();
    }

    public LoanStatus getStatus() {
        return getLoan().getStatus();
    }

    public BigDecimal getOverpaymentReceived() {
        return getLoan().getOverpaymentReceived();
    }

    public LocalDate getCloseDate() {
        return getLoan().getCloseDate();
    }

    public TestClient toClient() {
        return this.testClient;
    }

    public TestLoan updateDerivedValues(LocalDate onDate) {
        loanService.resolveLoanDerivedValues(this.loanId, onDate);
        return this;
    }

    public Balance getBalance() {
        return transactionService.getBalance(TransactionQuery.byLoan(getLoanId()));
    }

    public List<Installment> openInstallments() {
        return scheduleService.findInstallments(InstallmentQuery.openInstallments(loanId));
    }

    public List<Installment> allInstallments() {
        return scheduleService.findInstallments(InstallmentQuery.allLoanInstallments(loanId));
    }

    public List<Transaction> transactions() {
        return transactionService.findTransactions(TransactionQuery.byLoan(loanId));
    }

    public boolean isPaid() {
        return getLoan().getStatusDetail() == LoanStatusDetail.PAID;
    }


    public LoanPrepayment calculatePrepayment(LocalDate date) {
        return loanServicingFacade.calculatePrepayment(getLoanId(), date);
    }

    public TestLoan renounce(LocalDate date) {
        loanServicingFacade.renounceLoan(getLoanId(), date);
        return this;
    }

    public TestLoan postToDc() {
        this.debtId = dcFacade.postLoan(getLoanId(), null, null,true).orElse(null);
        return this;
    }

    public TestLoan externalizeDebt() {
        dcFacade.externalize(debtId, "TEIDE");
        return this;
    }

    public TestLoan sellDebt() {
        dcFacade.sell(debtId, "TEIDE");
        return this;
    }

    public TestLoan recoverDebt() {
        dcFacade.recoverExternal(new RecoverExternalCommand()
            .setDebtId(debtId)
            .setStatus("NoStatus")
            .setAutoAssign(true));
        return this;
    }


    public TestLoan repurchaseDebt(String portfolio) {
        dcFacade.repurchase(new RepurchaseDebtCommand()
            .setDebtId(debtId)
            .setStatus("NoStatus")
            .setPortfolio(portfolio)
            .setAutoAssign(true));
        return this;
    }

    public TestLoan triggerDcActions() {
        dcService.triggerActions(Preconditions.checkNotNull(debtId, "Loan not in DC"));
        return this;
    }

    public TestLoan changeDebtStatus(String status) {
        tx.execute(t -> {
            debtRepository.getRequired(getDebt().getId()).setStatus(status);
            return 1;
        });
        return this;
    }

    public TestLoan voidTransactions(TransactionQuery txQuery) {
        txQuery.setLoanId(loanId);
        transactionService.findTransactions(txQuery).forEach(tx -> transactionService
            .voidTransaction(new VoidTransactionCommand(tx.getId(), today(), today())));
        return this;
    }

    public List<Transaction> allTransactions() {
        return transactionService.findTransactions(TransactionQuery.byLoan(loanId));
    }

    public TestLoan sellLoan() {
        // TODO Should be replaced with service method when implemented
        AddTransactionCommand command = new AddTransactionCommand();
        command.setTransactionType(TransactionType.SOLD_LOAN);
        command.setPostDate(today());
        command.setValueDate(today());
        command.setBookingDate(today());
        command.setLoanId(loanId);
        transactionService.addTransaction(command);
        loanService.resolveLoanDerivedValues(loanId, today());
        return this;
    }

    public TestLoan reschedule(RescheduleCommand command) {
        command.setLoanId(loanId);
        changeDebtStatus("RescheduleOffered");
        dcFacade.reschedule(new RescheduleCommand()
            .setLoanId(loanId)
            .setPreview(command.getPreview())
            .setWhen(command.getWhen()));
        updateDerivedValues(TimeMachine.today());
        return this;
    }

    public TestLoan breakRescheduling(LocalDate when) {
        dcFacade.breakRescheduling(new BreakReschedulingCommand().setLoanId(loanId).setWhen(when));
        return this;
    }

    public Contract contract() {
        return scheduleService.getCurrentContract(loanId);
    }

    public List<Contract> contracts() {
        return scheduleService.getContracts(loanId);
    }

    public TestLoan reschedule() {
        ReschedulingPreview.Item item1 = new ReschedulingPreview.Item();
        item1.setInstallmentSequence(1L);
        item1.setPeriodFrom(TimeMachine.today());
        item1.setPeriodTo(TimeMachine.today().plusDays(2));
        item1.setDueDate(TimeMachine.today().plusDays(2));
        item1.setPrincipalScheduled(amount(100));
        item1.setInterestScheduled(amount(20));
        item1.setPenaltyScheduled(amount(70));
        item1.setTotalScheduled(amount(190));

        ReschedulingPreview.Item item2 = new ReschedulingPreview.Item();
        item2.setInstallmentSequence(2L);
        item2.setPeriodFrom(TimeMachine.today());
        item2.setPeriodTo(TimeMachine.today().plusDays(2));
        item2.setDueDate(TimeMachine.today().plusDays(2));
        item2.setPrincipalScheduled(amount(100));
        item2.setInterestScheduled(amount(15));
        item2.setPenaltyScheduled(amount(70));
        item2.setTotalScheduled(amount(185));

        dcFacade.reschedule(new RescheduleCommand()
            .setLoanId(loanId)
            .setWhen(today())
            .setPreview(new ReschedulingPreview().setPeriodFrom(null).setPeriodTo(null).setItems(newArrayList(item1, item2)))
        );

        return this;
    }

    public Optional<Installment> firstInstallment() {
        return scheduleService.findInstallments(InstallmentQuery.openInstallments(loanId)).stream()
            .min(Comparator.comparing(Installment::getInstallmentSequence));
    }

    public int attachmentCount(String type) {
        return clientAttachmentService
            .findAttachments(byLoan(loanId, type))
            .size();
    }

    public ReschedulingPreview generateReschedulePreview(ReschedulingPreviewCommand command) {
        command.setLoanId(loanId);
        return dcFacade.generateReschedulePreview(command);
    }

    public TestUpsellWorkflow issueUpsell() {
        return TestFactory.upsellWorkflow(testClient, underwritingFacade.startUpsellWorkflow(testClient.getClientId(), loanId));
    }

    public TestLoan voidLoan() {
        VoidLoanCommand command = new VoidLoanCommand();
        command.setLoanId(loanId);
        command.setVoidDate(TimeMachine.today());
        loanService.voidLoan(command);
        return this;
    }

    public TestLoan resolveDerivedValues() {
        loanService.resolveLoanDerivedValues(loanId, TimeMachine.today());
        return this;
    }

    public TestLoan addPromoCodeForRepeaters(String promoCode, BigDecimal discountRate, LocalDate activeFrom, LocalDate activeTo) {
        promoCodeService.create(new CreatePromoCodeCommand()
            .setCode(createPromoCodePostfix(promoCode))
            .setDescription("Demo promo code")
            .setEffectiveFrom(activeFrom)
            .setEffectiveTo(activeTo)
            .setRateInPercent(discountRate)
            .setMaxTimesToApply(10L)
            .setClientNumbers(Collections.singletonList(testClient.getClient().getNumber())));
        return this;
    }

    public TestLoan addPromoCode(String promoCode, BigDecimal discountRate, LocalDate activeFrom, LocalDate activeTo) {
        promoCodeService.create(new CreatePromoCodeCommand()
            .setCode(createPromoCodePostfix(promoCode))
            .setDescription("Demo promo code")
            .setEffectiveFrom(activeFrom)
            .setEffectiveTo(activeTo)
            .setRateInPercent(discountRate)
            .setMaxTimesToApply(100L));
        return this;
    }

    private String createPromoCodePostfix(String promoCode) {
        return promoCode + promoCodeRepository.count();
    }

}
