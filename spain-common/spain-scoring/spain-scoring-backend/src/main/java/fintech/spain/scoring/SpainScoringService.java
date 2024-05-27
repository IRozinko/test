package fintech.spain.scoring;

import fintech.spain.scoring.model.ScoringQuery;
import fintech.spain.scoring.model.ScoringRequestCommand;
import fintech.spain.scoring.model.ScoringResult;

import java.util.Optional;

public interface SpainScoringService {

    Optional<ScoringResult> findLatest(ScoringQuery scoringQuery);

    ScoringResult requestScore(ScoringRequestCommand command);

    ScoringResult get(Long id);
}
