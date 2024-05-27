package fintech.spain.alfa.web.config.security;

import com.google.common.base.MoreObjects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class InternalApiUser extends User {

    private final String name;

    public InternalApiUser(String name, String apiKey, Collection<? extends GrantedAuthority> authorities) {
        super(name, apiKey, authorities);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .toString();
    }
}
