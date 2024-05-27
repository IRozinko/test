package fintech.spain.alfa.product.extension.discounts;

import fintech.spain.alfa.product.extension.discounts.db.ExtensionDiscountEntity;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;


@Validated
public interface ExtensionDiscountService {

    Long createExtensionDiscount(CreateExtensionDiscountCommand command);

    void activateExtensionDiscount(Long extensionDiscountId);

    void deactivateExtensionDiscount(Long extensionDiscountId);

    Optional<ExtensionDiscountEntity> getExtensionDiscount(Long loanId);

    ExtensionDiscountOffer findExtensionDiscount(Long loanId);

    void deleteExtensionDiscount(Long extensionDiscountId);
}
