package fintech.spain.alfa.product.dc.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import fintech.Validate;
import fintech.cms.Pdf;
import fintech.cms.PdfRenderer;
import fintech.crm.attachments.AddAttachmentCommand;
import fintech.crm.attachments.AttachmentStatus;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.dc.DcConstants;
import fintech.dc.commands.ChangeCompanyCommand;
import fintech.dc.commands.EditDebtCommand;
import fintech.dc.commands.UnassignDebtCommand;
import fintech.dc.model.DcSettings;
import fintech.dc.model.DcSettings.Companies;
import fintech.dc.model.Debt;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.lending.core.PeriodUnit;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.InstallmentStatus;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.commands.AddInstallmentCommand;
import fintech.lending.core.loan.commands.AddLoanContractCommand;
import fintech.lending.core.loan.commands.CancelInstallmentCommand;
import fintech.lending.core.loan.commands.WriteOffAmountCommand;
import fintech.spain.alfa.product.filestorage.FileStorageCommandFactory;
import fintech.spain.alfa.product.lending.LoanReschedulingService;
import fintech.spain.dc.AbstractDcFacadeBean;
import fintech.spain.dc.command.BreakReschedulingCommand;
import fintech.spain.dc.command.InstallmentCommandFactory;
import fintech.spain.dc.command.RescheduleCommand;
import fintech.spain.dc.command.ReschedulingPreviewCommand;
import fintech.spain.dc.model.DcFacadeConfig;
import fintech.spain.dc.model.ReschedulingPreview;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.spain.alfa.product.dc.DcFacade;
import fintech.spain.alfa.product.dc.commands.ReassignDebtCommand;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.Balance;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.isPositive;
import static fintech.PojoUtils.copyProperties;
import static fintech.TimeMachine.today;
import static fintech.lending.core.loan.InstallmentQuery.openInstallments;
import static fintech.transactions.TransactionQuery.byLoan;
import static java.lang.String.format;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
@Component
@Transactional
class DcFacadeBean extends AbstractDcFacadeBean implements DcFacade {
    private final Set<String> reassignablePortfolios = ImmutableSet.of("Collections", "RescheduledCollections");

    @Autowired
    private PdfRenderer pdfRenderer;

    @Autowired
    private AlfaCmsModels cmsModels;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ClientAttachmentService attachmentService;

    @Autowired
    private AlfaNotificationBuilderFactory alfaNotificationBuilderFactory;

    @Autowired
    private LoanReschedulingService loanReschedulingService;

    public DcFacadeBean() {
        super(new DcFacadeConfig()
            .setReExternalizedPortfolio("Collections"));
    }

