package fintech.lending.core.loan.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import fintech.BigDecimalUtils;
import fintech.PredicateBuilder;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.AttachmentConstants;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.filestorage.CloudFile;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.commands.ApproveLoanApplicationCommand;
import fintech.lending.core.invoice.commands.GenerateInvoiceCommand;
import fintech.lending.core.invoice.spi.InvoicingStrategy;
import fintech.lending.core.loan.*;
import fintech.lending.core.loan.commands.*;
import fintech.lending.core.loan.db.LoanEntity;
import fintech.lending.core.loan.db.LoanRepository;
import fintech.lending.core.loan.events.IssueLoanEvent;
import fintech.lending.core.loan.events.LoanDisbursedEvent;
import fintech.lending.core.loan.events.LoanPaymentEvent;
import fintech.lending.core.loan.events.LoanVoidedEvent;
import fintech.lending.core.loan.spi.DisbursementStrategy;
import fintech.lending.core.loan.spi.LoanIssueStrategy;
import fintech.lending.core.loan.spi.LoanRegistry;
import fintech.lending.core.loan.spi.RepaymentStrategy;
import fintech.lending.core.util.TransactionBuilder;
import fintech.payments.DisbursementConstants;
import fintech.payments.DisbursementService;
import fintech.payments.InstitutionService;
import fintech.payments.PaymentService;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementStatusDetail;
import fintech.payments.model.InstitutionAccount;
import fintech.payments.model.Payment;
import fintech.transactions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.max;
import static fintech.lending.core.db.Entities.loan;
import static fintech.lending.core.loan.InstallmentQuery.openInstallments;
import static fintech.transactions.TransactionQuery.byLoan;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
class LoanServiceBean implements LoanService {

    private final LoanRepository loanRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;
    private final LoanRegistry loanRegistry;
    private final DisbursementService disbursementService;
    private final LoanApplicationService loanApplicationService;
    private final ScheduleService scheduleService;
    private final PaymentService paymentService;
    private final InstitutionService institutionService;
    private final ClientAttachmentService clientAttachmentService;

    @Override
    public Long issueLoan(IssueLoanCommand command) {
        LoanApplication application = loanApplicationService.get(command.getLoanApplicationId());
        LoanIssueStrategy loanIssueStrategy = loanRegistry.getLoanIssueStrategy(application.getProductId());

        return loanIssueStrategy.issue(command);
    }
    @Override
    public Long issueLoan(CreateLoanCommand command) {
        LoanIssueStrategy loanIssueStrategy = loanRegistry.getLoanIssueStrategy(command.getProductId());
        return loanIssueStrategy.issue(command);
    }

    @Override
    public void updateStrategies(UpdateStrategiesCommand command) {
        LoanEntity loanEntity = loanRepository.getRequired(command.getLoanId());
        loanEntity.setInterestStrategyId(command.getInterestStrategyId());
        loanEntity.setFeeStrategyId(command.getFeeStrategyId());
        loanEntity.setPenaltyStrategyId(command.getPenaltyStrategyId());
    }

    @EventListener
    public void processLoan(IssueLoanEvent event) {
        CreateLoanCommand command = event.getCreateLoanCommand();
        boolean loanExists = findLoans(LoanQuery.allLoansByNumber(command.getLoanNumber())).size()>0;
        if (!loanExists) {
            issueLoan(command);
        } else {
            log.warn("Skip creating loan");
        }
    }
    @Override
    public void withdraw(@Valid WithdrawCommand command) {
        LoanApplication application = loanApplicationService.get(command.getLoanApplicationId());
        LoanEntity loanEntity = loanRepository.getRequired(application.getLoanId());
        log.info("Withdrawing principal [{}] on [{}] for [{}]", application.getRequestedPrincipal(), command.getDate(), loanEntity);

        loanEntity.setLoanApplicationId(application.getId());

        ApproveLoanApplicationCommand approvedCommand = new ApproveLoanApplicationCommand();
        approvedCommand.setId(command.getLoanApplicationId());
        approvedCommand.setApproveDate(command.getDate());
        approvedCommand.setLoanId(loanEntity.getId());
        loanApplicationService.approve(approvedCommand);

        if (loanEntity.getStatusDetail() == LoanStatusDetail.ISSUED) {
            loanEntity.setStatusDetail(LoanStatusDetail.DISBURSING);
        }
    }

