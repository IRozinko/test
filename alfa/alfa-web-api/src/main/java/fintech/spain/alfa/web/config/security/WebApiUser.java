package fintech.spain.alfa.web.config.security;

import com.google.common.base.MoreObjects;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WebApiUser extends User {

    private final Long clientId;
    private String auditUser;
    private List<String> roles;

    public WebApiUser(Long clientId, String username, String auditUser, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.clientId = clientId;
        this.auditUser = auditUser;
        roles = authorities.stream().map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clientId", clientId)
                .add("email", getUsername())
                .add("auditUser", auditUser)
                .toString();
    }
}