    @Override
    public ReschedulingPreview generateReschedulePreview(ReschedulingPreviewCommand command) {
        DcSettings.ReschedulingSettings reschedulingSettings = dcSettingsService.getSettings().getReschedulingSettings();
        Balance balance = transactionService.getBalance(byLoan(command.getLoanId()));
        List<Installment> openInstallments = scheduleService.findInstallments(openInstallments(command.getLoanId()));

        isTrue(openInstallments.size() == 1, "Already rescheduled");

        final BigDecimal principalScheduledPerItem = balance.getPrincipalDue().divide(amount(command.getNumberOfPayments()), 2, RoundingMode.HALF_DOWN);
        BigDecimal principalLeft = balance.getPrincipalDue();
        final BigDecimal interestScheduledPerItem = balance.getInterestDue().divide(amount(command.getNumberOfPayments()), 2, RoundingMode.HALF_DOWN);
        BigDecimal interestLeft = balance.getInterestDue();
        final BigDecimal penaltyScheduledPerItem = balance.getPenaltyDue().divide(amount(command.getNumberOfPayments()), 2, RoundingMode.HALF_DOWN);
        BigDecimal penaltyLeft = balance.getPenaltyDue();

        List<ReschedulingPreview.FeeItem> feeApplied = ImmutableList.of();

        LocalDate periodFrom = command.getWhen();
        LocalDate periodTo = command.getWhen().plusDays(reschedulingSettings.getRepaymentDueDays());
        ReschedulingPreview plan = new ReschedulingPreview();
        plan.setPeriodFrom(periodFrom);
        for (int i = 0; i < command.getNumberOfPayments(); i++) {
            ReschedulingPreview.Item item = new ReschedulingPreview.Item();
            if (i == command.getNumberOfPayments() - 1) {
                // last payment
                item.setPrincipalScheduled(principalLeft);
                item.setInterestScheduled(interestLeft.add(item.getInterestScheduled()));
                item.setPenaltyScheduled(penaltyLeft);
            } else {
                item.setPrincipalScheduled(principalScheduledPerItem);
                principalLeft = principalLeft.subtract(principalScheduledPerItem);
                item.setInterestScheduled(item.getInterestScheduled().add(interestScheduledPerItem));
                interestLeft = interestLeft.subtract(interestScheduledPerItem);
                item.setPenaltyScheduled(penaltyScheduledPerItem);
                penaltyLeft = penaltyLeft.subtract(penaltyScheduledPerItem);
            }

            BigDecimal totalFeesInvoiced = BigDecimal.ZERO;
            if (i == 0) {
                item.setFeeItems(feeApplied);
                totalFeesInvoiced = totalFeesInvoiced.add(feeApplied.stream().map(ReschedulingPreview.FeeItem::getAmountScheduled).reduce(BigDecimal.ZERO, BigDecimal::add));
            }

            item.setInstallmentSequence(i + 1L);
            item.setPeriodFrom(periodFrom);
            item.setPeriodTo(periodTo);
            item.setDueDate(periodTo);

            item.setTotalScheduled(item.getPrincipalScheduled()
                .add(item.getInterestScheduled())
                .add(item.getPenaltyScheduled())
                .add(totalFeesInvoiced));
            item.setApplyPenalty(true);
            long gracePeriodDays = reschedulingSettings.getGracePeriodDays();
            item.setGracePeriodInDays(gracePeriodDays);

            plan.getItems().add(item);

            periodFrom = periodTo.plusDays(reschedulingSettings.getRepaymentDueDays());
            periodTo = periodTo.plusMonths(1);
            plan.setPeriodTo(periodTo);
        }

        return plan;
    }

    @Override
    public void reschedule(RescheduleCommand command) {
        loanReschedulingService.createLoanRescheduling(command);
        ReschedulingPreview preview = command.getPreview();
        log.info("Rescheduling loan: [{}]", command);
        Loan loan = loanService.getLoan(command.getLoanId());
        List<Installment> installments = scheduleService.findInstallments(openInstallments(loan.getId()));
        installments.forEach(installment -> loanService.cancelInstallment(new CancelInstallmentCommand()
            .setBroken(true)
            .setCancelDate(installment.getValueDate())
            .setInstallmentId(installment.getId())
            .setReverseAppliedAmounts(false)
        ));
        BigDecimal scheduledPenalties = preview.getItems().stream()
            .map(ReschedulingPreview.Item::getPenaltyScheduled)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal unscheduledPenalties = transactionService.getBalance(byLoan(loan.getId())).getPenaltyOutstanding().subtract(scheduledPenalties);
        if (isPositive(unscheduledPenalties)) {
            loanService.writeOffAmount(new WriteOffAmountCommand()
                .setLoanId(loan.getId())
                .setPenalty(unscheduledPenalties)
                .setWhen(command.getWhen())
                .setComments("Rescheduling")
            );
        }

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.RESCHEDULE_LOAN);
        tx.setValueDate(command.getWhen());
        transactionBuilder.addLoanValues(loan, tx);
        Long txId = transactionService.addTransaction(tx);

        Long newContractId = scheduleService.addContract(new AddLoanContractCommand()
            .setLoanId(loan.getId())
            .setProductId(loan.getProductId())
            .setClientId(loan.getClientId())
            .setApplicationId(loan.getApplicationId())
            .setContractDate(command.getWhen())
            .setEffectiveDate(command.getWhen())
            .setMaturityDate(Iterables.getLast(preview.getItems()).getDueDate())
            .setPeriodCount((long) preview.getItems().size())
            .setPeriodUnit(PeriodUnit.MONTH)
            .setNumberOfInstallments((long) preview.getItems().size())
            .setCloseLoanOnPaid(true)
            .setSourceTransactionType(TransactionType.RESCHEDULE_LOAN)
            .setSourceTransactionId(txId)
        );

