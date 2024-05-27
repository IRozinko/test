package fintech.instantor

import com.google.common.collect.ImmutableList
import fintech.instantor.json.insight.InstantorInsightResponse
import fintech.instantor.parser.InstantorDataResolver

import static fintech.PojoUtils.npeSafe

class TestingInsightInstantorDataResolver implements InstantorDataResolver<InstantorInsightResponse> {

    @Override
    String resolveName(InstantorInsightResponse json) {
        String name = resolveMistEntryValue(json, "name").orElse("")
        return name
    }

    @Override
    String resolveName(InstantorInsightResponse json, String bankAccountNumber) {
        String name = resolveMistEntryValue(json, "name").orElse("")
        return name
    }

    @Override
    String resolvePersonalNumber(InstantorInsightResponse json) {
        return npeSafe { json.getUserDetails().getPersonalIdentifier() }.orElse(ImmutableList.of())
            .stream()
            .filter { i -> (i.getName() == "dni") }
            .map { it.getValue() }
            .findAny().orElse(null)
    }

    @Override
    boolean resolveIsFakeType(InstantorInsightResponse json) {
        return false
    }

    @Override
    Long resolveClientId(InstantorInsightResponse json) {
        return resolveMistEntryValue(json, "clientId").map({ Long.valueOf(it) }).orElse(null)
    }

    @Override
    String primaryBankAccount(Long clientId) {
        return null
    }

    private static Optional<String> resolveMistEntryValue(InstantorInsightResponse json, String key) {
        return npeSafe({ json.getMiscParams() }).orElse(ImmutableList.of()).stream()
            .filter({ p -> key.equalsIgnoreCase(p.getName()) }).map({ it.getValue() }).findFirst()
    }
}
