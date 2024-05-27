package fintech.lending.core.discount.internal;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.lending.core.discount.ApplyDiscountCommand;
import fintech.lending.core.discount.Discount;
import fintech.lending.core.discount.DiscountService;
import fintech.lending.core.discount.db.DiscountEntity;
import fintech.lending.core.discount.db.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static fintech.lending.core.db.Entities.discount;
import static fintech.lending.core.db.Entities.loan;

@Component
@Transactional
class DiscountServiceBean implements DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public Discount applyDiscount(ApplyDiscountCommand command) {
        return discountRepository.save(new DiscountEntity()
            .setClientId(command.getClientId())
            .setRateInPercent(command.getRateInPercent())
            .setEffectiveFrom(command.getEffectiveFrom())
            .setEffectiveTo(command.getEffectiveTo())
            .setMaxTimesToApply(command.getMaxTimesToApply())).toValueObject();
    }

    @Override
    public Optional<Discount> findDiscount(Long clientId, LocalDate when) {
        return Optional.ofNullable(queryFactory.selectFrom(discount)
            .where(discount.clientId.eq(clientId)
                .and(discount.effectiveFrom.loe(when))
                .and(discount.effectiveTo.goe(when))
                .and(discount.maxTimesToApply.gt(JPAExpressions.select(loan.count()).from(loan).where(loan.discountId.eq(discount.id))))
            )
            .orderBy(discount.rateInPercent.desc(), discount.effectiveTo.asc(), discount.id.asc())
            .limit(1)
            .fetchOne()).map(DiscountEntity::toValueObject);
    }

    @Override
    public Discount get(Long discountId) {
        return discountRepository.getRequired(discountId).toValueObject();
    }
}
