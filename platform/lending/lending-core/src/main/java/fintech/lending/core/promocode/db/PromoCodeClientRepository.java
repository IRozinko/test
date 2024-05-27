package fintech.lending.core.promocode.db;

import fintech.db.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PromoCodeClientRepository extends BaseRepository<PromoCodeClientEntity, Long> {

    boolean existsByClientNumberAndPromoCodeId(String clientNumber, Long promoCodeId);

    @Modifying
    @Query(value = "delete from PromoCodeClientEntity e where e.promoCodeId = ?1")
    void deleteByPromoCodeId(Long promoCodeId);

}
