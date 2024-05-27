package fintech.spain.alfa.product.db;


import fintech.db.BaseRepository;

import java.util.List;

public interface AlfaDpdPenaltyStrategyPenaltyRepository extends BaseRepository<AlfaDpdPenaltyStrategyPenaltyEntity, Long> {

    List<AlfaDpdPenaltyStrategyPenaltyEntity> getAllByDpdPenaltyStrategyId(Long dpdPenaltyStrategyId);

    void deleteAllByDpdPenaltyStrategyId(Long dpdPenaltyStrategyId);

}
