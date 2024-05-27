package fintech.spain.crosscheck.impl;

import fintech.spain.crosscheck.model.SpainCrosscheckInput;

public interface SpainCrosscheckProvider {

    SpainCrosscheckResponse request(SpainCrosscheckInput input);
}
