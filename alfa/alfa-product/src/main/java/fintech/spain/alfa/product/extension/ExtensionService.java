package fintech.spain.alfa.product.extension;

import fintech.strategy.model.ExtensionOffer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExtensionService {

    String EXTENSION_FEE_TYPE = "EXTENSION";

    List<ExtensionOffer> listOffersForLoan(Long loanId, LocalDate onDate);

    Optional<ExtensionOffer> findOfferForLoan(Long loanId, BigDecimal paymentAmount, boolean exactPriceMatch, LocalDate onDate);

    Long applyAndRepayExtensionFee(ApplyAndRepayExtensionFeeCommand command);

}
