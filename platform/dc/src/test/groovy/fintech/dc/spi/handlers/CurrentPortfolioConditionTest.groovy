package fintech.dc.spi.handlers

import fintech.dc.spi.ConditionContext
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import spock.lang.Specification
import spock.lang.Unroll

class CurrentPortfolioConditionTest extends Specification {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    ConditionContext context

    void setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Unroll("return #expected when portfolios match (#portfolioActual == #portfolioRequired)")
    def "should return true if portfolio match"() {
        given:
        Mockito.when(context.getRequiredParam("portfolio", String.class)).thenReturn(portfolioRequired)
        Mockito.when(context.getDebt().getPortfolio()).thenReturn(portfolioActual)

        when:
        def result = new CurrentPortfolioCondition().apply(context)

        then:
        result == expected

        where:
        portfolioRequired | portfolioActual | expected
        "match"           | "match"         | true
        "no-match"        | "match"         | false
    }
}
