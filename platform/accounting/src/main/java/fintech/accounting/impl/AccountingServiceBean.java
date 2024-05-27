package fintech.accounting.impl;

import fintech.BigDecimalUtils;
import fintech.Validate;
import fintech.accounting.*;
import fintech.accounting.db.*;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;

@Slf4j
@Component
@Transactional
class AccountingServiceBean implements AccountingService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private TransactionService transactionService;

    @Override
    public Long addAccount(AddAccountCommand command) {
        log.info("Add account: [{}]1", command);
        AccountEntity entity = new AccountEntity();
        entity.setCode(command.getCode());
        entity.setName(command.getName());
        return accountRepository.saveAndFlush(entity).getId();
    }

    @Override
    public void book(BookTransactionCommand command) {
        log.debug("Post transaction: [{}]", command);
        Validate.notNull(command.getTransactionId(), "Transaction id required");

        List<EntryEntity> existingEntries = entryRepository.findAll(Entities.entry.transactionId.eq(command.getTransactionId()));
        entryRepository.deleteInBatch(existingEntries);

        Transaction transaction = transactionService.getTransaction(command.getTransactionId());

        List<EntryEntity> entries = command.getEntries().stream()
            .filter((e) -> !BigDecimalUtils.isZero(e.getAmount()))
            .map((e) -> {
                Validate.notNull(e.getBookingDate(), "Booking date required");
                Validate.notNull(e.getValueDate(), "Value date required");
                Validate.notBlank(e.getAccountCode(), "Account code required");
                Validate.notNull(e.getAmount(), "Amount required");
                Validate.notNull(e.getEntryType(), "Entry type required");

                AccountEntity account = getAccountEntity(e.getAccountCode());
                EntryEntity entity = new EntryEntity();
                entity.setAccount(account);
                entity.setValueDate(e.getValueDate());
                entity.setBookingDate(e.getBookingDate());
                entity.setPostDate(transaction.getPostDate());
                entity.setTransactionId(command.getTransactionId());
                entity.setAmount(e.getAmount());
                entity.setEntryType(e.getEntryType());
                if (e.getEntryType() == EntryType.D) {
                    entity.setDebit(e.getAmount());
                } else {
                    entity.setCredit(e.getAmount());
                }

                entity.setTransactionType(transaction.getTransactionType());
                entity.setProductId(transaction.getProductId());
                entity.setInstitutionId(transaction.getInstitutionId());
                entity.setInstitutionAccountId(transaction.getInstitutionAccountId());
                entity.setLoanId(transaction.getLoanId());
                entity.setClientId(transaction.getClientId());
                entity.setPaymentId(transaction.getPaymentId());
                entity.setDisbursementId(transaction.getDisbursementId());
                entity.setInvoiceId(transaction.getInvoiceId());

                return entity;
            }).collect(Collectors.toList());

        validateTotals(entries, command);
        validateEntries(entries, command);

        entryRepository.save(entries);
    }

    @Override
    public Optional<Account> findAccount(String code) {
        return accountRepository.getOptional(Entities.account.code.eq(code)).map(AccountEntity::toValueObject);
    }

    @Override
    public void bookVoid(Transaction transaction) {
        if (transaction.getVoidsTransactionId() == null) {
            return;
        }

        List<EntryEntity> entries = entryRepository
            .findAll(Entities.entry.transactionId.eq(transaction.getVoidsTransactionId()));
        if (entries.isEmpty()) {
            return;
        }

        List<PostEntry> reverseEntries = entries.stream().map((e) -> {
            PostEntry entry = new PostEntry();
            entry.setAccountCode(e.getAccount().getCode());
            entry.setAmount(e.getAmount().negate());
            entry.setEntryType(e.getEntryType());
            entry.setBookingDate(transaction.getBookingDate());
            entry.setValueDate(e.getValueDate());
            return entry;
        }).collect(Collectors.toList());

        BookTransactionCommand command = new BookTransactionCommand();
        command.setTransactionId(transaction.getId());
        command.setEntries(reverseEntries);
        book(command);
    }

    private AccountEntity getAccountEntity(String code) {
        return accountRepository.getOptional(Entities.account.code.eq(code))
            .orElseThrow(() -> new IllegalStateException("Account not found by code: " + code));
    }

    private void validateTotals(List<EntryEntity> entries, BookTransactionCommand command) {
        BigDecimal debitSum = entries.stream().map(EntryEntity::getDebit).reduce(amount(0), BigDecimal::add);
        BigDecimal creditSum = entries.stream().map(EntryEntity::getCredit).reduce(amount(0), BigDecimal::add);
        Validate.isEqual(debitSum, creditSum, "Debit [%s] and credit [%s] are not equal for transaction: [%s]", debitSum, creditSum, command);
    }

    private void validateEntries(List<EntryEntity> entries, BookTransactionCommand command) {
        entries.stream().collect(Collectors.groupingBy(EntryEntity::getValueDate)).values().forEach(e -> validateTotals(e, command));
    }
}
