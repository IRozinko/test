package fintech.payments.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.filestorage.FileStorageService;
import fintech.payments.InstitutionService;
import fintech.payments.PaymentService;
import fintech.payments.StatementService;
import fintech.payments.commands.AddPaymentCommand;
import fintech.payments.commands.StatementImportCommand;
import fintech.payments.db.*;
import fintech.payments.model.*;
import fintech.payments.spi.StatementParser;
import fintech.payments.spi.StatementProcessorHelper;
import fintech.payments.spi.StatementProcessorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.isNegative;

@Slf4j
@Transactional
@Component
public class StatementServiceBean implements StatementService {

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private StatementRowRepository statementRowRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private StatementProcessorRegistry statementProcessorRegistry;

    @Autowired
    private StatementProcessorHelper statementProcessorHelper;

    @Autowired
    private PaymentService paymentService;

    public Long importStatement(StatementImportCommand command) {

        Long fileId = command.getFileId();
        Long institutionId = command.getInstitutionId();

        log.info("Importing statement from fileId [{}] for institutionId [{}]", fileId, institutionId);

        Institution institution = institutionService.getInstitution(institutionId);

        String fileName = fileStorageService.get(fileId)
            .orElseThrow(() -> new RuntimeException("Failed to find file : " + fileId))
            .getOriginalFileName();

        StatementEntity statementEntity = new StatementEntity();
        statementEntity.setInstitutionId(institution.getId());
        statementEntity.setFormat(institution.getStatementImportFormat());
        statementEntity.setFileId(fileId);
        statementEntity.setFileName(fileName);

        String fileFormatName = institution.getStatementImportFormat();
        Validate.notNull(fileFormatName, String.format("File format for institution [%s] not defined", institution.getName()));

        StatementParser parser = statementProcessorRegistry.getParser(fileFormatName);
        StatementParseResult result = fileStorageService.readContents(fileId, parser::parse);

        statementEntity = saveStatement(statementEntity, result);

        return statementEntity.getId();
    }

    @VisibleForTesting
    public StatementEntity saveStatement(StatementEntity statementEntity, StatementParseResult result) {
        StatementEntity entity;

        if (result.getError() == null) {
            List<StatementRow> statementRows = result.getRows();

            statementEntity.setStartDate(result.getStartDate());
            statementEntity.setEndDate(result.getEndDate());
            statementEntity.setAccountNumber(result.getAccountNumber());
            statementEntity.setStatus(StatementStatus.NEW);

            List<StatementRowEntity> rows = result.getRows().stream()
                .map((row) -> this.mapRowToEntity(row, statementEntity))
                .collect(Collectors.toList());

            int importedRows = statementRows.size();
            log.info("Imported total [{}] rows.", importedRows);

            entity = statementRepository.saveAndFlush(statementEntity);
            rows.forEach(row -> statementRowRepository.save(row));
        } else {
            statementEntity.setStatus(StatementStatus.FAILED);
            statementEntity.setError(result.getError());

            entity = statementRepository.saveAndFlush(statementEntity);
            log.warn("Import from fileId [{}] failed, because [{}].", statementEntity.getFileId(), result.getError());
        }

        return entity;
    }

    private StatementRowEntity mapRowToEntity(StatementRow row, StatementEntity statementEntity) {
        Validate.notNull(row.getUniqueKey());
        StatementRowEntity rowEntity = new StatementRowEntity();
        rowEntity.setAccountNumber(row.getAccountNumber());
        rowEntity.setDate(row.getDate());
        rowEntity.setValueDate(row.getValueDate());
        rowEntity.setTransactionCode(row.getTransactionCode());
        rowEntity.setCounterpartyName(row.getCounterpartyName());
        rowEntity.setCounterpartyAccount(row.getCounterpartyAccount());
        rowEntity.setCounterpartyAddress(row.getCounterpartyAddress());
        rowEntity.setDescription(row.getDescription());
        rowEntity.setReference(row.getReference());
        rowEntity.setAmount(row.getAmount());
        rowEntity.setCurrency(row.getCurrency());
        rowEntity.setStatus(row.getStatus());
        rowEntity.setKey(row.getUniqueKey());
        rowEntity.setBalance(row.getBalance());
        rowEntity.setSuggestedTransactionSubType(row.getSuggestedTransactionSubType());
        rowEntity.setSourceJson(row.getSourceJson());
        rowEntity.setStatement(statementEntity);
        rowEntity.setAttributes(ImmutableMap.copyOf(row.getAttributes()));
        return rowEntity;
    }

