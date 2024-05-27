package fintech.spain.alfa.web.config.security;

import com.google.common.base.MoreObjects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AnonymousApiUser extends User {

    private final String ipAddress;

    public AnonymousApiUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String ipAddress) {
        super(username, password, authorities);
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("email", getUsername())
            .toString();
    }
}
