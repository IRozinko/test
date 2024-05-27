package fintech.payments.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.Validate;
import fintech.payments.InstitutionService;
import fintech.payments.commands.AddInstitutionCommand;
import fintech.payments.commands.UpdateInstitutionCommand;
import fintech.payments.db.*;
import fintech.payments.model.Institution;
import fintech.payments.model.InstitutionAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.JsonUtils.isJsonValid;
import static fintech.payments.db.Entities.institution;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
class InstitutionServiceBean implements InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private InstitutionAccountRepository accountRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Transactional
    @Override
    public Long addInstitution(AddInstitutionCommand command) {
        boolean hasPrimaryAccount = command.getAccounts().stream().anyMatch(AddInstitutionCommand.Account::isPrimary);
        Validate.isTrue(hasPrimaryAccount, "Institution should have primary account: [%s]", command);

        InstitutionEntity entity = new InstitutionEntity();
        entity.setInstitutionType(command.getInstitutionType());
        entity.setName(command.getName());
        entity.setCode(command.getCode());
        entity.setAccounts(command.getAccounts().stream().map(a -> toAccountEntity(a, entity)).collect(Collectors.toList()));
        entity.setPaymentMethods(String.join(",", command.getPaymentMethods()));
        entity.setPrimary(command.isPrimary());
        entity.setDisabled(command.isDisabled());
        entity.setStatementImportFormat(command.getStatementImportFormat());
        entity.setStatementExportFormat(command.getStatementExportFormat());
        entity.setStatementExportParamsJson(command.getStatementExportParamsJson());
        entity.setStatementApiExporter(command.getStatementApiExporter());

        if (command.isPrimary()) {
            unsetPrimaryInstitution();
        }

        return institutionRepository.saveAndFlush(entity).getId();
    }

    private void unsetPrimaryInstitution() {
        queryFactory.update(institution)
            .set(institution.primary, false)
            .where(institution.primary.isTrue())
            .execute();
    }

    @Transactional
    @Override
    public Institution getInstitution(Long id) {
        InstitutionEntity entity = institutionRepository.getRequired(id);
        return entity.toValueObject();
    }

    @Transactional
    @Override
    public Institution getInstitution(String code) {
        InstitutionEntity entity = institutionRepository.findOne(Entities.institution.code.eq(code));
        if (entity == null) {
            return null;
        }
        return entity.toValueObject();
    }

    @Transactional
    @Override
    public Institution getPrimaryInstitution() {
        Optional<InstitutionEntity> entity = institutionRepository.getOptional(institution.primary.isTrue());
        Validate.isTrue(entity.isPresent(), "No primary institution found");
        return entity.get().toValueObject();
    }

    @Override
    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll().stream().map(InstitutionEntity::toValueObject).collect(Collectors.toList());
    }

    private InstitutionAccountEntity toAccountEntity(AddInstitutionCommand.Account account, InstitutionEntity institution) {
        InstitutionAccountEntity entity = new InstitutionAccountEntity();
        entity.setAccountNumber(account.getAccountNumber());
        entity.setInstitution(institution);
        entity.setAccountingAccountCode(account.getAccountingAccountCode());
        entity.setPrimary(account.isPrimary());
        return entity;
    }

    @Override
    public InstitutionAccount getAccount(Long accountId) {
        InstitutionAccountEntity entity = accountRepository.getRequired(accountId);
        return entity.toValueObject();
    }

    @Override
    public Optional<InstitutionAccount> findAccountByNumber(String accountNumber) {
        Optional<InstitutionAccountEntity> entity = accountRepository.getOptional(
            Entities.institutionAccount.accountNumber.eq(accountNumber));
        Optional<InstitutionAccount> account = Optional.empty();
        if (entity.isPresent()) {
            account = Optional.of(entity.get().toValueObject());
        }
        return account;
    }

    @Override
    @Transactional
    public void updateInstitution(UpdateInstitutionCommand command) {
        InstitutionEntity entity = institutionRepository.getRequired(command.getInstitutionId());
        if (isNotBlank(command.getStatementExportParamsJson())) {
            Validate.isTrue(isJsonValid(command.getStatementExportParamsJson()),
                "Invalid export params json [%s]", command.getStatementExportParamsJson());
        }
        Validate.notNull(command.getInstitutionId(), "Institution id required");
        Validate.notBlank(command.getName(), "Institution name required");
        if (command.isDisabled()) {
            Validate.isTrue(!command.isPrimary(), "Can't disable primary institution");
        }

        entity.setName(command.getName());
        entity.setDisabled(command.isDisabled());
        entity.setStatementImportFormat(command.getStatementImportFormat());
        entity.setStatementExportFormat(command.getStatementExportFormat());
        entity.setStatementExportParamsJson(command.getStatementExportParamsJson());

        if (command.isPrimary()) {
            changePrimaryInstitution(entity);
        } else {
            entity.setPrimary(false);
        }

        Validate.notNull(getPrimaryInstitution());
    }

    private void changePrimaryInstitution(InstitutionEntity newPrimaryInstitution) {
        Optional<InstitutionEntity> maybePrimaryInstitution = institutionRepository.getOptional(institution.primary.isTrue());
        if (maybePrimaryInstitution.isPresent()) {
            InstitutionEntity primaryInstitution = maybePrimaryInstitution.get();
            if (!primaryInstitution.getId().equals(newPrimaryInstitution.getId())) {
                primaryInstitution.setPrimary(false);
            }

        }
        newPrimaryInstitution.setPrimary(true);
    }

}
