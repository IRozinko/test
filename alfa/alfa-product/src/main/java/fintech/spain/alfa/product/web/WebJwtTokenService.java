package fintech.spain.alfa.product.web;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Date.from;

@Component
public class WebJwtTokenService {

    public static final String HEADER_AUDIT_USER = "audit_user";
    public static final String HEADER_ROLE = "role";

    public static final String CLAIM_AFFILIATE_ID = "affiliateId";

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private final String secret;

    public WebJwtTokenService(@Value("${web.jwt.tokenSecret:12zH4Nv2GF5qC4aFN}") String secret) {
        this.secret = secret;
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
    }

    public static String userName(Jws<Claims> jwt) {
        return jwt.getBody().getSubject();
    }

    public static String auditUser(Jws<Claims> jwt) {
        return (String) jwt.getHeader().getOrDefault(HEADER_AUDIT_USER, "unknown");
    }

    public static String role(Jws<Claims> jwt) {
        return (String) jwt.getHeader().getOrDefault(HEADER_ROLE, "");
    }

    public TokenBuilder tokenBuilder(String userName, String auditUser, String role, Instant expiration) {
        return new TokenBuilder(userName, auditUser, role, expiration);
    }

    public class TokenBuilder {

        private final String userName;
        private final Instant expiration;
        private final Map<String, Object> headers = new HashMap<>();
        private final Map<String, Object> claims = new HashMap<>();

        public TokenBuilder(String userName, String auditUser, String role, Instant expiration) {
            this.userName = userName;
            this.expiration = expiration;
            this.headers.put(HEADER_AUDIT_USER, auditUser);
            this.headers.put(HEADER_ROLE, role);
        }

        public TokenBuilder withHeader(String name, Object value) {
            headers.put(name, value);
            return this;
        }

        public TokenBuilder withClaim(String name, Object value) {
            claims.put(name, value);
            return this;
        }

        public String build() {
            JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(this.userName)
                .setIssuedAt(new Date())
                .setExpiration(from(this.expiration));

            headers.forEach(jwtBuilder::setHeaderParam);
            claims.forEach(jwtBuilder::claim);

            return jwtBuilder.signWith(SIGNATURE_ALGORITHM, secret).compact();
        }

    }

}
