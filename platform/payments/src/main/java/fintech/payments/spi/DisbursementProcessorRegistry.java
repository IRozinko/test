package fintech.payments.spi;

public interface DisbursementProcessorRegistry {

    DisbursementBatchProcessor findBatchProcessor(Long disbursementId);

    DisbursementBatchProcessor findBatchProcessor(Long institutionId, Long institutionAccountId);

}
