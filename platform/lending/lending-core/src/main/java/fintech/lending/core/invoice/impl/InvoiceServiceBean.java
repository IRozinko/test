package fintech.lending.core.invoice.impl;

import com.google.common.collect.Maps;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.AmountForPayment;
import fintech.DateUtils;
import fintech.Validate;
import fintech.lending.core.invoice.Invoice;
import fintech.lending.core.invoice.InvoiceItem;
import fintech.lending.core.invoice.InvoiceQuery;
import fintech.lending.core.invoice.InvoiceService;
import fintech.lending.core.invoice.InvoiceStatus;
import fintech.lending.core.invoice.commands.CloseInvoiceCommand;
import fintech.lending.core.invoice.commands.GeneratedInvoice;
import fintech.lending.core.invoice.commands.GeneratedInvoice.GeneratedInvoiceItem;
import fintech.lending.core.invoice.commands.UpdateInvoiceCommand;
import fintech.lending.core.invoice.db.InvoiceEntity;
import fintech.lending.core.invoice.db.InvoiceItemEntity;
import fintech.lending.core.invoice.db.InvoiceItemType;
import fintech.lending.core.invoice.db.InvoiceRepository;
import fintech.lending.core.invoice.events.InvoiceClosedEvent;
import fintech.lending.core.invoice.events.InvoiceCreatedEvent;
import fintech.lending.core.invoice.events.InvoiceUpdatedEvent;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.commands.AddInstallmentCommand;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.TransactionEntryType;
import fintech.transactions.TransactionType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.querydsl.core.types.ExpressionUtils.allOf;
import static fintech.BigDecimalUtils.amountForPayment;
import static fintech.DateUtils.goe;
import static fintech.lending.core.db.Entities.invoice;
import static fintech.lending.core.invoice.db.InvoiceItemType.FEE;
import static fintech.lending.core.invoice.db.InvoiceItemType.INTEREST;
import static fintech.lending.core.invoice.db.InvoiceItemType.PENALTY;
import static fintech.lending.core.invoice.db.InvoiceItemType.PRINCIPAL;
import static java.util.stream.Collectors.toList;

