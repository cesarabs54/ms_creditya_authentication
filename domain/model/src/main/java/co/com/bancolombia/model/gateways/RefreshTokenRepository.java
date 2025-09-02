package co.com.bancolombia.model.gateways;

import co.com.bancolombia.model.entities.RefreshToken;
import co.com.bancolombia.model.entities.User;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepository {

    Mono<RefreshToken> findByToken(String token);

    Mono<RefreshToken> save(RefreshToken token);

    Mono<Void> delete(RefreshToken token);

    Mono<Integer> deleteByUser(User user);

}
