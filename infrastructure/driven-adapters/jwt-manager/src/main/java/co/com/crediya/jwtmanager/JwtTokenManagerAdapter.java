package co.com.crediya.jwtmanager;

import co.com.crediya.model.token.TokenDetails;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.service.TokenManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenManagerAdapter implements TokenManager {

    private final JwtProperties properties;

    @Override
    public Mono<String> generate(User user) {
        return Mono.fromCallable(() -> {
            Instant now = Instant.now();
            Instant expiration = now.plus(properties.getExpirationInMinutes(), ChronoUnit.MINUTES);
            SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getSecret()));

            Map<String, Object> claims = Map.of(
                    "userId", user.getIdUser(),
                    "rol", (user.getRol() != null && user.getRol().getName() != null) ? user.getRol().getName() : ""
            );

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getEmail())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiration))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        });
    }

    @Override
    public Mono<TokenDetails> validate(String token) {
        return Mono.fromCallable(() -> {
            SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getSecret()));

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return TokenDetails.builder()
                    .email(claims.getSubject())
                    .userId(claims.get("userId", String.class))
                    .role(claims.get("rol", String.class))
                    .build();

        }).onErrorResume(e -> Mono.empty());
    }


}
