package fintech.payxpert

import fintech.payxpert.db.PayxpertPaymentRequestRepository
import fintech.payxpert.impl.MockPayxpertProviderBean
import fintech.payxpert.spi.PayxpertBatchJobs
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class PayxpertBaseSpecification extends AbstractBaseSpecification {

    @Autowired
    PayxpertService service

    @Autowired
    PayxpertPaymentRequestRepository repository

    @Autowired
    PayxpertBatchJobs batchJobs

    @Autowired
    MockPayxpertProviderBean mockPayxpertProvider

    def setup() {
        testDatabase.cleanDb()
    }
}
