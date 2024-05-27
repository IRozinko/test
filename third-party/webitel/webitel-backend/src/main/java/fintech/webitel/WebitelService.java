package fintech.webitel;

import fintech.webitel.model.WebitelLoginCommand;
import fintech.webitel.model.WebitelCallCommand;
import fintech.webitel.model.WebitelCallResult;
import fintech.webitel.model.WebitelAuthToken;

public interface WebitelService {

    WebitelCallResult originateNewCall(WebitelCallCommand command);

    WebitelAuthToken authenticate(WebitelLoginCommand command);
}
