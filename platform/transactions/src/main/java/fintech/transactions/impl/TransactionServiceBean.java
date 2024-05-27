package fintech.transactions.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.PojoUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.Balance;
import fintech.transactions.EntryBalance;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionAddedEvent;
import fintech.transactions.TransactionEntryQuery;
import fintech.transactions.TransactionEntryType;
import fintech.transactions.TransactionQuery;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import fintech.transactions.VoidTransactionCommand;
import fintech.transactions.db.TransactionEntity;
import fintech.transactions.db.TransactionEntryEntity;
import fintech.transactions.db.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.max;
import static fintech.transactions.db.Entities.transaction;
import static fintech.transactions.db.Entities.transactionEntry;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Slf4j
@Transactional
@Component
public class TransactionServiceBean implements TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Long addTransaction(AddTransactionCommand command) {
        log.info("Add transaction: [{}]", command);
        TransactionEntity entity = map(command);
        Long id = repository.saveAndFlush(entity).getId();
        eventPublisher.publishEvent(new TransactionAddedEvent(getTransaction(id)));
        return id;
    }

    private TransactionEntity map(AddTransactionCommand command) {
        TransactionEntity entity = new TransactionEntity();

        entity.setTransactionType(command.getTransactionType());
        entity.setTransactionSubType(command.getTransactionSubType());
        entity.setValueDate(command.getValueDate());
        entity.setPostDate(firstNonNull(command.getPostDate(), TimeMachine.today()));
        entity.setBookingDate(command.getBookingDate());

        entity.setPrincipalDisbursed(command.getPrincipalDisbursed());
        entity.setPrincipalWrittenOff(command.getPrincipalWrittenOff());
        entity.setPrincipalPaid(command.getPrincipalPaid());
        entity.setPrincipalInvoiced(command.getPrincipalInvoiced());
        entity.setInterestApplied(command.getInterestApplied());
        entity.setInterestWrittenOff(command.getInterestWrittenOff());
        entity.setInterestPaid(command.getInterestPaid());
        entity.setInterestInvoiced(command.getInterestInvoiced());
        entity.setPenaltyApplied(command.getPenaltyApplied());
        entity.setPenaltyPaid(command.getPenaltyPaid());
        entity.setPenaltyWrittenOff(command.getPenaltyWrittenOff());
        entity.setPenaltyInvoiced(command.getPenaltyInvoiced());

        entity.setFeeApplied(calculateFeeEntryTotal(command.getEntries(), AddTransactionCommand.TransactionEntry::getAmountApplied));
        entity.setFeePaid(calculateFeeEntryTotal(command.getEntries(), AddTransactionCommand.TransactionEntry::getAmountPaid));
        entity.setFeeWrittenOff(calculateFeeEntryTotal(command.getEntries(), AddTransactionCommand.TransactionEntry::getAmountWrittenOff));
        entity.setFeeInvoiced(calculateFeeEntryTotal(command.getEntries(), AddTransactionCommand.TransactionEntry::getAmountInvoiced));

        entity.setOverpaymentReceived(command.getOverpaymentReceived());
        entity.setOverpaymentRefunded(command.getOverpaymentRefunded());
        entity.setOverpaymentUsed(command.getOverpaymentUsed());
        entity.setCreditLimit(command.getCreditLimit());
        entity.setCreditLimitAvailable(command.getCreditLimitAvailable());
        entity.setCashIn(command.getCashIn());
        entity.setCashOut(command.getCashOut());

        entity.setClientId(command.getClientId());
        entity.setLoanId(command.getLoanId());
        entity.setApplicationId(command.getApplicationId());
        entity.setProductId(command.getProductId());
        entity.setPaymentId(command.getPaymentId());
        entity.setInstitutionId(command.getInstitutionId());
        entity.setInstitutionAccountId(command.getInstitutionAccountId());
        entity.setDisbursementId(command.getDisbursementId());
        entity.setInvoiceId(command.getInvoiceId());
        entity.setInstallmentId(command.getInstallmentId());
        entity.setContractId(command.getContractId());

        entity.setExtensionDays(command.getExtensionDays());
        Validate.isTrue(command.getExtension() == 0 | command.getExtension() == 1 | command.getExtension() == -1, "Invalid extension value [%s]", command.getExtension());
        entity.setExtension(command.getExtension());

        entity.setDpd(command.getDpd());

        entity.setComments(command.getComments());

        command.getEntries().stream().map(entry -> mapEntry(entity, entry)).forEach(entity::addEntry);

        requireValidEntries(entity);

        return entity;
    }

    private BigDecimal calculateFeeEntryTotal(List<AddTransactionCommand.TransactionEntry> entries,
                                              Function<AddTransactionCommand.TransactionEntry, BigDecimal> amountFunction) {
        return entries.stream()
            .filter(entry -> entry.getType() == TransactionEntryType.FEE)
            .map(amountFunction)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void requireValidEntries(TransactionEntity entity) {
        List<TransactionEntryEntity> feeEntries = entity.getEntries().stream().filter(entry -> entry.getType() == TransactionEntryType.FEE).collect(toList());
        BigDecimal feeAmountApplied = feeEntries.stream().map(TransactionEntryEntity::getAmountApplied).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal feeAmountPaid = feeEntries.stream().map(TransactionEntryEntity::getAmountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal feeAmountWrittenOff = feeEntries.stream().map(TransactionEntryEntity::getAmountWrittenOff).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal feeAmountInvoiced = feeEntries.stream().map(TransactionEntryEntity::getAmountInvoiced).reduce(BigDecimal.ZERO, BigDecimal::add);

        Validate.isEqual(entity.getFeeApplied(), feeAmountApplied, "Transaction fee applied amount [%s] does not match with transaction entry amount [%s]");
        Validate.isEqual(entity.getFeePaid(), feeAmountPaid, "Transaction fee paid amount [%s] does not match with transaction entry amount [%s]");
        Validate.isEqual(entity.getFeeWrittenOff(), feeAmountWrittenOff, "Transaction fee written off amount [%s] does not match with transaction entry amount [%s]");
        Validate.isEqual(entity.getFeeInvoiced(), feeAmountInvoiced, "Transaction fee invoiced amount [%s] does not match with transaction entry amount [%s]");
    }

    private TransactionEntryEntity mapEntry(TransactionEntity txEntity, AddTransactionCommand.TransactionEntry command) {
        TransactionEntryEntity entryEntity = new TransactionEntryEntity();
        entryEntity.setType(command.getType());
        entryEntity.setSubType(command.getSubType());
        entryEntity.setAmountApplied(command.getAmountApplied());
        entryEntity.setAmountPaid(command.getAmountPaid());
        entryEntity.setAmountWrittenOff(command.getAmountWrittenOff());
        entryEntity.setAmountInvoiced(command.getAmountInvoiced());
        entryEntity.setTransaction(txEntity);

        return entryEntity;
    }

    @Override
    public Long voidTransaction(VoidTransactionCommand command) {
        log.info("Void transaction: [{}]", command);
        TransactionEntity sourceTx = repository.getRequired(command.getId());

        TransactionType voidTxType = sourceTx.getTransactionType().getVoidTransactionType();
        Validate.notNull(voidTxType, "Void not supported for transaction type [%s]", sourceTx.getTransactionType());
        Validate.isTrue(sourceTx.getVoidedDate() == null, "Transaction already voided: [%s]", sourceTx);

        TransactionEntity voidTx = new TransactionEntity();
        PojoUtils.copyProperties(voidTx, sourceTx);
        voidTx.setId(null);
        voidTx.setPostDate(command.getVoidedDate());
        voidTx.setBookingDate(command.getBookingDate());
        voidTx.setValueDate(sourceTx.getValueDate());
        voidTx.setVoidedDate(command.getVoidedDate());
        voidTx.setVoidsTransactionId(sourceTx.getId());
        voidTx.setTransactionType(voidTxType);
        voidTx.setVoided(true);
        voidTx.negate();

        sourceTx.getEntries().forEach(entry -> {
            TransactionEntryEntity voidEntry = new TransactionEntryEntity();
            PojoUtils.copyProperties(voidEntry, entry);
            voidEntry.negate();
            voidTx.addEntry(voidEntry);
        });
        Long voidTxId = repository.saveAndFlush(voidTx).getId();

        sourceTx.setVoidedDate(command.getVoidedDate());
        sourceTx.setVoided(true);

        eventPublisher.publishEvent(new TransactionAddedEvent(getTransaction(voidTxId)));
        return voidTxId;
    }

    @Override
    public Long voidDisbursementTransaction(long disbursementId, TransactionType type) {
        Transaction tx = findFirst(TransactionQuery.byDisbursement(disbursementId, type, false))
            .orElseThrow(() ->
                new IllegalStateException(format("Transaction not found: disbursementId [%d], [%s]", disbursementId, type)));

        return voidTransaction(new VoidTransactionCommand(tx.getId(), tx.getBookingDate(), TimeMachine.today()));
    }


    @Override
    public Transaction getTransaction(Long id) {
        TransactionEntity entity = repository.getRequired(id);
        return entity.toValueObject();
    }

    @Override
    public Optional<Transaction> lastPaidTransaction(Long loanId) {
        TransactionQuery query = TransactionQuery.notVoidedByLoan(loanId, TransactionType.REPAYMENT, TransactionType.LOAN_EXTENSION);

        return Optional.ofNullable(queryFactory.select(transaction)
            .from(transaction)
            .where(ExpressionUtils.allOf(toPredicates(query)))
            .orderBy(transaction.valueDate.desc())
            .fetchFirst())
            .map(TransactionEntity::toValueObject);
    }

    @Override
    public long countTransactions(TransactionQuery query) {
        List<Predicate> predicates = toPredicates(query);
        long count = queryFactory
            .selectFrom(transaction)
            .where(ExpressionUtils.allOf(predicates))
            .fetchCount();
        return count;
    }

    @Override
    public List<Transaction> findTransactions(TransactionQuery query) {
        List<Predicate> predicates = toPredicates(query);
        return repository.findAll(ExpressionUtils.allOf(predicates), transaction.id.desc())
            .stream()
            .map(TransactionEntity::toValueObject)
            .collect(toList());
    }

    @Override
    public Optional<Transaction> findFirst(TransactionQuery query) {
        List<Predicate> predicates = toPredicates(query);
        return repository.findFirst(ExpressionUtils.allOf(predicates), transaction.id.desc())
            .map(TransactionEntity::toValueObject);
    }

    @Override
    public List<Transaction> findTransactions(TransactionEntryQuery query) {
        return queryFactory
            .select(transaction)
            .from(transactionEntry)
            .join(transactionEntry.transaction, transaction).on(transactionEntry.transaction.id.eq(transaction.id))
            .where(ExpressionUtils.allOf(toPredicates(query)))
            .distinct()
            .fetch()
            .stream()
            .map(TransactionEntity::toValueObject)
            .collect(toList());
    }

    private List<Predicate> toPredicates(TransactionQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getClientId() != null) {
            predicates.add(transaction.clientId.eq(query.getClientId()));
        }
        if (query.getLoanId() != null) {
            predicates.add(transaction.loanId.eq(query.getLoanId()));
        }
        if (query.getPaymentId() != null) {
            predicates.add(transaction.paymentId.eq(query.getPaymentId()));
        }
        if (query.getInvoiceId() != null) {
            predicates.add(transaction.invoiceId.eq(query.getInvoiceId()));
        }
        if (query.getScheduleId() != null) {
            predicates.add(transaction.scheduleId.eq(query.getScheduleId()));
        }
        if (query.getInstallmentId() != null) {
            predicates.add(transaction.installmentId.eq(query.getInstallmentId()));
        }
        if (query.getTransactionType() != null) {
            predicates.add(transaction.transactionType.eq(query.getTransactionType()));
        }
        if (query.getTransactionTypes() != null) {
            predicates.add(transaction.transactionType.in(query.getTransactionTypes()));
        }
        if (query.getTransactionSubType() != null) {
            predicates.add(transaction.transactionSubType.eq(query.getTransactionSubType()));
        }
        if (query.getVoided() != null) {
            predicates.add(transaction.voided.eq(query.getVoided()));
        }
        if (query.getValueDateFrom() != null) {
            predicates.add(transaction.valueDate.goe(query.getValueDateFrom()));
        }
        if (query.getValueDateTo() != null) {
            predicates.add(transaction.valueDate.loe(query.getValueDateTo()));
        }
        if (query.getValueDateIs() != null) {
            predicates.add(transaction.valueDate.eq(query.getValueDateIs()));
        }
        if (query.getDisbursementId() != null) {
            predicates.add(transaction.disbursementId.eq(query.getDisbursementId()));
        }
        if (query.getInvoiceIdNotNull() != null && query.getInvoiceIdNotNull()) {
            predicates.add(transaction.invoiceId.isNotNull());
        }

        return predicates;
    }

    private List<Predicate> toPredicates(TransactionEntryQuery query) {
        List<Predicate> predicates = new ArrayList<>();

        if (query.getLoanId() != null) {
            predicates.add(transaction.loanId.eq(query.getLoanId()));
        }
        if (query.getType() != null) {
            predicates.add(transactionEntry.type.eq(query.getType()));
        }
        if (query.getSubType() != null) {
            predicates.add(transactionEntry.subType.in(query.getSubType()));
        }
        if (query.getInvoiceId() != null) {
            predicates.add(transaction.invoiceId.eq(query.getInvoiceId()));
        }
        if (query.getScheduleId() != null) {
            predicates.add(transaction.scheduleId.eq(query.getScheduleId()));
        }
        if (query.getInstallmentId() != null) {
            predicates.add(transaction.installmentId.eq(query.getInstallmentId()));
        }
        if (query.getValueDate() != null) {
            predicates.add(transaction.valueDate.eq(query.getValueDate()));
        }
        if (query.getValueDateTo() != null) {
            predicates.add(transaction.valueDate.loe(query.getValueDateTo()));
        }
        if (query.getValueDateFrom() != null) {
            predicates.add(transaction.valueDate.goe(query.getValueDateFrom()));
        }
        if (query.getTransactionType() != null) {
            predicates.add(transaction.transactionType.eq(query.getTransactionType()));
        }
        if (query.getVoided() != null) {
            predicates.add(transaction.voided.eq(query.getVoided()));
        }

        return predicates;
    }

    @Override
    public Balance getBalance(TransactionQuery query) {
        Tuple tuple = queryFactory
            .select(balanceSelect)
            .from(transaction)
            .where(ExpressionUtils.allOf(toPredicates(query)))
            .fetchOne();

        return mapBalance(tuple);
    }

    @Override
    public List<EntryBalance> getEntryBalance(TransactionEntryQuery query) {
        List<Tuple> tuple = queryFactory
            .select(entryBalanceSelect)
            .from(transactionEntry)
            .join(transactionEntry.transaction, transaction).on(transactionEntry.transaction.id.eq(transaction.id))
            .groupBy(transactionEntry.type, transactionEntry.subType)
            .where(ExpressionUtils.allOf(toPredicates(query)))
            .fetch();

        return tuple.stream().map(this::mapEntryBalance).collect(Collectors.toList());
    }

    private final Expression[] balanceSelect = new Expression[]{
        transaction.count(),
        transaction.principalDisbursed.sum(),
        transaction.principalPaid.sum(),
        transaction.principalWrittenOff.sum(),
        transaction.principalInvoiced.sum(),
        transaction.interestApplied.sum(),
        transaction.interestPaid.sum(),
        transaction.interestWrittenOff.sum(),
        transaction.interestInvoiced.sum(),
        transaction.penaltyApplied.sum(),
        transaction.penaltyPaid.sum(),
        transaction.penaltyWrittenOff.sum(),
        transaction.penaltyInvoiced.sum(),
        transaction.feeApplied.sum(),
        transaction.feePaid.sum(),
        transaction.feeWrittenOff.sum(),
        transaction.feeInvoiced.sum(),
        transaction.cashIn.sum(),
        transaction.cashOut.sum(),
        transaction.overpaymentReceived.sum(),
        transaction.overpaymentRefunded.sum(),
        transaction.overpaymentUsed.sum(),
        transaction.extensionDays.sum(),
        transaction.extension.sum(),
        transaction.creditLimit.sum()
    };

    private final Expression[] entryBalanceSelect = new Expression[]{
        transactionEntry.type,
        transactionEntry.subType,
        transactionEntry.count(),
        transactionEntry.amountApplied.sum(),
        transactionEntry.amountPaid.sum(),
        transactionEntry.amountWrittenOff.sum(),
        transactionEntry.amountInvoiced.sum(),
    };

    private Balance mapBalance(Tuple tuple) {
        Balance balance = new Balance();
        if (firstNonNull(tuple.get(transaction.count()), 0L) == 0L) {
            return balance;
        }

        BigDecimal principal = tuple.get(transaction.principalDisbursed.sum());
        BigDecimal principalPaid = tuple.get(transaction.principalPaid.sum());
        BigDecimal principalWrittenOff = tuple.get(transaction.principalWrittenOff.sum());
        BigDecimal principalInvoiced = tuple.get(transaction.principalInvoiced.sum());
        BigDecimal interest = tuple.get(transaction.interestApplied.sum());
        BigDecimal interestPaid = tuple.get(transaction.interestPaid.sum());
        BigDecimal interestWrittenOff = tuple.get(transaction.interestWrittenOff.sum());
        BigDecimal interestInvoiced = tuple.get(transaction.interestInvoiced.sum());
        BigDecimal fee = tuple.get(transaction.feeApplied.sum());
        BigDecimal feePaid = tuple.get(transaction.feePaid.sum());
        BigDecimal feeWrittenOff = tuple.get(transaction.feeWrittenOff.sum());
        BigDecimal feeInvoiced = tuple.get(transaction.feeInvoiced.sum());
        BigDecimal penalty = tuple.get(transaction.penaltyApplied.sum());
        BigDecimal penaltyPaid = tuple.get(transaction.penaltyPaid.sum());
        BigDecimal penaltyWrittenOff = tuple.get(transaction.penaltyWrittenOff.sum());
        BigDecimal penaltyInvoiced = tuple.get(transaction.penaltyInvoiced.sum());
        BigDecimal cashIn = tuple.get(transaction.cashIn.sum());
        BigDecimal cashOut = tuple.get(transaction.cashOut.sum());
        BigDecimal overpaymentReceived = tuple.get(transaction.overpaymentReceived.sum());
        BigDecimal overpaymentRefunded = tuple.get(transaction.overpaymentRefunded.sum());
        BigDecimal overpaymentUsed = tuple.get(transaction.overpaymentUsed.sum());
        Long extensionDays = tuple.get(transaction.extensionDays.sum());
        Long extensions = tuple.get(transaction.extension.sum());
        BigDecimal creditLimit = tuple.get(transaction.creditLimit.sum());

        balance.setPrincipalDisbursed(principal);
        balance.setPrincipalPaid(principalPaid);
        balance.setPrincipalWrittenOff(principalWrittenOff);
        balance.setPrincipalOutstanding(principal.subtract(principalPaid).subtract(principalWrittenOff));
        balance.setPrincipalDue(principalInvoiced.subtract(principalPaid));
        balance.setPrincipalInvoiced(principalInvoiced);

        balance.setInterestApplied(interest);
        balance.setInterestPaid(interestPaid);
        balance.setInterestWrittenOff(interestWrittenOff);
        balance.setInterestOutstanding(interest.subtract(interestPaid).subtract(interestWrittenOff));
        balance.setInterestDue(interestInvoiced.subtract(interestPaid));
        balance.setInterestInvoiced(interestInvoiced);

        balance.setFeeApplied(fee);
        balance.setFeePaid(feePaid);
        balance.setFeeWrittenOff(feeWrittenOff);
        balance.setFeeOutstanding(fee.subtract(feePaid).subtract(feeWrittenOff));
        balance.setFeeDue(feeInvoiced.subtract(feePaid));
        balance.setFeeInvoiced(feeInvoiced);

        balance.setPenaltyApplied(penalty);
        balance.setPenaltyPaid(penaltyPaid);
        balance.setPenaltyWrittenOff(penaltyWrittenOff);
        balance.setPenaltyOutstanding(penalty.subtract(penaltyPaid).subtract(penaltyWrittenOff));
        balance.setPenaltyDue(penaltyInvoiced.subtract(penaltyPaid));
        balance.setPenaltyInvoiced(penaltyInvoiced);

        balance.setCashIn(cashIn);
        balance.setCashOut(cashOut);
        balance.setOverpaymentReceived(overpaymentReceived);
        balance.setOverpaymentUsed(overpaymentUsed);
        balance.setOverpaymentRefunded(overpaymentRefunded);
        balance.setOverpaymentAvailable(overpaymentReceived.subtract(overpaymentRefunded).subtract(overpaymentUsed));

        balance.setUnsettledDisbursement(principal.add(overpaymentRefunded).subtract(cashOut));

        balance.setCreditLimit(creditLimit);
        balance.setCreditLimitAvailable(max(amount(0), creditLimit.subtract(balance.getPrincipalOutstanding())));

        balance.setTotalOutstanding(balance.getPrincipalOutstanding().add(balance.getInterestOutstanding().add(balance.getFeeOutstanding()).add(balance.getPenaltyOutstanding())));
        balance.setTotalPaid(balance.getPrincipalPaid()
            .add(balance.getInterestPaid())
            .add(balance.getFeePaid())
            .add(balance.getPenaltyPaid()));
        balance.setTotalInvoiced(balance.getPrincipalInvoiced().add(balance.getInterestInvoiced())
            .add(balance.getPenaltyInvoiced()).add(balance.getFeeInvoiced()));
        balance.setTotalDue(balance.getPrincipalDue().add(balance.getInterestDue()).add(balance.getPenaltyDue()).add(balance.getFeeDue()));

        balance.setExtensions(extensions);
        balance.setExtensionDays(extensionDays);

        return balance;
    }

    private EntryBalance mapEntryBalance(Tuple tuple) {
        EntryBalance balance = new EntryBalance();
        balance.setType(tuple.get(transactionEntry.type));
        balance.setSubType(tuple.get(transactionEntry.subType));

        if (firstNonNull(tuple.get(transactionEntry.count()), 0L) == 0L) {
            return balance;
        }

        BigDecimal amountApplied = tuple.get(transactionEntry.amountApplied.sum());
        BigDecimal amountPaid = tuple.get(transactionEntry.amountPaid.sum());
        BigDecimal amountWrittenOff = tuple.get(transactionEntry.amountWrittenOff.sum());
        BigDecimal amountInvoiced = tuple.get(transactionEntry.amountInvoiced.sum());
        BigDecimal amountDue = amountInvoiced.subtract(amountPaid);
        BigDecimal amountOutstanding = amountApplied.subtract(amountPaid).subtract(amountWrittenOff);

        balance.setAmountApplied(amountApplied);
        balance.setAmountPaid(amountPaid);
        balance.setAmountWrittenOff(amountWrittenOff);
        balance.setAmountInvoiced(amountInvoiced);
        balance.setAmountOutstanding(amountOutstanding);
        balance.setAmountDue(amountDue);
        return balance;
    }
}
