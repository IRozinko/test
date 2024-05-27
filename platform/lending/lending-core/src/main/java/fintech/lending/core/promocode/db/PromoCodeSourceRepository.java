package fintech.lending.core.promocode.db;

import fintech.db.BaseRepository;

import java.util.List;

public interface PromoCodeSourceRepository extends BaseRepository<PromoCodeSourceEntity, Long> {

    List<PromoCodeSourceEntity> getAllByPromoCodeId(Long promoCodeId);

    void deleteAllByPromoCodeId(Long promoCodeId);

}
