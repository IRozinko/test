package fintech.lending.core.loan.impl;

import com.querydsl.core.types.Predicate;
import fintech.Validate;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.InstallmentStatus;
import fintech.lending.core.loan.InstallmentStatusDetail;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.commands.AddInstallmentCommand;
import fintech.lending.core.loan.commands.AddLoanContractCommand;
import fintech.lending.core.loan.db.ContractEntity;
import fintech.lending.core.loan.db.ContractRepository;
import fintech.lending.core.loan.db.InstallmentEntity;
import fintech.lending.core.loan.db.InstallmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.allOf;

@Slf4j
@Transactional
@Component
public class ScheduleServiceBean implements ScheduleService {

    private final InstallmentRepository installmentRepository;
    private final ContractRepository contractRepository;

    @Autowired
    public ScheduleServiceBean(InstallmentRepository installmentRepository, ContractRepository contractRepository) {
        this.installmentRepository = installmentRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    public List<Installment> findInstallments(InstallmentQuery query) {
        List<InstallmentEntity> installments = installmentRepository
            .findAll(allOf(toPredicates(query)), Entities.installment.periodFrom.asc());
        return installments.stream().map(InstallmentEntity::toValueObject).collect(Collectors.toList());
    }

    @Override
    public Contract getCurrentContract(Long loanId) {
        ContractEntity currentContract = contractRepository.findOneOrNull(
            Entities.contract.loanId.eq(loanId).and(Entities.contract.current.isTrue()));
        Validate.notNull(currentContract, "Current contract not found for loan [%s]", loanId);
        return currentContract.toValueObject();
    }

    @Override
    public Contract getContract(Long contractId) {
        return contractRepository.getRequired(contractId).toValueObject();
    }

    @Override
    public Installment getFirstActiveInstallment(Long loanId) {
        Contract contract = getCurrentContract(loanId);
        List<Installment> installments = findInstallments(InstallmentQuery.openContractInstallments(contract.getId()));
        Validate.isTrue(!installments.isEmpty(), "No active installments");

        return installments.get(0);
    }

    @Override
    public Long addContract(AddLoanContractCommand command) {
        ContractEntity contract = new ContractEntity();

        Optional<ContractEntity> maybeCurrentContract = contractRepository.getOptional(
            Entities.contract.loanId.eq(command.getLoanId()).and(Entities.contract.current.isTrue()));
        if (maybeCurrentContract.isPresent()) {
            maybeCurrentContract.get().setCurrent(false);
            contract.setPreviousContractId(maybeCurrentContract.get().getId());
        }

        contract.setProductId(command.getProductId());
        contract.setLoanId(command.getLoanId());
        contract.setClientId(command.getClientId());
        contract.setApplicationId(command.getApplicationId());
        contract.setContractDate(command.getContractDate());
        contract.setEffectiveDate(command.getEffectiveDate());
        contract.setMaturityDate(command.getMaturityDate());
        contract.setCurrent(true);
        contract.setPeriodCount(command.getPeriodCount());
        contract.setPeriodUnit(command.getPeriodUnit());
        contract.setNumberOfInstallments(command.getNumberOfInstallments());
        contract.setCloseLoanOnPaid(command.isCloseLoanOnPaid());
        contract.setBaseOverdueDays(command.getBaseOverdueDays());
        contract.setSourceTransactionId(command.getSourceTransactionId());
        contract.setSourceTransactionType(command.getSourceTransactionType());
        contractRepository.save(contract);

        Validate.isTrue(getCurrentContract(command.getLoanId()).getId().equals(contract.getId()), "Invalid current contract");

        return contract.getId();
    }

    @Override
    public void changeContractEffectiveDate(Long contractId, LocalDate effectiveDate) {
        ContractEntity contractEntity = contractRepository.getRequired(contractId);
        contractEntity.setEffectiveDate(effectiveDate);
    }

    @Override
    public void changeContractMaturityDate(Long contractId, LocalDate maturityDate) {
        ContractEntity contractEntity = contractRepository.getRequired(contractId);
        contractEntity.setMaturityDate(maturityDate);
    }

    @Override
    public void changeContractCloseLoanOnPaid(Long contractId, boolean newValue) {
        ContractEntity contractEntity = contractRepository.getRequired(contractId);
        contractEntity.setCloseLoanOnPaid(newValue);
    }

    @Override
    public List<Contract> getContracts(Long loanId) {
        return contractRepository.findAll(Entities.contract.loanId.eq(loanId), Entities.contract.contractDate.asc()).stream()
            .map(ContractEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public Long addInstallment(AddInstallmentCommand command) {
        ContractEntity contract = contractRepository.getRequired(command.getContractId());
        InstallmentEntity installment = new InstallmentEntity();
        installment.setContractId(command.getContractId());
        installment.setLoanId(contract.getLoanId());
        installment.setClientId(contract.getClientId());
        installment.setInvoiceId(command.getInvoiceId());
        installment.setPeriodFrom(command.getPeriodFrom());
        installment.setPeriodTo(command.getPeriodTo());
        installment.setValueDate(command.getValueDate());
        installment.setDueDate(command.getDueDate());
        installment.setOriginalDueDate(command.getDueDate());
        installment.setGracePeriodInDays(command.getGracePeriodInDays());
        installment.setApplyPenalty(command.isApplyPenalty());
        installment.setInstallmentSequence(command.getInstallmentSequence());
        installment.setStatus(InstallmentStatus.OPEN);
        installment.setStatusDetail(InstallmentStatusDetail.PENDING);
        installment.setGenerateInvoiceOnDate(command.getGenerateInvoiceOnDate());
        installment.setInstallmentNumber(StringUtils.upperCase(command.getInstallmentNumber()));
        installment = installmentRepository.saveAndFlush(installment);

        return installment.getId();
    }

    @Override
    public void saveInstallmentInvoice(Long installmentId, Long fileId, String fileName, LocalDateTime when) {
        log.info("Installment [{}] invoice file [{}] with id [{}] generated", installmentId, fileName, fileId);
        InstallmentEntity installment = installmentRepository.getRequired(installmentId);
        installment.setInvoiceFileGeneratedAt(Validate.notNull(when));
        installment.setInvoiceFileId(Validate.notNull(fileId));
        installment.setInvoiceFileName(fileName);
        installment.setInvoiceFileSentAt(null);
    }

    @Override
    public void installmentInvoiceSent(Long installmentId, LocalDateTime when) {
        log.info("Installment [{}] invoice sent", installmentId);
        InstallmentEntity installment = installmentRepository.getRequired(installmentId);
        installment.setInvoiceFileSentAt(Validate.notNull(when));
    }

    @Override
    public Installment getInstallment(Long installmentId) {
        return installmentRepository.getRequired(installmentId).toValueObject();
    }

    @Override
    public Optional<Installment> findInstallmentByNumber(String number) {
        return installmentRepository.getOptional(Entities.installment.installmentNumber.eq(StringUtils.upperCase(number)))
            .map(InstallmentEntity::toValueObject);
    }


    private List<Predicate> toPredicates(InstallmentQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getLoanId() != null) {
            predicates.add(Entities.installment.loanId.eq(query.getLoanId()));
        }
        if (query.getContractId() != null) {
            predicates.add(Entities.installment.contractId.eq(query.getContractId()));
        }
        if (!query.getStatuses().isEmpty()) {
            predicates.add(Entities.installment.statusDetail.in(query.getStatuses()));
        }
        if (!query.getExcludeStatuses().isEmpty()) {
            predicates.add(Entities.installment.statusDetail.notIn(query.getExcludeStatuses()));
        }
        if (query.getClosed() != null) {
            predicates.add(Entities.installment.status.eq(query.getClosed() ? InstallmentStatus.CLOSED : InstallmentStatus.OPEN));
        }
        if (query.getClientId() != null) {
            predicates.add(Entities.installment.clientId.eq(query.getClientId()));
        }
        return predicates;
    }

}
