package fintech.instantor.parser;

import fintech.instantor.db.InstantorResponseEntity;
import fintech.instantor.model.SaveInstantorResponseCommand;

import java.util.Map;

public interface InstantorParser {

    InstantorResponseEntity parseResponse(SaveInstantorResponseCommand command);


    Map<String, String> parseAccountAttributes(InstantorResponseEntity response, String iban);

    String getNameForVerification(InstantorResponseEntity response, String iban);
}
