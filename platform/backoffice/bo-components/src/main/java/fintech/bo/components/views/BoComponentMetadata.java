package fintech.bo.components.views;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import fintech.bo.components.security.LoginService;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class BoComponentMetadata {

    private String caption;
    private Set<String> scopes = new HashSet<>();
    private Set<String> userRoles = new HashSet<>();
    private Set<String> features = new HashSet<>();

    public BoComponentMetadata(String caption) {
        this.caption = caption;
    }

    public BoComponentMetadata() {
    }

    public BoComponentMetadata withScope(String scope) {
        this.scopes.add(scope);
        return this;
    }

    public BoComponentMetadata withFeature(String feature) {
        this.features.add(feature);
        return this;
    }

    public BoComponentMetadata withUserRole(String role) {
        this.userRoles.add(role);
        return this;
    }

    public boolean matches(BoComponentMetadata metadata) {
        boolean scopeMatches = this.scopes.containsAll(metadata.getScopes());
        boolean featuresMatches = this.features.containsAll(metadata.getFeatures());
        ImmutableSet<String> currentRoles = ImmutableSet.copyOf(LoginService.getLoginData().getRoles().stream().filter(Objects::nonNull).collect(Collectors.toList()));
        boolean hasUserRole = this.userRoles.isEmpty() || !Sets.intersection(this.userRoles, currentRoles).isEmpty();
        return scopeMatches && featuresMatches && hasUserRole;
    }
}
