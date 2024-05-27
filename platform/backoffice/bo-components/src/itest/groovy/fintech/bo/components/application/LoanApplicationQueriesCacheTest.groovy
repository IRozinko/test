package fintech.bo.components.application

import com.vaadin.ui.Grid
import fintech.bo.components.PropertyLayout
import fintech.bo.components.dc.DcComponents
import fintech.bo.components.transaction.PaymentTransactionDataProvider
import fintech.bo.components.transaction.TransactionComponents
import fintech.bo.components.transaction.TransactionDataProvider
import fintech.bo.db.jooq.dc.tables.records.DebtRecord
import fintech.testing.integration.AbstractBaseSpecification
import org.jooq.Record
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@ActiveProfiles("applications-queries")
class LoanApplicationQueriesCacheTest extends AbstractBaseSpecification {

    static def sources1 = ["s1", "s2"]
    static def sources2 = ["s3", "s4"]

    static def types1 = ["t1", "t2"]
    static def types2 = ["t3", "t4"]

    @Autowired
    LoanApplicationQueries queries

    def "FindSourceNames"() {
        when:
        def sources = queries.findSourceNames()

        then:
        sources == sources1

        when:
        sources = queries.findSourceNames()

        then:
        sources == sources1
    }

    def "FindTypes"() {
        when:
        def types = queries.findTypes()

        then:
        types == types1

        when:
        types = queries.findTypes()

        then:
        types == types1
    }

    @Configuration
    @Profile("applications-queries")
    static class ContextConfiguration {

        @Bean
        @Primary
        LoanApplicationQueries orderService() {
            def queries = mock(LoanApplicationQueries.class)

            when(queries.findSourceNames())
                .thenReturn(sources1)
                .thenReturn(sources2)

            when(queries.findTypes())
                .thenReturn(types1)
                .thenReturn(types2)

            return queries
        }

        @Bean
        @Primary
        DcComponents dcComponents() {

            return new DcComponents(null, null, null, null, null) {

                @Override
                PropertyLayout debtInfo(DebtRecord debt) {
                    return null
                }
            }
        }

        @Bean
        @Primary
        TransactionComponents transactionComponents() {
            return new TransactionComponents(null, null, null) {
                @Override
                Grid<Record> grid(TransactionDataProvider dataProvider) {
                    return null
                }

                @Override
                Grid<Record> paymentTransactionsGrid(PaymentTransactionDataProvider dataProvider) {
                    return null
                }
            }
        }

    }

}
