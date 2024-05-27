package fintech.bo.api.server.security;

import com.google.common.base.MoreObjects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class BackofficeUser extends User {

    private final Long userId;
    private final String ipAddress;

    public BackofficeUser(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities, String ipAddress) {
        super(username, password, authorities);
        this.userId = userId;
        this.ipAddress = ipAddress;
    }

    public Long getUserId() {
        return userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .add("email", getUsername())
                .toString();
    }
}
