package fintech.payments.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import fintech.BigDecimalUtils;
import fintech.Validate;
import fintech.payments.PaymentService;
import fintech.payments.commands.AddPaymentCommand;
import fintech.payments.db.InstitutionAccountEntity;
import fintech.payments.db.InstitutionAccountRepository;
import fintech.payments.db.PaymentEntity;
import fintech.payments.db.PaymentRepository;
import fintech.payments.events.PaymentCreatedEvent;
import fintech.payments.events.PaymentManualEvent;
import fintech.payments.events.PaymentVoidedEvent;
import fintech.payments.model.Payment;
import fintech.payments.model.PaymentAutoProcessingResult;
import fintech.payments.model.PaymentStatus;
import fintech.payments.model.PaymentStatusDetail;
import fintech.payments.model.UpdatePaymentCommand;
import fintech.payments.spi.PaymentAutoProcessor;
import fintech.payments.spi.PaymentAutoProcessorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.payments.db.Entities.payment;

@Slf4j
@Component
public class PaymentServiceBean implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InstitutionAccountRepository accountRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PaymentAutoProcessorRegistry autoProcessorRegistry;

    @Transactional
    @Override
    public Long addPayment(AddPaymentCommand command) {
        log.info("Adding payment [{}]", command);
        InstitutionAccountEntity account = accountRepository.getRequired(command.getAccountId());

        PaymentEntity payment = toEntity(command);
        payment.setAccount(account);
        payment = paymentRepository.saveAndFlush(payment);

        eventPublisher.publishEvent(new PaymentCreatedEvent(payment.toValueObject()));
        return payment.getId();
    }

    @Override
    @Transactional
    public Long updatePayment(UpdatePaymentCommand command) {
        PaymentEntity payment = paymentRepository.getRequired(command.getPaymentId());
        if (payment.getValueDate().equals(command.getValueDate())) {
            return payment.getId();
        } else {
            payment.setValueDate(command.getValueDate());
            return paymentRepository.save(payment).getId();
        }
    }

    @Transactional
    @Override
    public Payment getPayment(Long id) {
        PaymentEntity entity = paymentRepository.getRequired(id);
        return entity.toValueObject();
    }

    @Transactional
    @Override
    public void voidPayment(Long id) {
        log.info("Voiding payment [{}]", id);
        PaymentEntity entity = paymentRepository.getRequired(id);
        Validate.isEqual(entity.getPendingAmount(), entity.getAmount(), "Can't void payment with processed amount: [%s]", entity);
        entity.close(PaymentStatusDetail.VOIDED);
        eventPublisher.publishEvent(new PaymentVoidedEvent(entity.toValueObject()));
    }

    @Transactional
    @Override
    public void unvoidPayment(Long id) {
        log.info("Unvoiding payment [{}]", id);
        PaymentEntity entity = paymentRepository.getRequired(id);
        Validate.isTrue(entity.getStatusDetail() == PaymentStatusDetail.VOIDED, "Payment is not in VOIDED status detail");
        entity.open(PaymentStatusDetail.MANUAL);
    }


    @Transactional
    @Override
    public void autoProcess(Long id, LocalDate when) {
        Payment payment = getPayment(id);
        log.info("Auto-processing payment [{}]", payment);
        Validate.isTrue(payment.getStatus() == PaymentStatus.OPEN, "Invalid payment status: [%s]", payment);
        Validate.isTrue(BigDecimalUtils.isPositive(payment.getPendingAmount()), "Payment has no pending amount: [%s]", payment);
        Validate.isTrue(BigDecimalUtils.eq(payment.getAmount(), payment.getPendingAmount()), "Payment already has processed amount: [%s]", payment);

        List<PaymentAutoProcessor> processors = autoProcessorRegistry.getProcessors();
        for (PaymentAutoProcessor processor : processors) {
            PaymentAutoProcessingResult autoProcessingResult = processor.autoProcessPayment(payment, when);
            if (autoProcessingResult.isPaymentProcessed()) {
                log.info("Auto-processor [{}] processed payment [{}]", processor, payment);
                return;
            }
        }
        // no transactions added, move to manual
        requireManualProcessing(id);

        log.info("No transactions created by auto-processors for payment [{}]", payment);
    }

    @Transactional
    @Override
    public void requireManualProcessing(Long id) {
        PaymentEntity payment = paymentRepository.getRequired(id);
        Validate.isTrue(payment.getStatusDetail() == PaymentStatusDetail.PENDING, "Invalid payments status: [%s]", payment);
        payment.setStatusDetail(PaymentStatusDetail.MANUAL);
        eventPublisher.publishEvent(new PaymentManualEvent(payment.toValueObject()));
    }

    @Override
    public List<Payment> findPayments(PaymentQuery query) {
        List<Predicate> predicates = getPredicates(query);
        return paymentRepository.findAll(ExpressionUtils.allOf(predicates)).stream().map(PaymentEntity::toValueObject).collect(Collectors.toList());
    }

    @Override
    public Optional<Payment> getOptional(PaymentQuery query) {
        return paymentRepository.getOptional(ExpressionUtils.allOf(getPredicates(query))).map(PaymentEntity::toValueObject);
    }

    private PaymentEntity toEntity(AddPaymentCommand command) {
        PaymentEntity entity = new PaymentEntity()
            .setAmount(command.getAmount())
            .setPendingAmount(command.getAmount())
            .setValueDate(command.getValueDate())
            .setDetails(command.getDetails())
            .setReference(command.getReference())
            .setBankOrderCode(command.getBankOrderCode())
            .setPaymentType(command.getPaymentType())
            .setPostedAt(command.getPostedAt())
            .setKey(command.getKey())
            .setCounterpartyAccount(command.getCounterpartyAccount())
            .setCounterpartyAddress(command.getCounterpartyAddress())
            .setCounterpartyName(command.getCounterpartyName());
        entity.open(command.isRequireManualStatus() ? PaymentStatusDetail.MANUAL : PaymentStatusDetail.PENDING);
        return entity;
    }

    private List<Predicate> getPredicates(PaymentQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getBankOrderCode() != null) {
            predicates.add(payment.bankOrderCode.eq(query.getBankOrderCode()));
        }
        if (query.getPaymentType() != null) {
            predicates.add(payment.paymentType.eq(query.getPaymentType()));
        }
        return predicates;
    }

}
