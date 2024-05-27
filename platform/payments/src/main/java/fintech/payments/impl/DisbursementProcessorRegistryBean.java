package fintech.payments.impl;

import fintech.payments.DisbursementService;
import fintech.payments.model.Disbursement;
import fintech.payments.spi.DisbursementBatchProcessor;
import fintech.payments.spi.DisbursementProcessorRegistry;
import groovy.util.logging.Slf4j;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DisbursementProcessorRegistryBean implements DisbursementProcessorRegistry {

    private final List<DisbursementBatchProcessor> processors;
    private final DisbursementService disbursementService;

    @Override
    public DisbursementBatchProcessor findBatchProcessor(Long disbursementId) {
        Disbursement disbursement = disbursementService.getDisbursement(disbursementId);
        return findBatchProcessor(new FindBatchProcessorRequest(disbursement));
    }

    @Override
    public DisbursementBatchProcessor findBatchProcessor(Long institutionId, Long institutionAccountId) {
        return findBatchProcessor(new FindBatchProcessorRequest(institutionId, institutionAccountId));
    }

    private DisbursementBatchProcessor findBatchProcessor(FindBatchProcessorRequest request) {
        return processors.stream().filter(processor -> processor.isApplicable(request))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("Can't find Disbursement Processor for request: {%s}", request)));
    }

    @Getter
    public static class FindBatchProcessorRequest {

        private final Long disbursementId;
        private final Long institutionId;
        private final Long institutionAccountId;
        private final boolean apiExport;

        public FindBatchProcessorRequest(Long institutionId, Long institutionAccountId) {
            this.institutionId = institutionId;
            this.institutionAccountId = institutionAccountId;
            this.apiExport = false;
            this.disbursementId = null;
        }

        public FindBatchProcessorRequest(Disbursement disbursement) {
            this.apiExport = disbursement.isApiExport();
            this.institutionId = disbursement.getInstitutionId();
            this.institutionAccountId = disbursement.getInstitutionAccountId();
            this.disbursementId = disbursement.getId();
        }
    }
}
