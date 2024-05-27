package fintech.strategy.db;


import fintech.db.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CalculationStrategyRepository extends BaseRepository<CalculationStrategyEntity, Long> {

    @Modifying
    @Query(value = "update strategy.calculation_strategy set is_default = null where strategy_type = ?1 and is_default = true", nativeQuery = true)
    void resetDefault(String strategyType);

    @Query(value = "from CalculationStrategyEntity as cse where cse.strategyType = ?1 and cse.calculationType = ?2 and cse.version = ?3  and cse.enabled = ?4")
    Optional<CalculationStrategyEntity> getStrategy(String strategyType, String calculationType, String version, boolean enabled);
}
