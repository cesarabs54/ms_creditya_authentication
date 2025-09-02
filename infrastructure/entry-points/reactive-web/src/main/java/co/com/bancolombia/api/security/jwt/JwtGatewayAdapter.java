package co.com.bancolombia.api.security.jwt;

import co.com.bancolombia.api.security.service.SecurityUserDetailsService;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.model.gateways.JwtGateway;
import co.com.bancolombia.usecase.api.security.RefreshTokenUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGatewayAdapter implements JwtGateway {

    private final SecurityUserDetailsService securityUserDetailsService;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Value("${security.jwt.jwtSecret}")
    private String jwtSecret;

    @Value("${security.jwt.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${security.jwt.jwtRefreshExpirationMs}")
    private long jwtRefreshExpirationMs;


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public Mono<String> generateToken(String username) {
        return securityUserDetailsService.findByUsername(username)
                .flatMap(userDetails -> Mono.fromSupplier(() -> {
                    var oMapper = new ObjectMapper();
                    var claims = oMapper.convertValue(userDetails, HashMap.class);
                    return Jwts.builder()
                            .claims(claims)
                            .subject(userDetails.getUsername())
                            .issuedAt(new Date())
                            .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                            .signWith(getSigningKey())
                            .compact();
                }));
    }

    @Override
    public Mono<String> getUsernameFromToken(String token) {
        return Mono.fromSupplier(() ->
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }

    @Override
    public Mono<String> generateAccessToken(User user) {
        return Mono.fromSupplier(() -> {
            var now = new Date();
            var expiryDate = new Date(now.getTime() + jwtExpirationMs);
            return Jwts.builder()
                    .subject(user.getDocumentIdentification())
                    .claim("userId", user.getUserId())
                    .claim("roles", user.getRolesAsString())
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        });
    }

    @Override
    public Mono<String> generateRefreshToken(User user) {
        return Mono.fromSupplier(() -> {
            var now = new Date();
            var expiryDate = new Date(now.getTime() + jwtRefreshExpirationMs);
            return Jwts.builder()
                    .subject(user.getDocumentIdentification())
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        });
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromSupplier(() -> {
            try {
                var key = getSigningKey();
                Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
                return true;
            } catch (SignatureException e) {
                log.error("Firma JWT inválida: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                log.error("Token JWT mal formado: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                log.error("Token JWT expirado: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.error("Token JWT no soportado: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                log.error("JWT claims vacíos: {}", e.getMessage());
            }
            return false;
        });
    }

    @Override
    public Mono<Void> invalidateRefreshTokensForUser(String username) {
        return refreshTokenUseCase.deleteByUsername(username);
    }
}
