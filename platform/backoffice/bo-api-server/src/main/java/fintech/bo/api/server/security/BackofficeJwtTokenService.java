package fintech.bo.api.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

import static java.util.Date.from;

@Component
public class BackofficeJwtTokenService {

    private static SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private final String secret;

    public BackofficeJwtTokenService(@Value("${backoffice.jwt.tokenSecret:SdntbKeH2NvkGF5qC4aVN}") String jwtTokenSecret) {
        this.secret = jwtTokenSecret;
    }

    public String generate(String userName, Instant expiration) {
        return Jwts.builder()
            .setSubject(userName)
            .setIssuedAt(new Date())
            .setExpiration(from(expiration))
            .signWith(SIGNATURE_ALGORITHM, secret)
            .compact();
    }

    public String getUsername(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        return claims.getBody().getSubject();
    }
}
