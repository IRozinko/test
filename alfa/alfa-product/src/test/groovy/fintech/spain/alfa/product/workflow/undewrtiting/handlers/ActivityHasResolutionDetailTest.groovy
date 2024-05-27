package fintech.spain.alfa.product.workflow.undewrtiting.handlers

import fintech.workflow.spi.ActivityContext
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import spock.lang.Specification
import spock.lang.Unroll

class ActivityHasResolutionDetailTest extends Specification {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    ActivityContext context

    void setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Unroll
    def "IsTrueFor"() {
        given:
        Mockito.when(context.getWorkflow().activity("activity1").getResolution()).thenReturn(resolutionDetail)

        expect:
        new ActivityHasResolution("activity1", "res1").isTrueFor(context) == expectedResult


        where:
        resolutionDetail | expectedResult
        "res1"           | true
        "res2"           | false
        "abrakadabra"    | false
    }
}