        for (ReschedulingPreview.Item item : preview.getItems()) {
            String installmentNumber = generateInstallmentNumber(loan, item);

            AddInstallmentCommand addInstallment = InstallmentCommandFactory
                .addInstallment(newContractId, item, installmentNumber);
            loanService.addInstallment(addInstallment);
        }
        loanService.resolveLoanDerivedValues(loan.getId(), today());

        Balance balance = transactionService.getBalance(byLoan(loan.getId()));
        Validate.isEqual(balance.getPrincipalOutstanding(), balance.getPrincipalDue(), "All principal should be rescheduled");
        Validate.isEqual(balance.getInterestOutstanding(), balance.getInterestDue(), "All interest should be rescheduled");

        sendReschedulingTocNotification(loan.getId());
        dcService.findByLoanId(loan.getId()).map(Debt::getId).ifPresent(dcService::triggerActions);
    }

    @Override
    public void breakRescheduling(BreakReschedulingCommand command) {
        Loan loan = loanService.getLoan(command.getLoanId());
        isTrue(loan.getStatusDetail() == LoanStatusDetail.RESCHEDULED,
            "Loan [%s] is not in RESCHEDULED state: [%s]", loan.getId(), loan.getStatusDetail());

        Contract contract = scheduleService.getCurrentContract(command.getLoanId());
        List<Installment> installments = scheduleService.findInstallments(new InstallmentQuery().setContractId(contract.getId()));

        installments.forEach(installment -> {
            if (installment.getStatus() == InstallmentStatus.OPEN) {
                loanService.cancelInstallment(
                    new CancelInstallmentCommand()
                        .setBroken(true)
                        .setCancelDate(installment.getValueDate())
                        .setInstallmentId(installment.getId()));
            }
        });

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.BREAK_LOAN_RESCHEDULE);
        tx.setValueDate(command.getWhen());

        transactionBuilder.addLoanValues(loan, tx);
        Contract previousContract = scheduleService.getContract(contract.getPreviousContractId());

        Long txId = transactionService.addTransaction(tx);
        LocalDate maturityDate = transactionService.lastPaidTransaction(loan.getId()).map(Transaction::getValueDate).orElse(previousContract.getMaturityDate());
        Long newContractId = scheduleService.addContract(new AddLoanContractCommand()
            .setLoanId(loan.getId())
            .setProductId(loan.getProductId())
            .setClientId(loan.getClientId())
            .setApplicationId(loan.getApplicationId())
            .setContractDate(command.getWhen())
            .setEffectiveDate(command.getWhen())
            .setMaturityDate(maturityDate)
            .setPeriodCount(0L)
            .setPeriodUnit(PeriodUnit.DAY)
            .setNumberOfInstallments(1L)
            .setCloseLoanOnPaid(true)
            .setSourceTransactionId(txId)
            .setSourceTransactionType(tx.getTransactionType())
            // dpd count will be calculated during resolve installment derived values
        );

        Balance balance = transactionService.getBalance(byLoan(command.getLoanId()));
        AddInstallmentCommand addInstallmentCommand = new AddInstallmentCommand()
            .setTransactionType(TransactionType.INSTALLMENT)
            .setContractId(newContractId)
            .setPeriodFrom(command.getWhen())
            .setPeriodTo(command.getWhen())
            .setDueDate(maturityDate)
            .setValueDate(command.getWhen())
            .setInstallmentSequence(1L)
            .setInstallmentNumber(randomAlphabetic(8))
            .setPrincipalInvoiced(balance.getPrincipalOutstanding())
            .setInterestInvoiced(balance.getInterestOutstanding())
            .setPenaltyInvoiced(balance.getPenaltyOutstanding());
        loanService.addInstallment(addInstallmentCommand);

        loanService.resolveLoanDerivedValues(loan.getId(), today());

        loanReschedulingService.cancel(loan.getId(), today());
        dcService.findByLoanId(loan.getId()).map(Debt::getId).ifPresent(dcService::triggerActions);
    }

    @Override
    public void externalize(Long debtId, String company) {
        Companies companies = dcSettingsService.getSettings().getCompanies();
        isTrue(companies.mangingCompanyDefined(company), "No owning company found");

        Debt debt = dcService.get(debtId);
        isTrue(!isSold(debt), format("Already sold to %s.", debt.getOwningCompany()));
        isTrue(!dcService.isExternalized(debt), format("Already externalized to %s.", debt.getManagingCompany()));

        dcService.changeCompany(new ChangeCompanyCommand()
            .setDebtId(debtId)
            .setOwningCompany(companies.getDefaultOwningCompany())
            .setManagingCompany(company)
            .setOperationType("externalizing")
            .setPortfolio(DcConstants.EXTERNALIZED_PORTFOLIO)
        );

        dcService.unassignDebt(new UnassignDebtCommand()
            .setDebtId(debtId)
            .setComment("Debt externalized")
        );

        dcService.triggerActions(debtId);
    }

    protected void sendLoanSoldNotification(Debt debt) {
        alfaNotificationBuilderFactory.fromExtraLegal(debt.getClientId())
            .loanId(debt.getLoanId())
            .debtId(debt.getId())
            .render(CmsSetup.LOAN_SOLD_NOTIFICATION, ImmutableMap.of(AlfaCmsModels.SCOPE_COMPANY, cmsModels.company(),
                AlfaCmsModels.SCOPE_CLIENT, cmsModels.client(debt.getClientId()), AlfaCmsModels.SCOPE_LOAN, cmsModels.loan(debt.getLoanId())))
            .send();
    }

    @Override
    public void reassign(ReassignDebtCommand command) {
        Companies companies = dcSettingsService.getSettings().getCompanies();
        Debt debt = dcService.get(command.getDebtId());

        notBlank(debt.getPortfolio(), "Portfolio can't be empty");
        isTrue(reassignablePortfolios.contains(debt.getPortfolio()), "%s is not reassignable portfolio", debt.getPortfolio());
        isTrue(!isSold(debt), format("Sold to %s.", debt.getOwningCompany()));
        isTrue(!dcService.isExternalized(debt), format("Externalized to %s.", debt.getManagingCompany()));

        EditDebtCommand editDebtCommand = new EditDebtCommand();
        copyProperties(editDebtCommand, command);
        editDebtCommand.setOperationType("reassigning");
        editDebtCommand.setManagingCompany(companies.getDefaultManagingCompany());
        editDebtCommand.setOwningCompany(companies.getDefaultOwningCompany());
        dcService.edit(editDebtCommand);

        boolean autoAssign = StringUtils.isBlank(command.getAgent());
        doAssignDebt(debt.getId(), autoAssign, command.getAgent());
    }

    private void sendReschedulingTocNotification(Long loanId) {
        Loan loan = loanService.getLoan(loanId);

        Map<String, Object> cmsContext = dcService.findByLoanId(loanId)
            .map(debt -> cmsModels.debtContext(debt.getId()))
            .orElseGet(() -> cmsModels.loanContext(loan.getId()));

        Pdf pdf = pdfRenderer.renderRequired(CmsSetup.RESCHEDULING_TOC_PDF, cmsContext, AlfaConstants.LOCALE);
        CloudFile file = fileStorageService.save(
            FileStorageCommandFactory.fromPdf(AlfaConstants.FILE_DIRECTORY_AGREEMENTS, pdf)
        );
        addClientAttachment(loan, file);

        alfaNotificationBuilderFactory.fromLoan(loan.getId())
            .emailAttachmentFileIds(ImmutableList.of(file.getFileId()))
            .loanId(loan.getId())
            .render(CmsSetup.RESCHEDULING_TOC_NOTIFICATION, cmsContext)
            .send();
    }

    private void addClientAttachment(Loan loan, CloudFile pdfFile) {
        AddAttachmentCommand addAttachment = new AddAttachmentCommand();
        addAttachment.setLoanId(loan.getId());
        addAttachment.setClientId(loan.getClientId());
        addAttachment.setFileId(pdfFile.getFileId());
        addAttachment.setStatus(AttachmentStatus.OK);
        addAttachment.setAttachmentType(AlfaConstants.ATTACHMENT_TYPE_RESCHEDULING_TOC);
        addAttachment.setName(pdfFile.getOriginalFileName());
        attachmentService.addAttachment(addAttachment);
    }

}
