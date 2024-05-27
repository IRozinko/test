package fintech.lending.creditline.validators

import fintech.DateUtils
import fintech.TimeMachine
import fintech.lending.creditline.settings.CreditLineInterestSettings
import fintech.lending.creditline.settings.CreditLinePricingSettings
import org.mockito.Mockito
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.ConstraintValidatorContext

class InterestRatesValidatorTest extends Specification {

    ConstraintValidatorContext context

    def setup() {
        // mock the context
        context = Mockito.mock(ConstraintValidatorContext.class);
        def builder = Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        def node = Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(builder);
        Mockito.when(builder.addConstraintViolation()).thenReturn(context);
        Mockito.when(builder.addPropertyNode(Mockito.anyString())).thenReturn(node);
    }

    @Unroll
    def "Check interest rates validation"() {
        given:
        def validator = new InterestRatesValidator()

        when:
        def valid = validator.isValid(new CreditLinePricingSettings(interestRatePerYearPercent: settings), context)

        then:
        valid == result

        where:
        settings                                                                                                                                                                                              | result
        []                                                                                                                                                                                                    | false
        [new CreditLineInterestSettings()]                                                                                                                                                                    | false
        [new CreditLineInterestSettings(), new CreditLineInterestSettings()]                                                                                                                                  | false
        [new CreditLineInterestSettings(startDate: TimeMachine.today(), ratePerYearPercent: 10.0), new CreditLineInterestSettings()]                                                                          | false
        [new CreditLineInterestSettings(startDate: TimeMachine.today(), ratePerYearPercent: 10.0), new CreditLineInterestSettings(startDate: TimeMachine.today(), ratePerYearPercent: 10.0)]                  | false
        [new CreditLineInterestSettings(startDate: TimeMachine.today(), ratePerYearPercent: 10.0)]                                                                                                            | false
        [new CreditLineInterestSettings(startDate: TimeMachine.today().plusDays(1), ratePerYearPercent: 10.0)]                                                                                                | false
        [new CreditLineInterestSettings(startDate: TimeMachine.today().plusDays(1), ratePerYearPercent: 10.0), new CreditLineInterestSettings()]                                                              | false
        [new CreditLineInterestSettings(startDate: DateUtils.farFarInPast(), ratePerYearPercent: 10.0)]                                                                                                       | true
        [new CreditLineInterestSettings(startDate: DateUtils.farFarInPast(), ratePerYearPercent: 10.0), new CreditLineInterestSettings(startDate: TimeMachine.today().plusDays(1), ratePerYearPercent: 10.0)] | true
    }
}