    @Override
    public List<Loan> findLoans(LoanQuery query) {
        query.orDefaultOrder(loan.issueDate.asc(), loan.number.asc());
        List<LoanEntity> entities = loanRepository.findAll(toPredicates(query).allOf(), query.orders());
        return entities.stream().map(LoanEntity::toValueObject).collect(Collectors.toList());
    }

    @Override
    public Optional<Loan> findLastLoan(LoanQuery query) {
        return loanRepository.findFirst(toPredicates(query).allOf(), loan.id.desc()).map(LoanEntity::toValueObject);
    }

    @Override
    public Optional<Loan> findLastLoanByIssueDate(LoanQuery query) {
        return loanRepository.findFirst(toPredicates(query).allOf(), loan.issueDate.desc()).map(LoanEntity::toValueObject);
    }

    private PredicateBuilder toPredicates(LoanQuery query) {
        return new PredicateBuilder()
            .addIfPresent(query.getClientId(), loan.clientId::eq)
            .addIfPresent(query.getLoanNumber(), loan.number::eq)
            .addIfPresent(query.getStatuses(), loan.status::in)
            .addIfPresent(query.getStatusDetails(), loan.statusDetail::in)
            .addIfPresent(query.getExcludeStatusDetails(), loan.statusDetail::notIn)
            .addIfPresent(query.getIssueDateTo(), loan.issueDate::loe)
            .addIfPresent(query.getIssueDateFrom(), loan.issueDate::goe)
            .addIfPresent(query.getMaturityDateTo(), loan.maturityDate::loe)
            .addIfPresent(query.getMaturityDateFrom(), loan.maturityDate::goe);
    }

    @Override
    public Loan getLoan(Long loanId) {
        LoanEntity entity = loanRepository.getRequired(loanId);
        return entity.toValueObject();
    }

    @Override
    public void voidLoan(VoidLoanCommand command) {
        Validate.notNull(command.getLoanId(), "No loan id");
        Loan loan = getLoan(command.getLoanId());
        log.info("Voiding Loan: [{}]", command.getLoanId());

        Balance balance = transactionService.getBalance(byLoan(command.getLoanId()));

        Validate.isZero(balance.getPrincipalPaid(), "Principal has already been paid");
        Validate.isZero(balance.getFeePaid(), "Fee has already been paid");
        Validate.isZero(balance.getInterestPaid(), "Interest has already been paid");
        Validate.isZero(balance.getPenaltyPaid(), "Penalty has already been paid");
        Validate.isZero(balance.getTotalOutstanding(), "Loan has outstanding amount");

        List<Disbursement> pendingDisbursements = disbursementService.findDisbursements(DisbursementService.DisbursementQuery.byLoan(command.getLoanId(), DisbursementStatusDetail.PENDING));
        pendingDisbursements.forEach(d -> disbursementService.cancel(d.getId(), "LoanVoided"));

        List<Disbursement> exportedDisbursements = disbursementService.findDisbursements(DisbursementService.DisbursementQuery.byLoan(command.getLoanId(), DisbursementStatusDetail.EXPORTED));
        Validate.isTrue(exportedDisbursements.isEmpty(), "Can't void loan, there are [%s] exported disbursements", exportedDisbursements.size());

        List<Disbursement> settledDisbursements = disbursementService.findDisbursements(DisbursementService.DisbursementQuery.byLoan(command.getLoanId(), DisbursementStatusDetail.SETTLED));
        Validate.isTrue(settledDisbursements.isEmpty(), "Can't void loan, there are [%s] settled disbursements", settledDisbursements.size());


        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setValueDate(command.getVoidDate());
        tx.setTransactionType(TransactionType.VOID_LOAN);
        transactionBuilder.addLoanValues(loan, tx);
        transactionService.addTransaction(tx);

        log.info("Loan is voided: [{}]", command.getLoanId());
        eventPublisher.publishEvent(new LoanVoidedEvent(loan));
    }

