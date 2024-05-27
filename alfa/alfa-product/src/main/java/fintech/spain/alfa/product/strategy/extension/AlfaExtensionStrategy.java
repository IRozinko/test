package fintech.spain.alfa.product.strategy.extension;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import fintech.BigDecimalUtils;
import fintech.JsonUtils;
import fintech.lending.core.loan.Loan;
import fintech.spain.alfa.product.extension.discounts.ExtensionDiscountOffer;
import fintech.spain.alfa.product.extension.discounts.ExtensionDiscountService;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties;
import fintech.strategy.model.ExtensionOffer;
import fintech.strategy.spi.ExtensionStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static java.math.BigDecimal.ROUND_HALF_UP;

public class AlfaExtensionStrategy implements ExtensionStrategy {

    public static final CalculationType CALCULATION_TYPE = CalculationType.D;

    private final Loan loan;
    private final ExtensionStrategyProperties strategyProperties;
    private final ExtensionDiscountService discountService;

    public AlfaExtensionStrategy(Loan loan, JsonNode properties, ExtensionDiscountService discountService) {
        this.loan = loan;
        this.strategyProperties = JsonUtils.readValue(properties, ExtensionStrategyProperties.class);
        this.discountService = discountService;
    }

    @Override
    public List<ExtensionOffer> getOffers(LocalDate onDate) {
        if (!BigDecimalUtils.isPositive(loan.getPrincipalDisbursed())) {
            return ImmutableList.of();
        }
       ExtensionDiscountOffer  extensionDiscount = discountService.findExtensionDiscount(loan.getId());

        return strategyProperties.getExtensions()
            .stream()
            .map(o -> {
                BigDecimal price =  round(
                    loan.getPrincipalDisbursed()
                        .multiply(o.getRate())
                        .divide(amount(100.00), 6, RoundingMode.HALF_UP));
                BigDecimal extensionDiscountPercent = extensionDiscount.getDiscountInPercent();
                BigDecimal extensionDiscountAmount = round(price.multiply(extensionDiscountPercent).divide(amount(100.00), 6, RoundingMode.HALF_UP));
                BigDecimal priceWithDiscount = price.subtract(extensionDiscountAmount);
               return new ExtensionOffer()
                    .setPeriodCount(o.getTerm())
                    .setPeriodUnit(ChronoUnit.DAYS)
                    .setPrice(price)
                    .setPriceWithDiscount(priceWithDiscount)
                    .setDiscountAmount(extensionDiscountAmount)
                    .setDiscountPercent(extensionDiscountPercent);
                }
            )
            .filter(offer -> loanIsNotOverdueAfterExtension(onDate, loan, offer))
            .collect(Collectors.toList());
    }

    private boolean loanIsNotOverdueAfterExtension(LocalDate onDate, Loan loan, ExtensionOffer offer) {
        LocalDate newMaturityDate = loan.getMaturityDate().plus(offer.getPeriodCount(), offer.getPeriodUnit());
        return !newMaturityDate.isBefore(onDate);
    }

    private BigDecimal round(BigDecimal price) {
        return price.setScale(0, ROUND_HALF_UP);
    }

}
