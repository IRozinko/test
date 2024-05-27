package fintech.webitel;

import fintech.webitel.model.WebitelAuthToken;
import fintech.webitel.model.WebitelCallResult;
import fintech.webitel.model.WebitelLoginCommand;
import fintech.webitel.model.WebitelCallCommand;

public interface WebitelProvider {

    WebitelCallResult originateNewCall(WebitelCallCommand command);

    WebitelAuthToken authenticate(WebitelLoginCommand command);
}

