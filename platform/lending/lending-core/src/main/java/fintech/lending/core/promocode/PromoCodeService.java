package fintech.lending.core.promocode;

import fintech.lending.core.promocode.db.PromoCode;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Optional;

@Validated
public interface PromoCodeService {

    Long create(CreatePromoCodeCommand command);

    void update(UpdatePromoCodeCommand command);

    void updateClients(Long promoCodeId, Collection<String> clientNumbers);

    void activate(Long promoCodeId);

    void deactivate(Long promoCodeId);

    Optional<PromoCodeOffer> getPromoCodeOffer(String promoCode, Long clientId);

    Optional<PromoCodeOffer> getPromoCodeOffer(String promoCode, Long clientId, String affiliateId);

    PromoCodeOffer getRequired(Long promoCodeId);

    PromoCode getRequiredEntity(Long promoCodeId);

    void delete(Long promoCodeId);

}
