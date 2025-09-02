package co.com.bancolombia.r2dbc.repositories;

import co.com.bancolombia.r2dbc.entities.RefreshTokenData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RefreshTokenDataRepository extends
        R2dbcRepository<RefreshTokenData, String> {

    Mono<RefreshTokenData> findByToken(String token);

    Mono<Integer> deleteByUserId(UUID userId);
}
