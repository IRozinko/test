package fintech.spain.alfa.product.web.db;

import fintech.db.BaseRepository;
import fintech.spain.alfa.product.web.model.PopupResolution;
import fintech.spain.alfa.product.web.model.PopupType;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PopupRepository extends BaseRepository<PopupEntity, Long> {

    @Query("from PopupEntity where clientId = ?1 and resolution = 'NONE' and (validUntil >= ?2 or validUntil is null)")
    List<PopupEntity> findActual(long clientId, LocalDateTime validUntil);

    List<PopupEntity> findByClientIdAndTypeAndResolution(long clientId, PopupType type, PopupResolution resolution);

}
