package fintech.spain.alfa.product.workflow.undewrtiting.predicates


import fintech.spain.alfa.product.db.Entities
import fintech.spain.alfa.product.db.IdentificationDocumentEntity
import fintech.spain.alfa.product.db.IdentificationDocumentRepository
import fintech.workflow.spi.ActivityContext
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import spock.lang.Specification
import spock.lang.Unroll

import static org.mockito.Mockito.when

class SkipIdRevalidationPredicateTest extends Specification {

    @InjectMocks
    SkipIdRevalidationPredicate predicate

    @Mock
    IdentificationDocumentRepository identificationDocumentRepository

    @Mock
    ActivityContext context

    void setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Unroll
    def "should skip id revalidation if no valid document exist"() {
        given:
        when(context.getClientId()).thenReturn(1L)
        when(identificationDocumentRepository.findAll(Entities.identificationDocument.clientId.eq(1L))).thenReturn(docs)

        when:
        def result = predicate.test(context)

        then:
        result == expectedResult

        where:
        docs                                             | expectedResult
        []                                               | true
        [new IdentificationDocumentEntity(valid: false)] | true
        [new IdentificationDocumentEntity(valid: true)]  | false

    }
}
