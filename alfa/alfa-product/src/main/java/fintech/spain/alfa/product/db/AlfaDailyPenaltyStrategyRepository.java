package fintech.spain.alfa.product.db;


import fintech.db.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AlfaDailyPenaltyStrategyRepository extends BaseRepository<AlfaDailyPenaltyStrategyEntity, Long> {

    @Modifying
    @Query(value = "delete from alfa.alfa_daily_penalty_strategy where calculation_strategy_id = ?1", nativeQuery = true)
    void deleteByStrategyId(Long strategyId);
}
