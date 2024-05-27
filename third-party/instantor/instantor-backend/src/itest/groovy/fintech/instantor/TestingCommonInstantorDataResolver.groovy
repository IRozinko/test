package fintech.instantor

import com.google.common.collect.ImmutableList
import fintech.PojoUtils
import fintech.instantor.json.common.InstantorCommonResponse
import fintech.instantor.parser.InstantorDataResolver

@Deprecated
class TestingCommonInstantorDataResolver implements InstantorDataResolver<InstantorCommonResponse> {

    @Override
    String resolveName(InstantorCommonResponse json) {
        String name = resolveMistEntryValue(json, "name").orElse("")
        return name
    }

    @Override
    String resolveName(InstantorCommonResponse json, String bankAccountNumber) {
        String name = resolveMistEntryValue(json, "name").orElse("")
        return name
    }

    @Override
    String resolvePersonalNumber(InstantorCommonResponse json) {
        String name = resolveMistEntryValue(json, "dni").orElse("")
        return name
    }

    @Override
    boolean resolveIsFakeType(InstantorCommonResponse json) {
        return false
    }

    @Override
    Long resolveClientId(InstantorCommonResponse json) {
        return resolveMistEntryValue(json, "clientId").map({ Long.valueOf(it) }).orElse(null)
    }

    @Override
    String primaryBankAccount(Long clientId) {
        return null
    }

    private static Optional<String> resolveMistEntryValue(InstantorCommonResponse json, String key) {
        return PojoUtils.npeSafe({ json.getBasicInfo().getMiscEntryList() }).orElse(ImmutableList.of()).stream()
            .filter({ p -> key.equalsIgnoreCase(p.getKey()) }).map({ it.getValue() }).findFirst()
    }
}
