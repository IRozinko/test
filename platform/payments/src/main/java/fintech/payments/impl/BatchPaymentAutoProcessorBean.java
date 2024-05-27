package fintech.payments.impl;

import fintech.payments.PaymentService;
import fintech.payments.db.Entities;
import fintech.payments.db.PaymentEntity;
import fintech.payments.db.PaymentRepository;
import fintech.payments.model.PaymentStatusDetail;
import fintech.payments.spi.BatchPaymentAutoProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class BatchPaymentAutoProcessorBean implements BatchPaymentAutoProcessor {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public void autoProcessPending(int batchSize, LocalDate when) {
        Page<PaymentEntity> page = paymentRepository.findAll(Entities.payment.statusDetail.eq(PaymentStatusDetail.PENDING), new QPageRequest(0, batchSize, Entities.payment.id.asc()));
        List<PaymentEntity> payments = page.getContent();
        if (!payments.isEmpty()) {
            log.info("Found [{}] pending payments to auto-process", payments.size());
        }
        payments.forEach(payment -> {
            try {
                paymentService.autoProcess(payment.getId(), when);
            } catch (Exception e) {
                log.warn("Failed to auto process payment", e);
                paymentService.requireManualProcessing(payment.getId());
            }
        });
    }
}
