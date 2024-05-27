package fintech.instantor.parser;

import fintech.instantor.json.InstantorResponseJson;

public interface InstantorDataResolver<T extends InstantorResponseJson> {

    String resolveName(T json);

    String resolveName(T json, String bankAccountNumber);

    String resolvePersonalNumber(T json);

    boolean resolveIsFakeType(T json);

    Long resolveClientId(T json);

    String primaryBankAccount(Long clientId);
}
