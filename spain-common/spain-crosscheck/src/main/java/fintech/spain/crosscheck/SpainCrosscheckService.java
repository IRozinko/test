package fintech.spain.crosscheck;

import fintech.spain.crosscheck.model.SpainCrosscheckRequestCommand;
import fintech.spain.crosscheck.model.SpainCrosscheckResult;

public interface SpainCrosscheckService {

    SpainCrosscheckResult requestCrossCheck(SpainCrosscheckRequestCommand command);

    SpainCrosscheckResult get(Long id);
}