    @Override
    public List<Long> repayLoan(RepayLoanCommand command) {
        LoanEntity loanEntity = loanRepository.getRequired(command.getLoanId());
        Validate.isTrue(loanEntity.getStatus() == LoanStatus.OPEN,
            "Can't repay loan [%s] with status [%s]", loanEntity.getId(), loanEntity.getStatus());
        RepaymentStrategy repaymentStrategy = loanRegistry.getRepaymentStrategy(loanEntity.toValueObject());
        List<Long> txs = repaymentStrategy.repay(command);
        eventPublisher.publishEvent(new LoanPaymentEvent(command.getLoanId(), command.getValueDate(), command.getPaymentId()));
        return txs;
    }

    @Override
    public Long disburseLoan(DisburseLoanCommand command) {
        Disbursement disbursement = disbursementService.getDisbursement(command.getDisbursementId());

        Preconditions.checkState(DisbursementConstants.DISBURSEMENT_TYPE_PRINCIPAL.equals(disbursement.getDisbursementType()));

        LoanEntity loanEntity = loanRepository.getRequired(disbursement.getLoanId());
        DisbursementStrategy disbursementStrategy = loanRegistry.getDisbursementStrategy(loanEntity.toValueObject());
        Long txId = disbursementStrategy.disburse(command);
        loanRegistry.getLoanDerivedValueResolver(loanEntity.getProductId()).resolveDerivedValues(loanEntity.getId());
        eventPublisher.publishEvent(new LoanDisbursedEvent(getLoan(disbursement.getLoanId())));
        return txId;
    }

    @Override
    public Long applyInterest(@Valid ApplyInterestCommand command) {
        Loan loan = getLoan(command.getLoanId());

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setValueDate(command.getValueDate());
        tx.setInterestApplied(command.getAmount());
        tx.setTransactionType(TransactionType.APPLY_INTEREST);
        transactionBuilder.addLoanValues(loan, tx);

        return transactionService.addTransaction(tx);
    }

    @Override
    public Long applyPenalty(@Valid ApplyPenaltyCommand command) {
        Loan loan = getLoan(command.getLoanId());

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setValueDate(command.getValueDate());
        tx.setPenaltyApplied(command.getAmount());
        tx.setPenaltyInvoiced(command.getAmountInvoiced());
        tx.setTransactionType(TransactionType.APPLY_PENALTY);
        tx.setTransactionSubType(command.getSubType());
        tx.setInvoiceId(command.getInvoiceId());
        tx.setInstallmentId(command.getInstallmentId());
        tx.setComments(command.getComments());
        transactionBuilder.addLoanValues(loan, tx);
        return transactionService.addTransaction(tx);
    }

