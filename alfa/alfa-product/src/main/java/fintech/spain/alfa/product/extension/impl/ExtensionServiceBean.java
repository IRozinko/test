package fintech.spain.alfa.product.extension.impl;

import com.google.common.collect.ImmutableList;
import fintech.BigDecimalUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.db.ContractEntity;
import fintech.lending.core.loan.db.ContractRepository;
import fintech.lending.core.loan.db.InstallmentEntity;
import fintech.lending.core.loan.db.InstallmentRepository;
import fintech.lending.core.loan.events.LoanPaymentEvent;
import fintech.lending.core.util.TransactionBuilder;
import fintech.spain.alfa.product.extension.ApplyAndRepayExtensionFeeCommand;
import fintech.spain.alfa.product.extension.ExtensionService;
import fintech.strategy.CalculationStrategyService;
import fintech.strategy.model.ExtensionOffer;
import fintech.transactions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static fintech.BigDecimalUtils.eq;
import static fintech.BigDecimalUtils.loe;
import static fintech.transactions.TransactionQuery.byLoan;
import static fintech.transactions.TransactionType.RESCHEDULE_LOAN;

@Slf4j
@Transactional
@Component
public class ExtensionServiceBean implements ExtensionService {

    private final LoanService loanService;
    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;
    private final InstallmentRepository installmentRepository;
    private final ContractRepository contractRepository;
    private final ScheduleService scheduleService;
    private final CalculationStrategyService calculationStrategyService;
    private final ApplicationEventPublisher eventPublisher;


    public ExtensionServiceBean(LoanService loanService,
                                TransactionService transactionService,
                                TransactionBuilder transactionBuilder,
                                InstallmentRepository installmentRepository,
                                ContractRepository contractRepository,
                                ScheduleService scheduleService,
                                CalculationStrategyService calculationStrategyService,
                                ApplicationEventPublisher eventPublisher) {
        this.loanService = loanService;
        this.transactionService = transactionService;
        this.transactionBuilder = transactionBuilder;
        this.installmentRepository = installmentRepository;
        this.contractRepository = contractRepository;
        this.scheduleService = scheduleService;
        this.calculationStrategyService = calculationStrategyService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<ExtensionOffer> findOfferForLoan(Long loanId, BigDecimal paymentAmount, boolean exactPriceMatch, LocalDate onDate) {
        List<ExtensionOffer> extensions = listOffersForLoan(loanId, onDate);
        Predicate<ExtensionOffer> filter = exactPriceMatch ? (offer) -> eq(offer.getPrice(), paymentAmount) || eq(offer.getPriceWithDiscount(), paymentAmount) : (offer) -> loe(offer.getPrice(), paymentAmount);
        return extensions.stream().sorted((o1, o2) -> o2.getPrice().compareTo(o1.getPrice())).filter(filter).findFirst();
    }

    @Override
    public List<ExtensionOffer> listOffersForLoan(Long loanId, LocalDate onDate) {
        Loan loan = loanService.getLoan(loanId);
        if (loan.getStatusDetail() != LoanStatusDetail.ACTIVE) {
            return ImmutableList.of();
        }
        List<Contract> contracts = scheduleService.getContracts(loanId);
        boolean hasReschedule = contracts.stream().anyMatch(contract -> contract.getSourceTransactionType().equals(RESCHEDULE_LOAN));
        if (hasReschedule) {
            return ImmutableList.of();
        }
        return calculationStrategyService.getExtensionStrategyForLoan(loanId)
            .map(s -> s.getOffers(onDate))
            .orElse(ImmutableList.of());
    }

    @Override
    public Long applyAndRepayExtensionFee(ApplyAndRepayExtensionFeeCommand command) {
        log.info("Applying and repaying extension fee: [{}]", command);
        long loanId = command.getLoanId();
        LocalDate valueDate = command.getValueDate();

        Balance loanBalance = transactionService.getBalance(byLoan(loanId, valueDate));
        Loan loan = loanService.getLoan(loanId);
        ExtensionOffer offer = command.getExtensionOffer();
        BigDecimal price = offer.getPrice();
        if (!BigDecimalUtils.isZero(offer.getDiscountPercent())) {
            price = offer.getPriceWithDiscount();
        }
        Validate.isTrue(offer.getPeriodUnit() == ChronoUnit.DAYS, "Only days are supported as extension period unit for now");
        Installment installment = scheduleService.getFirstActiveInstallment(loan.getId());

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setInstallmentId(installment.getId());
        tx.setTransactionType(TransactionType.LOAN_EXTENSION);
        tx.setTransactionSubType(offer.getPeriodCount() + "_DAYS");
        tx.setCashIn(command.getPaymentAmount());
        tx.setValueDate(valueDate);
        tx.setOverpaymentUsed(command.getOverpaymentAmount());

        // quite dummy strategy - revert all remaining penalties on value date of payment
        tx.setPenaltyApplied(loanBalance.getPenaltyDue().negate());
        tx.setPenaltyInvoiced(loanBalance.getPenaltyDue().negate());

        tx.setExtension(1L);
        tx.setExtensionDays(offer.getPeriodCount());

        tx.setOverpaymentUsed(command.getOverpaymentAmount());
        AddTransactionCommand.TransactionEntry entry = new AddTransactionCommand.TransactionEntry();
        entry.setType(TransactionEntryType.FEE);
        entry.setSubType(ExtensionService.EXTENSION_FEE_TYPE);
        entry.setAmountApplied(price);
        entry.setAmountInvoiced(price);
        entry.setAmountPaid(price);
        tx.addEntry(entry);
        if (BigDecimalUtils.isPositive(command.getPaymentAmount())) {
            transactionBuilder.addPaymentValues(command.getPaymentId(), tx);
        }
        transactionBuilder.addLoanValues(loan, tx);
        Long txId = transactionService.addTransaction(tx);

        LocalDate newDueDate = installment.getDueDate().plusDays(tx.getExtensionDays());

        InstallmentEntity installmentEntity = installmentRepository.getRequired(installment.getId());
        installmentEntity.setDueDate(newDueDate);

        changeContractMaturityDate(loan.getId(), newDueDate);

        loanService.resolveLoanDerivedValues(loan.getId(), TimeMachine.today());
        eventPublisher.publishEvent(new LoanPaymentEvent(command.getLoanId(), valueDate, command.getPaymentId()));
        return txId;
    }

    @EventListener
    public void extensionVoided(TransactionAddedEvent event) {
        Transaction transaction = event.getTransaction();
        if (transaction.getTransactionType() != TransactionType.VOID_LOAN_EXTENSION) {
            return;
        }

        Installment installment = scheduleService.getFirstActiveInstallment(transaction.getLoanId());
        LocalDate previousDueDate = installment.getDueDate().plusDays(transaction.getExtensionDays());

        InstallmentEntity installmentEntity = installmentRepository.getRequired(installment.getId());
        installmentEntity.setDueDate(previousDueDate);

        changeContractMaturityDate(transaction.getLoanId(), previousDueDate);

        loanService.resolveLoanDerivedValues(transaction.getLoanId(), TimeMachine.today());
    }

    private void changeContractMaturityDate(Long loanId, LocalDate newDueDate) {
        Contract contract = scheduleService.getCurrentContract(loanId);
        ContractEntity contractEntity = contractRepository.getRequired(contract.getId());
        contractEntity.setMaturityDate(newDueDate);
    }
}