    @Override
    public void processStatement(Long statementId) {

        log.info("Processing statement [{}]", statementId);

        StatementEntity statementEntity = statementRepository.getRequired(statementId);
        String accountNumber = statementEntity.getAccountNumber();

        Long institutionId = statementEntity.getInstitutionId();
        Optional<InstitutionAccount> accountMaybe = findInstitutionAccount(accountNumber, institutionId);

        if (!accountMaybe.isPresent()) {
            statementEntity.setStatus(StatementStatus.FAILED);
            statementEntity.setError(String.format("Account number [%s] not registered in the system", accountNumber));
            log.error("Account number [{}] not found for statement [{}]", accountNumber, statementId);
            statementRepository.saveAndFlush(statementEntity);
            return;
        }

        InstitutionAccount account = accountMaybe.get();

        List<StatementRowEntity> statementRows = statementRowRepository
            .findAll(Entities.statementRow.statement.id.eq(statementId));

        statementRows.forEach(rowEntity -> processStatementRow(rowEntity, account));

        statementEntity = statementRepository.getRequired(statementId);
        statementEntity.setStatus(StatementStatus.PROCESSED);
        statementRepository.saveAndFlush(statementEntity);
    }

    @Override
    public Optional<Statement> findStatement(Long statementId) {
        return Optional.ofNullable(statementRepository.findOne(Entities.statement.id.eq(statementId))).map(StatementEntity::toValueObject);
    }

    @Override
    public List<StatementRow> findStatementRows(Long statementId) {
        return statementRowRepository
            .findAll(Entities.statementRow.statement.id.eq(statementId), Entities.statementRow.id.asc()).stream()
            .map(StatementRowEntity::toValueObject).collect(Collectors.toList());
    }

    @Override
    public Optional<StatementRow> findStatementRowByPayment(Long paymentId) {
        return Optional.ofNullable(statementRowRepository.findOne(Entities.statementRow.paymentId.eq(paymentId)))
            .map(StatementRowEntity::toValueObject);
    }

    private AddPaymentCommand mapPaymentCommand(StatementRow row, InstitutionAccount account) {
        AddPaymentCommand paymentCommand = new AddPaymentCommand();
        paymentCommand.setAccountId(account.getId());
        paymentCommand.setAmount(row.getAmount().abs());
        paymentCommand.setValueDate(row.getValueDate());
        paymentCommand.setDetails(row.getDescription());
        paymentCommand.setPaymentType(determinePaymentType(row));
        paymentCommand.setReference(row.getReference());
        paymentCommand.setPostedAt(TimeMachine.now());
        paymentCommand.setKey(row.getUniqueKey());
        paymentCommand.setCounterpartyAccount(row.getCounterpartyAccount());
        paymentCommand.setCounterpartyName(row.getCounterpartyName());
        paymentCommand.setCounterpartyAddress(row.getCounterpartyAddress());
        return paymentCommand;
    }

    private PaymentType determinePaymentType(StatementRow row) {
        return isNegative(row.getAmount()) ? PaymentType.OUTGOING : PaymentType.INCOMING;
    }

    private Optional<InstitutionAccount> findInstitutionAccount(String accountNumber, Long institutionId) {

        Institution institution = institutionService.getInstitution(institutionId);
        List<InstitutionAccount> accounts = institution.getAccounts().stream().filter((acc) ->
            acc.getAccountNumber().equals(accountNumber)).collect(Collectors.toList());

        if (accounts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(accounts.get(0));
    }

    private void processStatementRow(StatementRowEntity rowEntity,
                                     InstitutionAccount account) {

        AddPaymentCommand paymentCommand = mapPaymentCommand(rowEntity.toValueObject(), account);
        Optional<Payment> unnaxPayment = statementProcessorHelper.paymentByUnnaxDetails(paymentCommand.getDetails());

        if (unnaxPayment.isPresent()) {
            rowEntity.setStatus(StatementRowStatus.IGNORED);
            rowEntity.setStatusMessage(String.format(
                "Payment with bank_order_code [%s] already exists", paymentCommand.getBankOrderCode()));
            log.info("Payment with bank_order_code [{}] already exists for statement row [{}]",
                paymentCommand.getBankOrderCode(), rowEntity.getId());

            paymentService.updatePayment(new UpdatePaymentCommand(unnaxPayment.get().getId(), rowEntity.getValueDate()));

        } else if (statementProcessorHelper.paymentByKeyExists(paymentCommand.getKey())) {
            rowEntity.setStatus(StatementRowStatus.IGNORED);
            rowEntity.setStatusMessage(String.format(
                "Payment with key [%s] already exists", paymentCommand.getKey()));
            log.info("Payment with key [{}] already exists for statement row [{}]",
                paymentCommand.getKey(), rowEntity.getId());
        } else {
            Long paymentId = paymentService.addPayment(paymentCommand);
            rowEntity.setPaymentId(paymentId);
            rowEntity.setStatus(StatementRowStatus.PROCESSED);
        }
    }

}
