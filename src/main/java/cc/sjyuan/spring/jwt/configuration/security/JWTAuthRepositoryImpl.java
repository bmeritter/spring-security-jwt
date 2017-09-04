package cc.sjyuan.spring.jwt.configuration.security;

import cc.sjyuan.spring.jwt.repository.TokenAuthRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class JWTAuthRepositoryImpl implements TokenAuthRepository {

    @Value("${security.jwt.secret:_SEMS_JWT_SECRET_201708240805987}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-in-seconds}")
    private long expirationInSeconds;

    @Override
    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expirationInSeconds * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Override
    public String extractAuthorizedSubject(String jwtToken) {
        return Jwts.parser().setSigningKey(jwtSecret)
                .parseClaimsJws(jwtToken)
                .getBody().getSubject();
    }
}
