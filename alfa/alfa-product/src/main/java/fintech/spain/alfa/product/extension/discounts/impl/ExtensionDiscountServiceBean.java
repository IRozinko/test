package fintech.spain.alfa.product.extension.discounts.impl;

import fintech.DateUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.lending.core.loan.db.LoanRepository;
import fintech.spain.alfa.product.extension.discounts.CreateExtensionDiscountCommand;
import fintech.spain.alfa.product.extension.discounts.ExtensionDiscountOffer;
import fintech.spain.alfa.product.extension.discounts.ExtensionDiscountService;
import fintech.spain.alfa.product.extension.discounts.db.ExtensionDiscountEntity;
import fintech.spain.alfa.product.extension.discounts.db.ExtensionDiscountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static fintech.BigDecimalUtils.amount;
import static fintech.spain.alfa.product.db.Entities.extensionDiscount;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.exclusiveBetween;
import static org.apache.commons.lang3.Validate.isTrue;

@Component
@Transactional
@Slf4j
public class ExtensionDiscountServiceBean implements ExtensionDiscountService {

    @Autowired
    private ExtensionDiscountRepository extensionDiscountRepository;

    @Autowired
    private LoanRepository loanRepository;


    @Override
    public Long createExtensionDiscount(CreateExtensionDiscountCommand command) {
        extensionDiscountRepository.findFirst(extensionDiscount.loan.id.eq(command.getLoanId()).and(extensionDiscount.active.isTrue()), extensionDiscount.createdAt.asc())
            .ifPresent(pc -> {
                throw new IllegalArgumentException("Extension discount already exists");
            });
        isTrue(command.getEffectiveFrom().isEqual(command.getEffectiveTo())
            || command.getEffectiveFrom().isBefore(command.getEffectiveTo()), "Invalid effective date range");
        exclusiveBetween(BigDecimal.ZERO, BigDecimal.valueOf(100.001), command.getRateInPercent(), "Invalid discount rate");
        if (DateUtils.lt(command.getEffectiveTo(), LocalDate.now())) {
            throw new IllegalArgumentException("The discount can not be created with the 'Effective to' date to any date in the past");
        }

        ExtensionDiscountEntity discountEntity = new ExtensionDiscountEntity()
            .setEffectiveFrom(command.getEffectiveFrom())
            .setEffectiveTo(command.getEffectiveTo())
            .setRateInPercent(command.getRateInPercent())
            .setLoan(loanRepository.getRequired(command.getLoanId()))
            .setActive(true);

        ExtensionDiscountEntity extensionDiscount = extensionDiscountRepository.save(discountEntity);

        return extensionDiscount.getId();
    }

    @Override
    public void activateExtensionDiscount(Long extensionDiscountId) {
        ExtensionDiscountEntity extensionDiscountEntity = extensionDiscountRepository.getRequired(extensionDiscountId);
        if (extensionDiscountEntity.getEffectiveTo().isBefore(TimeMachine.today())) {
            throw new IllegalArgumentException("Cannot activate extension discount: Effective to date is in past");
        }
        extensionDiscountEntity.setActive(true);
        extensionDiscountRepository.save(extensionDiscountEntity);
    }

    @Override
    public void deactivateExtensionDiscount(Long extensionDiscountId) {
        ExtensionDiscountEntity extensionDiscountEntity = extensionDiscountRepository.getRequired(extensionDiscountId);
        extensionDiscountEntity.setActive(false);
        extensionDiscountRepository.save(extensionDiscountEntity);
    }

    @Override
    public Optional<ExtensionDiscountEntity> getExtensionDiscount(Long loanId) {
        Validate.notNull(loanId, "Null loanId");
        ExtensionDiscountEntity entity = extensionDiscountRepository.findOne(extensionDiscount.loan.id.eq(loanId).and(extensionDiscount.active.isTrue()));
        if (entity != null) {
            return Optional.of(entity.toValueObject());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public ExtensionDiscountOffer findExtensionDiscount(Long loanId) {
        Optional<ExtensionDiscountEntity> extensionDiscountEntity = getExtensionDiscount(loanId);
        if (extensionDiscountEntity.isPresent()) {
            ExtensionDiscountEntity entity = extensionDiscountEntity.get();
            return new ExtensionDiscountOffer()
                .setDiscountInPercent(entity.getRateInPercent())
                .setEffectiveFrom(entity.getEffectiveFrom())
                .setEffectiveTo(entity.getEffectiveTo());
        } else {
            return new ExtensionDiscountOffer().setDiscountInPercent(amount(0));
        }
    }

    @Override
    public void deleteExtensionDiscount(Long extensionDiscountId) {
        ExtensionDiscountEntity extensionDiscountEntity = extensionDiscountRepository.getRequired(extensionDiscountId);
        if (extensionDiscountEntity.isActive()) {
            throw new IllegalArgumentException("Cannot delete active extension discount");
        }
        extensionDiscountRepository.delete(extensionDiscountEntity);
    }

    // Every day at 1:00
    @Scheduled(cron = "0 0 1 * * *")
    public void expireExtensionDiscount() {
        List<ExtensionDiscountEntity> expiredExtensionDiscounts = extensionDiscountRepository.findAll(extensionDiscount.active.isTrue()
            .and(extensionDiscount.effectiveTo.before(TimeMachine.today())));

        if (!expiredExtensionDiscounts.isEmpty()) {
            expiredExtensionDiscounts.forEach(extensionDiscount -> deactivateExtensionDiscount(extensionDiscount.getId()));

            log.info("Deactivated expired Extension discount(s) {}", expiredExtensionDiscounts.stream()
                .collect(toList()));
        }
    }

}