    @Override
    public Long applyFee(@Valid ApplyFeeCommand command) {
        LoanEntity loan = loanRepository.getRequired(command.getLoanId());
        loan.setFeeApplied(command.getAmount());
        loanRepository.saveAndFlush(loan);

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.APPLY_FEE);
        tx.setTransactionSubType(command.getSubType());
        tx.setValueDate(command.getValueDate());
        tx.addEntry(new AddTransactionCommand.TransactionEntry()
            .setType(TransactionEntryType.FEE)
            .setSubType(command.getSubType())
            .setAmountApplied(command.getAmount())
        );
        transactionBuilder.addLoanValues(loan.toValueObject(), tx);
        return transactionService.addTransaction(tx);
    }

    @Override
    public Optional<Loan> findLoanByNumber(String loanNumber) {
        return loanRepository.getOptional(loan.number.eq(loanNumber)).map(LoanEntity::toValueObject);
    }

    @Override
    public Long settleDisbursement(@Valid SettleDisbursementCommand command) {
        Payment payment = paymentService.getPayment(command.getPaymentId());
        InstitutionAccount account = institutionService.getAccount(payment.getAccountId());
        Disbursement disbursement = disbursementService.getDisbursement(command.getDisbursementId());
        Loan loan = getLoan(disbursement.getLoanId());

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setCashOut(command.getAmount());
        tx.setTransactionType(TransactionType.DISBURSEMENT_SETTLEMENT);
        tx.setDisbursementId(command.getDisbursementId());
        tx.setComments(command.getComments());
        transactionBuilder.addPaymentValues(account, payment, tx);
        transactionBuilder.addLoanValues(loan, tx);

        return transactionService.addTransaction(tx);
    }

    @Override
    public Long generateInvoice(@Valid GenerateInvoiceCommand command) {
        LoanEntity loanEntity = loanRepository.getRequired(command.getLoanId());
        InvoicingStrategy invoicingStrategy = loanRegistry.getInvoicingStrategy(loanEntity.toValueObject());
        return invoicingStrategy.generateInvoice(command);
    }

    @Override
    public void breakLoan(@Valid BreakLoanCommand command) {
        LoanEntity loanEntity = loanRepository.getRequired(command.getLoanId());
        loanEntity.setReasonForBreak(command.getReasonForBreak());
        loanRegistry.getBreakLoanStrategy(loanEntity.toValueObject()).breakLoan(command);
        loanRegistry.getLoanDerivedValueResolver(loanEntity.getProductId()).resolveDerivedValues(loanEntity.getId());
    }

    @Override
    public void unBreakLoan(UnBreakLoanCommand command) {
        LoanEntity loanEntity = loanRepository.getRequired(command.getLoanId());
        loanRegistry.getBreakLoanStrategy(loanEntity.toValueObject()).unBreakLoan(command);
        loanRegistry.getLoanDerivedValueResolver(loanEntity.getProductId()).resolveDerivedValues(loanEntity.getId());
    }

    @Override
    public void writeOffAmount(@Valid WriteOffAmountCommand command) {
        Loan loan = getLoan(command.getLoanId());

        Balance balance = transactionService.getBalance(byLoan(command.getLoanId()));
        BigDecimal principalInvoicedAdjustment = BigDecimalUtils.min(balance.getPrincipalDue(), command.getPrincipal()).negate();
        BigDecimal interestInvoicedAdjustment = BigDecimalUtils.min(balance.getInterestDue(), command.getInterest()).negate();
        BigDecimal penaltyInvoicedAdjustment = BigDecimalUtils.min(balance.getPenaltyDue(), command.getPenalty()).negate();

        AddTransactionCommand transaction = new AddTransactionCommand();
        boolean hasOpenInstallments = !scheduleService.findInstallments(openInstallments(command.getLoanId())).isEmpty();
        if (hasOpenInstallments) {
            Installment installment = scheduleService.getFirstActiveInstallment(command.getLoanId());
            transaction.setInstallmentId(installment.getId());
            transaction.setInvoiceId(installment.getInvoiceId());
        }

        transaction.setTransactionType(TransactionType.WRITE_OFF);
        transaction.setValueDate(command.getWhen());
        transaction.setPrincipalWrittenOff(command.getPrincipal());
        transaction.setPrincipalInvoiced(principalInvoicedAdjustment);
        transaction.setInterestWrittenOff(command.getInterest());
        transaction.setInterestInvoiced(interestInvoicedAdjustment);
        transaction.setPenaltyWrittenOff(command.getPenalty());
        transaction.setPenaltyInvoiced(penaltyInvoicedAdjustment);
        transaction.setComments(command.getComments());
        transaction.setTransactionSubType(command.getSubType());
        if (BigDecimalUtils.isPositive(command.getFee())) {
            BigDecimal runningAmount = command.getFee();

            List<EntryBalance> feeEntriesBalance = transactionService.getEntryBalance(TransactionEntryQuery.byLoan(command.getLoanId(), TransactionEntryType.FEE));
            for (EntryBalance entryBalance : feeEntriesBalance) {
                if (!BigDecimalUtils.isPositive(runningAmount)) {
                    break;
                }

                BigDecimal feeInvoicedAdjustment = BigDecimalUtils.min(entryBalance.getAmountDue(), runningAmount).negate();
                BigDecimal amount = BigDecimalUtils.min(entryBalance.getAmountOutstanding(), runningAmount);

                transaction.addEntry(new AddTransactionCommand.TransactionEntry()
                    .setType(TransactionEntryType.FEE)
                    .setSubType(entryBalance.getSubType())
                    .setAmountWrittenOff(amount)
                    .setAmountInvoiced(feeInvoicedAdjustment)
                );

                runningAmount = runningAmount.subtract(amount);
            }
        }

        transactionBuilder.addLoanValues(loan, transaction);

        transactionService.addTransaction(transaction);
    }

    @Override
    public Long updateCreditLimit(@Valid UpdateCreditLimitCommand command) {
        Loan loan = getLoan(command.getLoanId());

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.UPDATE_CREDIT_LIMIT);
        tx.setValueDate(command.getValueDate());
        tx.setCreditLimit(command.getAmount());
        tx.setComments(command.getComments());
        transactionBuilder.addLoanValues(loan, tx);

        return transactionService.addTransaction(tx);
    }

    @Override
    public Long updateAvailableCreditLimit(UpdateAvailableCreditLimitCommand command) {
        Loan loan = getLoan(command.getLoanId());
        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.UPDATE_AVAILABLE_CREDIT_LIMIT);
        tx.setCreditLimitAvailable(command.getAmount());
        tx.setValueDate(command.getValueDate());
        transactionBuilder.addLoanValues(loan, tx);
        return transactionService.addTransaction(tx);
    }

    @Override
    public void resolveLoanDerivedValues(Long loanId, LocalDate when) {
        loanRegistry.getLoanDerivedValueResolver(getLoan(loanId).getProductId()).resolveDerivedValues(loanId,null, null, when);
    }

    @Override
    public void resolveLoanDerivedValues(Long loanId,String state, String status, LocalDate when) {
        loanRegistry.getLoanDerivedValueResolver(getLoan(loanId).getProductId()).resolveDerivedValues(loanId,state, status, when);
    }

    @Override
    public void validateLoanBalance(Long loanId, Balance balance) {
        loanRegistry.getLoanDerivedValueResolver(getLoan(loanId).getProductId()).validateLoanBalance(loanId, balance);
    }

    @Override
    public void extendMaturityDate(Long loanId, LocalDate when) {
        Loan loan = getLoan(loanId);
        Validate.isTrue(loan.getStatus() == LoanStatus.OPEN, "It's possible to extend maturity date only for OPEN loans");
        Validate.isTrue(loan.getStatusDetail() == LoanStatusDetail.ACTIVE
                || loan.getStatusDetail() == LoanStatusDetail.PAID
                || loan.getStatusDetail() == LoanStatusDetail.DISBURSING
                || loan.getStatusDetail() == LoanStatusDetail.ISSUED,
            "It's possible to extend maturity date only for ACTIVE or PAID or DISBURSING or ISSUED loans");
        Validate.isTrue(!loan.getMaturityDate().isAfter(when.plusDays(2)), "It's possible to extend the maturity date max 2 days in advance");

        LoanApplication loanApplication = loanApplicationService.get(loan.getApplicationId());
        //TODO for presto the period is always 365 days for every application, so even if the application is not the one used for issue the loan we are safe.
        // Is it correct? Why we are updating the loan application of the loan with the last one? What's the meaning of duration for a "withdrawal" application?
        LocalDate newMaturityDate = loan.getMaturityDate().plus(loanApplication.getOfferedPeriodCount(), loanApplication.getOfferedPeriodUnit().toTemporalUnit());
        Contract contract = scheduleService.getCurrentContract(loanId);
        Validate.isTrue(contract.getSourceTransactionType() == TransactionType.ISSUE_LOAN, "Expected ISSUE_LOAN as source transaction for current contract, not possible to extend maturity date");

        log.info("Extending loan maturity date for loan {} from {} to {}", loanId, loan.getMaturityDate(), newMaturityDate);
        scheduleService.changeContractMaturityDate(contract.getId(), newMaturityDate);
        resolveLoanDerivedValues(loanId, when);
    }

    @Override
    public void setPenaltySuspended(Long loanId, boolean penaltySuspended) {
        LoanEntity loan = loanRepository.getRequired(loanId);
        loan.setPenaltySuspended(penaltySuspended);
    }

    @Override
    public Long addInstallment(AddInstallmentCommand command) {
        log.info("Adding installment: [{}]", command);

        Contract contract = scheduleService.getContract(command.getContractId());
        Loan loan = getLoan(contract.getLoanId());
        Long installmentId = scheduleService.addInstallment(command);

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setContractId(command.getContractId());
        tx.setTransactionType(firstNonNull(command.getTransactionType(), TransactionType.INSTALLMENT));
        tx.setInvoiceId(command.getInvoiceId());
        tx.setPrincipalInvoiced(command.getPrincipalInvoiced());
        tx.setPrincipalWrittenOff(command.getPrincipalWrittenOff());
        tx.setInterestApplied(command.getInterestApplied());
        tx.setInterestInvoiced(command.getInterestInvoiced());
        tx.setInterestWrittenOff(command.getInterestWrittenOff());
        tx.setPenaltyApplied(command.getPenaltyApplied());
        tx.setPenaltyInvoiced(command.getPenaltyInvoiced());
        tx.setPenaltyWrittenOff(command.getPenaltyWrittenOff());
        tx.setInstallmentId(installmentId);
        tx.setEntries(ImmutableList.copyOf(command.getEntries()));
        tx.setValueDate(command.getValueDate());

        transactionBuilder.addLoanValues(loan, tx);
        transactionService.addTransaction(tx);

        return installmentId;
    }

    @Override
    public void cancelInstallment(CancelInstallmentCommand command) {
        log.info("Cancelling installment: [{}]", command);

        Installment installment = scheduleService.getInstallment(command.getInstallmentId());
        Loan loan = getLoan(installment.getLoanId());
        Validate.isTrue(installment.getStatus() == InstallmentStatus.OPEN, "Can not cancel closed installment: [{}]", command);

        Balance balance = transactionService.getBalance(TransactionQuery.byInstallment(installment.getId()));
        List<EntryBalance> feesBalance = transactionService.getEntryBalance(TransactionEntryQuery.byInstallment(installment.getId(), TransactionEntryType.FEE));


        AddTransactionCommand tx = new AddTransactionCommand();

        feesBalance.forEach(feeBalance -> {
            BigDecimal appliedReversal = max(amount(0), feeBalance.getAmountApplied().subtract(feeBalance.getAmountPaid())).subtract(feeBalance.getAmountWrittenOff()).negate();
            BigDecimal invoiceReversal = max(amount(0), feeBalance.getAmountInvoiced().subtract(feeBalance.getAmountPaid())).negate();
            tx.addEntry(new AddTransactionCommand.TransactionEntry()
                .setAmountApplied(appliedReversal)
                .setAmountInvoiced(invoiceReversal)
                .setSubType(feeBalance.getSubType())
                .setType(feeBalance.getType())
            );
        });

        BigDecimal principalInvoicedReversal = max(amount(0), balance.getPrincipalInvoiced().subtract(balance.getPrincipalPaid())).negate();
        BigDecimal interestAppliedReversal = max(amount(0), balance.getInterestApplied().subtract(balance.getInterestPaid())).negate();
        BigDecimal interestWrittenOffReversal = balance.getInterestWrittenOff().negate();
        BigDecimal interestInvoicedReversal = max(amount(0), balance.getInterestInvoiced().subtract(balance.getInterestPaid())).negate();
        BigDecimal penaltyAppliedReversal = max(amount(0), balance.getPenaltyApplied().subtract(balance.getPenaltyPaid())).negate();
        BigDecimal penaltyInvoicedReversal = max(amount(0), balance.getPenaltyInvoiced().subtract(balance.getPenaltyPaid())).negate();
        BigDecimal penaltyWrittenOffReversal = balance.getPenaltyWrittenOff().negate();

        tx.setPrincipalInvoiced(principalInvoicedReversal);
        tx.setInterestInvoiced(interestInvoicedReversal);
        tx.setInterestWrittenOff(interestWrittenOffReversal);
        tx.setPenaltyInvoiced(penaltyInvoicedReversal);
        tx.setPenaltyWrittenOff(penaltyWrittenOffReversal);
        if (command.isReverseAppliedAmounts()) {
            tx.setInterestApplied(interestAppliedReversal);
            tx.setPenaltyApplied(penaltyAppliedReversal);
        }

        tx.setTransactionType(TransactionType.CANCEL_INSTALLMENT);
        if (command.isBroken()) {
            tx.setTransactionSubType("BROKEN");
        }

        tx.setScheduleId(installment.getScheduleId());
        tx.setInstallmentId(installment.getId());
        tx.setValueDate(command.getCancelDate());
        transactionBuilder.addLoanValues(loan, tx);
        transactionService.addTransaction(tx);
    }

    @Override
    public void startUpsellDisbursement(Long loanId) {
        log.info("StartUpsellDisbursement loan: {}", loanId);
        LoanEntity loan = loanRepository.getRequired(loanId);
        loan.open(LoanStatusDetail.DISBURSING_UPSELL);
    }

    @Override
    public void endUpsellDisbursement(Long loanId) {
        log.info("EndUpsellDisbursement loan: {}", loanId);
        LoanEntity loan = loanRepository.getRequired(loanId);
        loan.open(LoanStatusDetail.ACTIVE);
    }

    @Override
    public void closePaidLoan(ClosePaidLoanCommand command) {
        log.info("Closing paid loan {}", command.getLoanId());

        LoanEntity loanEntity = loanRepository.getRequired(command.getLoanId());
        Validate.isTrue(!LoanStatus.CLOSED.equals(loanEntity.getStatus()), "Loan must not be closed");
        Validate.isTrue(LoanStatusDetail.PAID.equals(loanEntity.getStatusDetail()), "Loan must be paid");

        Contract contract = scheduleService.getCurrentContract(loanEntity.getId());
        Validate.isTrue(!contract.isCloseLoanOnPaid());

        scheduleService.changeContractCloseLoanOnPaid(contract.getId(), true);

        LoanDerivedValuesResolver loanDerivedValueResolver = loanRegistry.getLoanDerivedValueResolver(loanEntity.getProductId());
        loanDerivedValueResolver.resolveDerivedValues(loanEntity.getId());
    }

    @Override
    public CloudFile exportAgreements(List<Long> loanIds) {
        List<Long> attachmentIds = new ArrayList<>();
        for (Long loanId : loanIds) {
            Loan loan = getLoan(loanId);
            Optional<Attachment> loanAgreement = clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byClient(loan.getClientId(), AttachmentConstants.ATTACHMENT_TYPE_LOAN_AGREEMENT)).stream().findFirst();
            loanAgreement.ifPresent(attachment -> attachmentIds.add(attachment.getFileId()));
        }
        return clientAttachmentService.exportToZipArchive(attachmentIds, String.format("loan_agreements_%s.zip", TimeMachine.today()));
    }

}
