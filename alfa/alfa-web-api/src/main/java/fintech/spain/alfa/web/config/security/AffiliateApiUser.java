package fintech.spain.alfa.web.config.security;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@ToString(of = "affiliateName")
public class AffiliateApiUser extends User {

    private final String affiliateName;

    public AffiliateApiUser(String affiliateName, String password, Collection<? extends GrantedAuthority> authorities) {
        super(affiliateName, password, authorities);
        this.affiliateName = affiliateName;
    }
}
