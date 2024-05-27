package fintech.lending.creditline.validators;

import fintech.DateUtils;
import fintech.lending.creditline.settings.CreditLineInterestSettings;
import fintech.lending.creditline.settings.CreditLinePricingSettings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class InterestRatesValidator implements ConstraintValidator<ValidInterestsRate, CreditLinePricingSettings> {

    @Override
    public void initialize(ValidInterestsRate constraintAnnotation) {

    }

    @Override
    public boolean isValid(CreditLinePricingSettings value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        List<CreditLineInterestSettings> interestSettings = value.getInterestRatePerYearPercent();

        if (interestSettings == null || interestSettings.isEmpty()) {
            context.buildConstraintViolationWithTemplate("Null values not allowed")
                .addPropertyNode("interestRatePerYearPercent")
                .addConstraintViolation();
            return false;
        }

        boolean nullRates = interestSettings.stream().anyMatch(is -> is.getRatePerYearPercent() == null);
        if (nullRates) {
            context.buildConstraintViolationWithTemplate("Null values not allowed")
                .addPropertyNode("interestRatePerYearPercent.ratePerYearPercent")
                .addConstraintViolation();
            return false;
        }

        boolean nullDates = interestSettings.stream().anyMatch(is -> is.getStartDate() == null);
        if (nullDates) {
            context.buildConstraintViolationWithTemplate("Null values not allowed")
                .addPropertyNode("interestRatePerYearPercent.startDate")
                .addConstraintViolation();
            return false;
        }

        List<CreditLineInterestSettings> sorted = interestSettings.stream().sorted(Comparator.comparing(CreditLineInterestSettings::getStartDate)).collect(Collectors.toList());
        if (sorted.get(0).getStartDate().isAfter(DateUtils.farFarInPast())) {
            context.buildConstraintViolationWithTemplate("First date is not old enough")
                .addPropertyNode("interestRatePerYearPercent.startDate")
                .addConstraintViolation();
            return false;
        }

        boolean duplicated = !sorted.stream().map(CreditLineInterestSettings::getStartDate).allMatch(new HashSet<>()::add);
        if (duplicated) {
            context.buildConstraintViolationWithTemplate("Duplicated start dates")
                .addPropertyNode("interestRatePerYearPercent.startDate")
                .addConstraintViolation();
            return false;
        }

        boolean negativeInterests = sorted.stream().anyMatch(is -> is.getRatePerYearPercent().compareTo(BigDecimal.ZERO) < 0);
        if (negativeInterests) {
            context.buildConstraintViolationWithTemplate("Negative interest rate")
                .addPropertyNode("interestRatePerYearPercent.ratePerYearPercent")
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}
