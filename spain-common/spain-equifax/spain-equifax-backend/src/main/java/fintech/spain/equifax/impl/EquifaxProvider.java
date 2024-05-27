package fintech.spain.equifax.impl;

import fintech.spain.equifax.model.EquifaxRequest;
import fintech.spain.equifax.model.EquifaxResponse;

import java.io.IOException;

public interface EquifaxProvider {

    EquifaxResponse request(EquifaxRequest request) throws IOException;

}
