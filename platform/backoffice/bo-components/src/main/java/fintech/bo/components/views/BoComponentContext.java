package fintech.bo.components.views;

import fintech.bo.api.model.permissions.BackofficePermissions;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Data
@Accessors(chain = true)
public class BoComponentContext {

    private Map<String, Long> scopes = new HashMap<>();
    private Set<String> features = new HashSet<>();
    private String[] permissionsForDeletedClients = new String[]{BackofficePermissions.ADMIN};

    public Optional<Long> scope(String scope) {
        return Optional.ofNullable(this.scopes.get(scope));
    }

    public boolean inScope(String scope) {
        return this.scope(scope).isPresent();
    }

    public boolean requiresFeature(String feature) {
        return this.features.contains(feature);
    }

    public BoComponentContext withScope(String scope, Long id) {
        this.scopes.put(scope, id);
        return this;
    }

    public BoComponentContext withFeature(String feature) {
        this.features.add(feature);
        return this;
    }
}