@Transactional
@Component
@Slf4j
class InvoiceServiceBean implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final JPAQueryFactory queryFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final LoanService loanService;
    private final ScheduleService scheduleService;

    @Autowired
    public InvoiceServiceBean(InvoiceRepository invoiceRepository, JPAQueryFactory queryFactory, ApplicationEventPublisher eventPublisher,
                              LoanService loanService, ScheduleService scheduleService) {
        this.invoiceRepository = invoiceRepository;
        this.queryFactory = queryFactory;
        this.eventPublisher = eventPublisher;
        this.loanService = loanService;
        this.scheduleService = scheduleService;
    }

    @Override
    public Invoice get(@NonNull Long invoiceId) {
        return invoiceRepository.getRequired(invoiceId).toValueObject();
    }

    @Override
    public Optional<Invoice> findLastOpenInvoice(@NonNull Long loanId) {
        InvoiceEntity invoiceEntity = queryFactory
            .selectFrom(invoice)
            .where(invoice.loanId.eq(loanId)
                .and(invoice.status.eq(InvoiceStatus.OPEN)))
            .orderBy(invoice.periodFrom.desc())
            .fetchFirst();

        return Optional.ofNullable(invoiceEntity).map(InvoiceEntity::toValueObject);
    }

    @Override
    public Optional<Invoice> findFirstOpenInvoice(@NonNull Long loanId) {
        InvoiceEntity invoiceEntity = queryFactory
            .selectFrom(invoice)
            .where(invoice.loanId.eq(loanId)
                .and(invoice.status.eq(InvoiceStatus.OPEN)))
            .orderBy(invoice.periodFrom.asc())
            .fetchFirst();
        return Optional.ofNullable(invoiceEntity).map(InvoiceEntity::toValueObject);
    }

    @Override
    public List<Invoice> find(InvoiceQuery query) {
        List<Predicate> predicates = toPredicates(query);

        return invoiceRepository.findAll(allOf(predicates), invoice.periodFrom.asc())
            .stream()
            .map(InvoiceEntity::toValueObject)
            .collect(toList());
    }

    @Override
    public Long createInvoice(GeneratedInvoice command) {
        log.info("Creating invoice [{}]", command);
        requireValid(command);

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setProductId(command.getProductId());
        invoice.setClientId(command.getClientId());
        invoice.setLoanId(command.getLoanId());
        invoice.setNumber(command.getNumber());
        invoice.setInvoiceDate(command.getInvoiceDate());
        invoice.setDueDate(command.getDueDate());
        invoice.setPeriodFrom(command.getPeriodFrom());
        invoice.setPeriodTo(command.getPeriodTo());
        invoice.setGenerateFile(command.isGenerateFile());
        invoice.setSendFile(command.isSendFile());
        invoice.setMembershipLevelChanged(command.getMembershipLevelChecked());
        invoice.setManual(command.isManual());

        Map<InvoiceItem, GeneratedInvoiceItem> itemMapping = Maps.newHashMap();
        for (GeneratedInvoiceItem itemCommand : command.getItems()) {
            InvoiceItemEntity item = addItem(invoice, itemCommand);
            itemMapping.put(item.toValueObject(), itemCommand);
        }

        invoiceRepository.saveAndFlush(invoice);

        Invoice invoiceValueObject = invoice.toValueObject();
        addInstallmentTransaction(invoiceValueObject, itemMapping);

        eventPublisher.publishEvent(new InvoiceCreatedEvent(invoiceValueObject));
        return invoice.getId();
    }

    private InvoiceItemEntity addItem(InvoiceEntity invoice, GeneratedInvoiceItem itemCommand) {
        InvoiceItemEntity item = new InvoiceItemEntity();
        item.setInvoice(invoice);
        AmountForPayment amount = amountForPayment(itemCommand.getAmount());
        item.setAmount(amount.getRoundedAmount());
        item.setType(itemCommand.getType());
        item.setSubType(itemCommand.getSubType());
        item.setLoanId(invoice.getLoanId());
        item.setCorrection(itemCommand.isCorrection());
        invoice.addItem(item);
        return item;
    }

    @Override
    public void updateInvoice(UpdateInvoiceCommand command) {
        Validate.isTrue(!command.getItems().isEmpty(), "Required correction items");

        InvoiceEntity invoice = invoiceRepository.getRequired(command.getInvoiceId());
        log.info("Updating invoice [{}] with items [{}]", invoice, command.getItems());
        invoice.setGenerateFile(command.isGenerateFile());
        invoice.setSendFile(command.isSendFile());
        invoice.corrected();

        Map<InvoiceItem, GeneratedInvoiceItem> itemMapping = Maps.newHashMap();
        List<GeneratedInvoiceItem> items = command.getItems();
        items.forEach(item -> item.setCorrection(true));
        for (GeneratedInvoiceItem itemCommand : items) {
            InvoiceItemEntity item = addItem(invoice, itemCommand);
            itemMapping.put(item.toValueObject(), itemCommand);
        }

        addInstallmentTransaction(invoice.toValueObject(), itemMapping);

        eventPublisher.publishEvent(new InvoiceUpdatedEvent(invoice.toValueObject()));
    }

    @Override
    public void closeInvoice(@Valid CloseInvoiceCommand command) {
        InvoiceEntity invoice = invoiceRepository.getRequired(command.getInvoiceId());
        log.info("Closing invoice [{}] with status detail [{}]", invoice, command.getStatusDetail());

        Validate.isTrue(invoice.isOpen(), "Can close only open invoice");
        Optional<Invoice> lastInvoice = findLastOpenInvoice(invoice.getLoanId());
        Validate.isTrue(lastInvoice.isPresent(), "Can close only next open invoice");
        Validate.isTrue(invoice.getId().equals(lastInvoice.get().getId()), "Can close only next open invoice");

        invoice.close(command.getStatusDetail(), command.getDate(), command.getReason());
        eventPublisher.publishEvent(new InvoiceClosedEvent(invoice.toValueObject()));
    }

    @Override
    public void invoiceFileGenerated(Long invoiceId, Long fileId, String fileName) {
        InvoiceEntity invoice = invoiceRepository.getRequired(invoiceId);
        invoice.setFileId(fileId);
        invoice.setFileName(fileName);
        invoice.setGenerateFile(false);
    }

    @Override
    public void invoiceFileSent(Long invoiceId, LocalDateTime when) {
        InvoiceEntity invoice = invoiceRepository.getRequired(invoiceId);
        invoice.setSendFile(false);
        invoice.setSentAt(when);
    }

    @Override
    public void generateFile(Long invoiceId) {
        InvoiceEntity invoice = invoiceRepository.getRequired(invoiceId);
        invoice.setGenerateFile(true);
    }

    @Override
    public void sendFile(Long invoiceId) {
        InvoiceEntity invoice = invoiceRepository.getRequired(invoiceId);
        invoice.setSendFile(true);
    }

    @Override
    public List<Invoice> findForMembershipLevel(Long clientId) {
        JPQLQuery<LocalDate> lastMembershipLevelChange = JPAExpressions
            .select(invoice.periodFrom.max().coalesce(DateUtils.farFarInPast()))
            .from(invoice)
            .where(invoice.membershipLevelChanged.isTrue());

        return queryFactory.selectFrom(invoice)
            .where(invoice.clientId.eq(clientId)
                .and(invoice.periodFrom.after(lastMembershipLevelChange)))
            .orderBy(invoice.periodFrom.asc())
            .fetch()
            .stream()
            .map(InvoiceEntity::toValueObject)
            .collect(toList());
    }

    @Override
    public void markMembershipLevelChanged(Long invoiceId) {
        InvoiceEntity invoice = invoiceRepository.getRequired(invoiceId);
        invoice.setMembershipLevelChanged(true);
    }

    private List<Predicate> toPredicates(InvoiceQuery query) {
        List<Predicate> predicates = new ArrayList<>();

        if (query.getClientId() != null) {
            predicates.add(invoice.clientId.eq(query.getClientId()));
        }
        if (query.getLoanId() != null) {
            predicates.add(invoice.loanId.eq(query.getLoanId()));
        }
        if (query.getStatus() != null) {
            predicates.add(invoice.status.eq(query.getStatus()));
        }
        if (query.getInvoiceDateFrom() != null) {
            predicates.add(invoice.invoiceDate.goe(query.getInvoiceDateFrom()));
        }
        if (query.getInvoiceDateTo() != null) {
            predicates.add(invoice.invoiceDate.loe(query.getInvoiceDateTo()));
        }
        if (query.getStatusDetailArrayList() != null && !query.getStatusDetailArrayList().isEmpty()) {
            predicates.add(invoice.statusDetail.in(query.getStatusDetailArrayList()));
        }
        if (query.getVoided() != null) {
            predicates.add(invoice.voided.eq(query.getVoided()));
        }
        if (query.getPeriodToTo() != null) {
            predicates.add(invoice.periodTo.loe(query.getPeriodToTo()));
        }
        if (query.getDueDateFrom() != null) {
            predicates.add(invoice.dueDate.goe(query.getDueDateFrom()));
        }
        if (query.getDueDateTo() != null) {
            predicates.add(invoice.dueDate.loe(query.getDueDateTo()));
        }
        if (query.getClosedFrom() != null) {
            predicates.add(invoice.closeDate.goe(query.getClosedFrom()));
        }
        if (query.getClosedTo() != null) {
            predicates.add(invoice.closeDate.loe(query.getClosedTo()));
        }
        if (query.getNumber() != null) {
            predicates.add(invoice.number.eq(query.getNumber()));
        }
        if (query.getManual() != null) {
            predicates.add(invoice.manual.eq(query.getManual()));
        }
        return predicates;
    }

    private void requireValid(GeneratedInvoice command) {
        Optional<LocalDate> nextInvoicePeriodFrom = getNextInvoicePeriodFrom(command.getLoanId());
        Validate.isTrue(goe(command.getPeriodTo(), command.getPeriodFrom()),
            "Invalid invoice period from [%s] to [%s]", command.getPeriodFrom(), command.getPeriodTo());

        nextInvoicePeriodFrom.ifPresent(localDate -> Validate.isTrue(command.getPeriodFrom().isEqual(localDate),
            "Invalid invoice period from %s, should be %s", command.getPeriodFrom(), localDate));

        Validate.isTrue(itemCount(command, PRINCIPAL) <= 1, "Principal items can't be more than 1");
        Validate.isTrue(itemCount(command, INTEREST) <= 1, "Interest items can't be more than 1");
        Validate.isTrue(itemCount(command, PENALTY) <= 1, "Penalty items can't be more than 1");
    }

    private Optional<LocalDate> getNextInvoicePeriodFrom(Long loanId) {
        LocalDate maxPeriodTo = queryFactory
            .select(invoice.periodTo.max())
            .from(invoice)
            .where(invoice.loanId.eq(loanId).and(invoice.voided.eq(false)))
            .fetchOne();

        return maxPeriodTo == null ? Optional.empty() : Optional.of(maxPeriodTo.plusDays(1));
    }

    private long itemCount(GeneratedInvoice command, InvoiceItemType type) {
        return command.getItems().stream().filter(itemCommand -> itemCommand.getType() == type).count();
    }

    private void addInstallmentTransaction(Invoice invoice, Map<InvoiceItem, GeneratedInvoiceItem> itemMapping) {
        Contract contract = scheduleService.getCurrentContract(invoice.getLoanId());
        List<Installment> installments = scheduleService.findInstallments(new InstallmentQuery().setContractId(contract.getId()));
        AddInstallmentCommand addInstallmentCommand = new AddInstallmentCommand()
            .setTransactionType(TransactionType.INVOICE)
            .setContractId(contract.getId())
            .setInvoiceId(invoice.getId())
            .setPeriodFrom(invoice.getPeriodFrom())
            .setPeriodTo(invoice.getPeriodTo())
            .setDueDate(invoice.getDueDate())
            .setValueDate(invoice.getPeriodTo())
            .setInstallmentSequence((long) (installments.size() + 1))
            .setInstallmentNumber(invoice.getNumber());

        findItem(itemMapping, PRINCIPAL).ifPresent(principalItem -> {
            GeneratedInvoiceItem generatedInvoiceItem = itemMapping.get(principalItem);
            AmountForPayment principalAmount = amountForPayment(generatedInvoiceItem.getAmount());
            addInstallmentCommand.setPrincipalInvoiced(principalAmount.getRoundedAmount());
            // not writing off the principal when invoicing, in case needed should be done manually when the loan is fully paid
            // addInstallmentCommand.setPrincipalWrittenOff(principalAmount.getRoundingDifferenceAmount());
        });
        findItem(itemMapping, INTEREST).ifPresent(interestItem -> {
            GeneratedInvoiceItem generatedInvoiceItem = itemMapping.get(interestItem);
            AmountForPayment interestAmount = amountForPayment(generatedInvoiceItem.getAmount());
            addInstallmentCommand.setInterestInvoiced(interestAmount.getRoundedAmount());
            addInstallmentCommand.setInterestWrittenOff(interestAmount.getRoundingDifferenceAmount());
        });
        findItem(itemMapping, PENALTY).ifPresent(penaltyItem -> {
            GeneratedInvoiceItem generatedInvoiceItem = itemMapping.get(penaltyItem);
            AmountForPayment penaltyAmount = amountForPayment(generatedInvoiceItem.getAmount());
            addInstallmentCommand.setPenaltyInvoiced(penaltyAmount.getRoundedAmount());
            addInstallmentCommand.setPenaltyWrittenOff(penaltyAmount.getRoundingDifferenceAmount());
        });

        findItems(itemMapping, FEE).forEach(feeItem -> {
            GeneratedInvoiceItem generatedInvoiceItem = itemMapping.get(feeItem);
            AmountForPayment feeAmount = amountForPayment(generatedInvoiceItem.getAmount());
            AddTransactionCommand.TransactionEntry entry = new AddTransactionCommand.TransactionEntry();
            entry.setType(TransactionEntryType.FEE);
            entry.setSubType(feeItem.getSubType());
            entry.setAmountInvoiced(feeAmount.getRoundedAmount());
            entry.setAmountWrittenOff(feeAmount.getRoundingDifferenceAmount());
            addInstallmentCommand.addEntry(entry);
        });

        loanService.addInstallment(addInstallmentCommand);
    }

    private Optional<InvoiceItem> findItem(Map<InvoiceItem, GeneratedInvoiceItem> itemMapping, InvoiceItemType type) {
        return itemMapping.keySet().stream().filter(item -> item.getType() == type).findFirst();
    }

    private List<InvoiceItem> findItems(Map<InvoiceItem, GeneratedInvoiceItem> itemMapping, InvoiceItemType type) {
        return itemMapping.keySet().stream().filter(item -> item.getType() == type).collect(toList());
    }

}
