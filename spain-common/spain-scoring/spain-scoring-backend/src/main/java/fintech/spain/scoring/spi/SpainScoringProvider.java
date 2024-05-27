package fintech.spain.scoring.spi;

import fintech.spain.scoring.model.ScoringRequestCommand;

public interface SpainScoringProvider {

    ScoringResponse request(ScoringRequestCommand command);
}
