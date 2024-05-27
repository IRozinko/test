package fintech.bo.api.server.security;

import fintech.security.user.User;
import fintech.security.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Slf4j
@Component
@Transactional
public class BackofficeUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("User not found by email %s", email)));
        List<String> roles = new ArrayList<>(user.getPermissions());
        roles.add(BackoficeApiSecurityConfiguration.AUTHORITY_BACKOFFICE);
        List<GrantedAuthority> grantedAuthorities = createAuthorityList(roles.toArray(new String[roles.size()]));
        String ipAddress = WebRequestUtils.resolveIpAddress();
        return new BackofficeUser(user.getId(), user.getEmail(), user.getPassword(), grantedAuthorities, ipAddress);
    }
}
